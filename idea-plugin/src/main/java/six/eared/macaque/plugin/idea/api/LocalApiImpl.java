package six.eared.macaque.plugin.idea.api;

import com.intellij.openapi.project.Project;
import com.intellij.util.PathUtil;
import org.apache.commons.lang.StringUtils;
import six.eared.macaque.client.attach.Attach;
import six.eared.macaque.client.attach.DefaultAttachFactory;
import six.eared.macaque.client.common.PortNumberGenerator;
import six.eared.macaque.client.process.JavaProcessHolder;
import six.eared.macaque.common.util.Pair;
import six.eared.macaque.plugin.idea.jps.JpsHolder;
import six.eared.macaque.plugin.idea.notify.Notify;
import six.eared.macaque.plugin.idea.settings.Settings;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LocalApiImpl extends ServerApi{
    private final DefaultAttachFactory defaultAttachFactory = new DefaultAttachFactory();
    private String findLibPath() {
        final Path path = Paths.get(PathUtil.getJarPathForClass(this.getClass()));
        if (path.endsWith("classes")) return path.resolve("../lib")
                .normalize()
                .toString();
        return Paths.get(PathUtil.getJarPathForClass(getClass()))
                .getParent()
                .toString();
    }
    protected boolean attach(Integer pid) {
        Attach runtimeAttach
                = this.defaultAttachFactory.createRuntimeAttach(String.valueOf(pid));

        Integer agentPort = PortNumberGenerator.getPort(pid);
        Notify.error("agentPort:"+agentPort);
        String property = "port="+agentPort+",debug=true";
        String libPath = findLibPath();
        Notify.error("libPath:"+libPath+File.separator+"macaque-agent-1.0.jar");
        return runtimeAttach.attach(libPath+File.separator+"macaque-agent-1.0.jar", property);
    }
    /**
     * 替换包
     */
    public void doRedefine(Settings settings, File file, String pid){
        if(attach(Integer.parseInt(pid))){
            Notify.error("attach succeed");
        }else{
            Notify.error("attach failed");
        }
    }
    @Override
    public void setJPSList(Project project, JpsHolder instance) {
        try{
            Settings settings = project.getService(Settings.class);
            String processFilter = null;
            if(settings!=null){
                processFilter = settings.getState().processFilter;
            }
            JavaProcessHolder.refresh();
            List<Map<String, String>> javaProcess = new ArrayList<>();
            List<Pair<String, String>> pairs = JavaProcessHolder.getJavaProcess();
            if(pairs!=null){
                for(six.eared.macaque.common.util.Pair<String, String> item:pairs){
                    Map<String, String> map = new HashMap<>();
                    map.put("pid", item.getFirst());
                    map.put("process", item.getSecond());
                    if(StringUtils.isNotBlank(processFilter)&&item.getSecond()!=null){
                        if(!item.getSecond().toUpperCase().contains(processFilter.toUpperCase())){
                            continue;
                        }
                    }
                    javaProcess.add(map);
                }
            }
            List proList = javaProcess.stream()
                    .map(item -> {
                        String process = item.get("process");

                        JpsHolder.ProcessItem processItem = new JpsHolder.ProcessItem();
                        processItem.pid = item.get("pid");
                        processItem.process = StringUtils.isBlank(process)
                                ? "Unknown"
                                : process.length() > JpsHolder.MAX_PROCESS_NAME_LENGTH ? process.substring(0, JpsHolder.MAX_PROCESS_NAME_LENGTH) : process;
                        return (JpsHolder.ProcessItem) processItem;
                    })
                    .collect(Collectors.toList());
            if(proList!=null){
                instance.getState().processList = proList;
            }
        }catch (Exception e){
            Notify.error(StringUtils.isBlank(e.getMessage()) ? "Error" : e.getMessage());
        }
    }
}
