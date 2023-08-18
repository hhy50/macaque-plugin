package six.eared.macaque.plugin.idea.api;

import com.intellij.openapi.project.Project;
import org.apache.commons.lang.StringUtils;
import six.eared.macaque.client.process.JavaProcessHolder;
import six.eared.macaque.common.util.Pair;
import six.eared.macaque.plugin.idea.jps.JpsHolder;
import six.eared.macaque.plugin.idea.notify.Notify;
import six.eared.macaque.plugin.idea.settings.Settings;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LocalApiImpl extends ServerApi{
    /**
     * 替换包
     */
    public void doRedefine(Settings settings, File file, String pid){
        Notify.error("doRedefine暂未实现");
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
