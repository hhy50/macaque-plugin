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

    protected ServerConfig serverConfig;

    protected ServerApi(Project project, ServerConfig serverConfig) {
        this.project = project;
        this.serverConfig = serverConfig;
    }

    protected List<ProcessItem> filterProcess(List<ProcessItem> processList) {
        return processList.stream()
                .filter(item -> {
                    if (StringUtils.isNotBlank(serverConfig.pattern)) {
                        return item.process.matches(serverConfig.pattern)
                                || item.process.contains(serverConfig.pattern);
                    }
                    return true;
                }).collect(Collectors.toList());
    }

    /**
     * 替换包
     */
    public abstract void doRedefine(Settings settings, File file, String pid);

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
