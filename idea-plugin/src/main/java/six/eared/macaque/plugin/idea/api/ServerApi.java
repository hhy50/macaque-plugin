package six.eared.macaque.plugin.idea.api;

import com.intellij.openapi.project.Project;
import org.apache.commons.lang.StringUtils;
import six.eared.macaque.plugin.idea.settings.ServerConfig;
import six.eared.macaque.plugin.idea.settings.Settings;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 后端接口抽象类
 */
public abstract class ServerApi {

    protected Project project;

    protected String serverUnique;

    protected ServerApi(Project project, String serverUnique) {
        this.project = project;
        this.serverUnique = serverUnique;
    }

    protected List<ProcessItem> filterProcess(List<ProcessItem> processList) {
        ServerConfig serverConfig = Settings.getInstance(project).getState().getServerConfig(serverUnique);
        return processList.stream()
                .filter(item -> {
                    if (StringUtils.isNotBlank(serverConfig.pattern)) {
                        // TODO 通配符解析
                        return item.process.contains(serverConfig.pattern);
                    }
                    return true;
                }).collect(Collectors.toList());
    }

    /**
     * 替换包
     */
    public abstract void doRedefine(File file, String pid);

    /**
     * 获取jps进程信息
     *
     */
    public abstract List<ProcessItem> getJavaProcess();

    public static class ProcessItem {

        /**
         * 进程pid
         */
        public String pid;

        /**
         * 进程名
         */
        public String process;
    }
}
