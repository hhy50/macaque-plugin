package six.eared.macaque.plugin.idea.api;

import com.intellij.openapi.util.io.FileUtil;
import org.apache.commons.lang.StringUtils;
import six.eared.macaque.client.attach.Attach;
import six.eared.macaque.client.attach.DefaultAttachFactory;
import six.eared.macaque.client.common.PortNumberGenerator;
import six.eared.macaque.client.process.JavaProcessHolder;
import six.eared.macaque.plugin.idea.jps.JpsHolder;
import six.eared.macaque.plugin.idea.notify.Notify;
import six.eared.macaque.plugin.idea.settings.Settings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LocalApiImpl extends ServerApi {

    private static final LocalApiImpl INSTANCE = new LocalApiImpl();

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

    private LocalApiImpl() {
        super(null, null);
    }

    public static LocalApiImpl getInstance() {
        return INSTANCE;
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
    public List<ProcessItem> getJavaProcess() {
        JavaProcessHolder.refresh();
        try {
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
