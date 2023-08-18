package six.eared.macaque.plugin.idea.api;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import org.apache.commons.lang.StringUtils;
import six.eared.macaque.plugin.idea.http.interfaces.HotSwap;
import six.eared.macaque.plugin.idea.http.interfaces.Jps;
import six.eared.macaque.plugin.idea.jps.JpsHolder;
import six.eared.macaque.plugin.idea.notify.Notify;
import six.eared.macaque.plugin.idea.settings.Settings;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RemoteApiImpl extends ServerApi{
    private static final Map<Project, Jps> JPS_CACHE =
            new HashMap<>();
    /**
     * 替换包
     */
    public  void doRedefine(Settings settings, File file, String pid){
        if (settings != null) {
            try {
                HotSwap hotSwap = new HotSwap(settings.getState().getUrl());
                hotSwap.setPid(pid);
                hotSwap.setFileType("class");
                hotSwap.setFileName(file.getName());
                hotSwap.setFileData(FileUtil.loadFileBytes(file));

                hotSwap.execute((response) -> {
                    if (response.isSuccess()) {
                        Notify.success("success");
                    }else{
                        Notify.error("remote hot-swap failed.");
                    }
                });
            } catch (Exception e) {
                Notify.error(e.getMessage());
            }
        }
    }
    public void setJPSList(Project project, JpsHolder instance) {
        try {
            String processFilter = null;
            Jps jps = JPS_CACHE.get(project);
            if (jps == null) {
                Settings settings = project.getService(Settings.class);
                if (settings == null) {
                    Notify.error("Not configuration macaque server");
                    return;
                }
                processFilter = settings.getState().processFilter;
                jps = new Jps(settings.getState().getUrl());
                JPS_CACHE.put(project, jps);
            }

            jps.execute((response) -> {
                if (response.isSuccess()) {
                    List proList = response.getData().stream()
                            .map(item -> {
                                String process = item.getProcess();

                                JpsHolder.ProcessItem processItem = new JpsHolder.ProcessItem();
                                processItem.pid = item.getPid();
                                processItem.process = StringUtils.isBlank(process)
                                        ? "Unknown"
                                        : process.length() > JpsHolder.MAX_PROCESS_NAME_LENGTH ? process.substring(0, JpsHolder.MAX_PROCESS_NAME_LENGTH) : process;
                                return (JpsHolder.ProcessItem) processItem;
                            })
                            .collect(Collectors.toList());
                    if(proList!=null){
                        instance.getState().processList = proList;
                    }
                    Notify.success("Refresh success");
                }
            });
            if(StringUtils.isNotBlank(processFilter)&&instance.getState().processList!=null){
                List<JpsHolder.ProcessItem> toRemove = new ArrayList<>();
                for(JpsHolder.ProcessItem pi:instance.getState().processList){
                    if(pi.process==null||!pi.process.toUpperCase().contains(processFilter.toUpperCase())){
                        toRemove.add(pi);
                    }
                }
                instance.getState().processList.removeAll(toRemove);
            }
        } catch (Exception e) {
            Notify.error(StringUtils.isBlank(e.getMessage()) ? "Error" : e.getMessage());
        }
    }
}
