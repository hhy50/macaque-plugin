package six.eared.macaque.plugin.idea.api;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import org.apache.commons.lang.StringUtils;
import six.eared.macaque.plugin.idea.http.interfaces.HotSwap;
import six.eared.macaque.plugin.idea.http.interfaces.Jps;
import six.eared.macaque.plugin.idea.http.response.JpsResponse;
import six.eared.macaque.plugin.idea.jps.JpsHolder;
import six.eared.macaque.plugin.idea.notify.Notify;
import six.eared.macaque.plugin.idea.settings.ServerConfig;
import six.eared.macaque.plugin.idea.settings.Settings;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RemoteApiImpl extends ServerApi {

    private Jps jps;

    public RemoteApiImpl(Project project, ServerConfig serverConfig) {
        super(project, serverConfig);
        this.jps = new Jps(serverConfig.getUrl());
    }

    /**
     * 替换包
     */
    public void doRedefine(Settings settings, File file, String pid) {
        if (settings != null) {
            try {
                HotSwap hotSwap = new HotSwap(serverConfig.getUrl());
                hotSwap.setPid(pid);
                hotSwap.setFileType("class");
                hotSwap.setFileName(file.getName());
                hotSwap.setFileData(FileUtil.loadFileBytes(file));

                hotSwap.execute((response) -> {
                    if (response.isSuccess()) {
                        Notify.success("success");
                    } else {
                        Notify.error("remote hot-swap failed.");
                    }
                });
            } catch (Exception e) {
                Notify.error(e.getMessage());
            }
        }
    }

    @Override
    public List<ProcessItem> getJavaProcess() {
        try {
            JpsResponse response = jps.execute();
            if (response.isSuccess()) {
                List<ProcessItem> proList = response.getData().stream()
                        .map(item -> {
                            String process = item.getProcess();
                            ProcessItem processItem = new ProcessItem();
                            processItem.pid = item.getPid();
                            processItem.process = StringUtils.isBlank(process)
                                    ? "Unknown"
                                    : process.length() > JpsHolder.MAX_PROCESS_NAME_LENGTH ? process.substring(0, JpsHolder.MAX_PROCESS_NAME_LENGTH) : process;
                            return (ProcessItem) processItem;
                        })
                        .collect(Collectors.toList());
                Notify.success("Refresh success");
                return filterProcess(proList);
            }
        } catch (Exception e) {
            Notify.error(StringUtils.isBlank(e.getMessage()) ? "Error" : e.getMessage());
        }
        return Collections.EMPTY_LIST;
    }
}
