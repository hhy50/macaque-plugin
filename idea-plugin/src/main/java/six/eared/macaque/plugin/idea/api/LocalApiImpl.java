package six.eared.macaque.plugin.idea.api;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import org.apache.commons.lang.StringUtils;
import six.eared.macaque.client.attach.Attach;
import six.eared.macaque.client.attach.DefaultAttachFactory;
import six.eared.macaque.client.common.PortNumberGenerator;
import six.eared.macaque.client.jmx.JmxClient;
import six.eared.macaque.client.jmx.JmxClientResourceManager;
import six.eared.macaque.client.process.JavaProcessHolder;
import six.eared.macaque.mbean.MBean;
import six.eared.macaque.mbean.MBeanObjectName;
import six.eared.macaque.mbean.rmi.ClassHotSwapRmiData;
import six.eared.macaque.plugin.idea.jps.JpsHolder;
import six.eared.macaque.plugin.idea.notify.Notify;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class LocalApiImpl extends ServerApi {

    private static String AGENT_PATH;

    private static final TreeSet<Integer> ATTACH_HISTORY = new TreeSet<>();

    private final DefaultAttachFactory defaultAttachFactory = new DefaultAttachFactory();

    static {
        URL resource = LocalApiImpl.class.getClassLoader().getResource("lib/macaque-agent.jar");
        if (resource != null) {
            try {
                File tmp = new File(System.getProperty("java.io.tmpdir") + File.separator + "macaque-agent.jar");
                if (!tmp.exists()) {
                    FileUtil.copy(resource.openStream(), new FileOutputStream(tmp));
                }
                AGENT_PATH = tmp.getPath();
            } catch (IOException e) {
                Notify.error(e.getMessage());
            }
        }
        if (AGENT_PATH == null) {
            // TODO 从网络下载
            AGENT_PATH = "";
        }
    }

    public LocalApiImpl(Project project, String serverUnique) {
        super(project, serverUnique);
    }

    protected boolean attach(Integer pid) {
        if (ATTACH_HISTORY.contains(pid)) {
            // TODO pid重复的问题，最好根据进程启动时间判断
            return true;
        }

        Attach runtimeAttach = this.defaultAttachFactory.createRuntimeAttach(String.valueOf(pid));
        Integer agentPort = PortNumberGenerator.getPort(pid);
        String property = "port=" + agentPort + ",debug=true";

        if (runtimeAttach.attach(AGENT_PATH, property)) {
            ATTACH_HISTORY.add(pid);
            Notify.info(String.format("attach '%s' succeed", pid));
            return true;
        }
        Notify.error(String.format("attach '%s' failed", pid));
        return false;
    }

    /**
     * 替换包
     */
    public void doRedefine(File file, String pid) {
        if (JpsHolder.getInstance(project).getState().getProcess(serverUnique)
                .stream().noneMatch(item -> item.pid.equals(pid))) {
            Notify.error(String.format("pid '%s' does not exist", pid));
            return;
        }

        if (attach(Integer.parseInt(pid))) {
            JmxClient jmxClient = JmxClientResourceManager.getInstance()
                    .getResource(pid);
            if (jmxClient != null) {
                MBean<ClassHotSwapRmiData> hotSwapMBean = jmxClient.getMBean(MBeanObjectName.HOT_SWAP_MBEAN);
                try {
                    hotSwapMBean.process(new ClassHotSwapRmiData(file.getName(), "class", FileUtil.loadFileBytes(file)));
                    Notify.success();
                } catch (Exception e) {
                    Notify.error(e.getMessage());
                }
            }
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
}
