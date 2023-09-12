package six.eared.macaque.plugin.idea.api;

import com.intellij.openapi.project.Project;
import org.apache.commons.lang.StringUtils;
import six.eared.macaque.common.ExtPropertyName;
import six.eared.macaque.common.util.FileUtil;
import six.eared.macaque.plugin.idea.settings.BetaConfig;
import six.eared.macaque.plugin.idea.settings.ServerConfig;
import six.eared.macaque.plugin.idea.settings.Settings;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public void redefineFile(String fileType, File file, String pid) {
        ClassLoader origin = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(ServerApi.class.getClassLoader());
        try {
            doRedefine(pid, file.getName(), fileType, FileUtil.readBytes(file.getPath()));
        } finally {
            Thread.currentThread().setContextClassLoader(origin);
        }
    }

    public void redefineClass(byte[] bytes, String pid) {
        ClassLoader origin = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(ServerApi.class.getClassLoader());
        try {
            doRedefine(pid, null, "class", bytes);
        } finally {
            Thread.currentThread().setContextClassLoader(origin);
        }
    }

    public Map<String, String> getExtProperties() {
        Map<String, String> extProp = new HashMap<>();
        Settings.State state = Settings.getInstance(project).getState();
        BetaConfig betaConfig = state.betaConfig;

        extProp.put(ExtPropertyName.MODE, state.getServerConfig(serverUnique).mode);
        extProp.put(ExtPropertyName.COMPATIBILITY_MODE, Boolean.toString(betaConfig.compatibilityMode));
        return extProp;
    }

    /**
     * 替换包
     */
    protected abstract void doRedefine(String pid, String fileName, String fileType, byte[] bytes);

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
