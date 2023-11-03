package six.eared.macaque.plugin.idea.api;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import org.apache.commons.lang.StringUtils;
import six.eared.macaque.core.client.MacaqueClient;
import six.eared.macaque.core.jps.JavaProcessHolder;
import six.eared.macaque.mbean.rmi.HotSwapRmiData;
import six.eared.macaque.mbean.rmi.RmiResult;
import six.eared.macaque.plugin.idea.PluginInfo;
import six.eared.macaque.plugin.idea.jps.JpsHolder;
import six.eared.macaque.plugin.idea.notify.Notify;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LocalApiImpl extends ServerApi {

    private MacaqueClient macaqueClient;

    public LocalApiImpl(Project project, String serverUnique) {
        super(project, serverUnique);

        this.macaqueClient = new MacaqueClient();
        this.macaqueClient.setAgentPath(getAgentPath());
    }

    /**
     * 替换包
     */
    public void doRedefine(String pid, String fileName, String fileType, byte[] bytes) {
        if (JpsHolder.getInstance(project).getState().getProcess(serverUnique)
                .stream().noneMatch(item -> item.pid.equals(pid))) {
            Notify.error(String.format("pid '%s' does not exist", pid));
            return;
        }
        try {
            Map<String, String> property = getExtProperties();
            RmiResult result = macaqueClient.hotswap(Integer.valueOf(pid), new HotSwapRmiData(fileName, fileType, bytes, property));
            if (result.isSuccess()) {
                Notify.success();
            } else {
                Notify.error(result.getMessage());
            }
        } catch (Exception e) {
            String msg = e.getMessage() == null ? e.toString() : e.getMessage();
            Notify.error(msg);
        }
    }

    @Override
    public List<ProcessItem> getJavaProcess() {
        try {
            JavaProcessHolder.refresh();
            List<ProcessItem> processList = JavaProcessHolder.getJavaProcess().stream()
                    .map(item -> {
                        String process = item.getSecond();

                        ProcessItem processItem = new ProcessItem();
                        processItem.pid = item.getFirst();
                        processItem.process = StringUtils.isBlank(process)
                                ? "Unknown"
                                : process.length() > JpsHolder.MAX_PROCESS_NAME_LENGTH ? process.substring(0, JpsHolder.MAX_PROCESS_NAME_LENGTH) : process;
                        return processItem;
                    })
                    .collect(Collectors.toList());
            return filterProcess(processList);
        } catch (Exception e) {
            Notify.error(StringUtils.isBlank(e.getMessage()) ? "Error" : e.getMessage());
        }
        return Collections.EMPTY_LIST;
    }

    public static String getAgentPath() {
        URL resource = LocalApiImpl.class.getClassLoader().getResource("lib/macaque-agent.jar");
        if (resource != null) {
            try {
                File tmp = new File(System.getProperty("java.io.tmpdir")
                        + File.separator + "macaque-plugin"
                        + File.separator + PluginInfo.AGENT_VERSION
                        + File.separator + "macaque-agent.jar");
                if (!tmp.exists()) {
                    if (!tmp.getParentFile().exists()) {
                        tmp.getParentFile().mkdirs();
                    }
                    FileUtil.copy(resource.openStream(), new FileOutputStream(tmp));
                }
                return tmp.getPath();
            } catch (IOException e) {
                Notify.error(e.getMessage());
            }
        }
        return "";
    }
}
