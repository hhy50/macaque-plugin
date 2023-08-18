package six.eared.macaque.plugin.idea.api;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LocalApiImpl extends ServerApi {
    private static String AGENT_PATH;

    private final DefaultAttachFactory defaultAttachFactory = new DefaultAttachFactory();

    static {
        URL resource = LocalApiImpl.class.getClassLoader().getResource("lib/macaque-agent.jar");
        if (resource != null) {
            try {
                String tmp = System.getProperty("java.io.tmpdir") + File.separator + "macaque-agent.jar";
                FileUtil.copy(resource.openStream(), new FileOutputStream(tmp));
                AGENT_PATH = tmp;
            } catch (IOException e) {
                Notify.error(e.getMessage());
            }
        }
        if (AGENT_PATH == null) {
            // TODO 从网络下载
            AGENT_PATH = "";
        }
    }

    protected boolean attach(Integer pid) {
        Attach runtimeAttach
                = this.defaultAttachFactory.createRuntimeAttach(String.valueOf(pid));
        Integer agentPort = PortNumberGenerator.getPort(pid);
        String property = "port=" + agentPort + ",debug=true";
        return runtimeAttach.attach(AGENT_PATH, property);
    }

    /**
     * 替换包
     */
    public void doRedefine(Settings settings, File file, String pid) {
        if (attach(Integer.parseInt(pid))) {
            Notify.info(String.format("attach '%s' succeed", pid));
        } else {
            Notify.error("attach failed");
        }
    }

    @Override
    public void setJPSList(Project project, JpsHolder instance) {
        try {
            Settings settings = project.getService(Settings.class);
            String processFilter = null;
            if (settings != null) {
                processFilter = settings.getState().processFilter;
            }
            JavaProcessHolder.refresh();
            List<Map<String, String>> javaProcess = new ArrayList<>();
            List<Pair<String, String>> pairs = JavaProcessHolder.getJavaProcess();
            if (pairs != null) {
                for (six.eared.macaque.common.util.Pair<String, String> item : pairs) {
                    Map<String, String> map = new HashMap<>();
                    map.put("pid", item.getFirst());
                    map.put("process", item.getSecond());
                    if (StringUtils.isNotBlank(processFilter) && item.getSecond() != null) {
                        if (!item.getSecond().toUpperCase().contains(processFilter.toUpperCase())) {
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
            if (proList != null) {
                instance.getState().processList = proList;
            }
        } catch (Exception e) {
            Notify.error(StringUtils.isBlank(e.getMessage()) ? "Error" : e.getMessage());
        }
    }
}
