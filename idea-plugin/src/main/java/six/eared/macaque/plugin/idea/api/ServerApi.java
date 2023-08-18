package six.eared.macaque.plugin.idea.api;

import com.intellij.openapi.project.Project;
import six.eared.macaque.plugin.idea.jps.JpsHolder;
import six.eared.macaque.plugin.idea.notify.Notify;
import six.eared.macaque.plugin.idea.settings.Settings;

import java.io.File;

/**
 * 后端接口抽象类
 */
public abstract class ServerApi {
    /**
     * -1 未初始化
     * 0 远程模式
     * 1 本地模式
     */
    public static  int localMode = -1;
    public static ServerApi getAPI(Project project){
        if(localMode==-1){
            //初始化
            Settings settings = project.getService(Settings.class);
            if (settings == null) {
                Notify.error("Not configuration macaque server");
                return localApi;
            }
            localMode = settings.getState().mode;
        }
        if(localMode==0){
            return localApi;
        }else{
            if(remoteApi==null){
                remoteApi = new RemoteApiImpl();
            }
            return remoteApi;
        }
    }
    public  static ServerApi localApi = new LocalApiImpl();
    public  static ServerApi remoteApi = null;
    /**
     * 替换包
     */
    public abstract void doRedefine(Settings settings, File file, String pid);

    /**
     * 获取jps进程信息
     * @param project
     * @param instance
     */
    public abstract void setJPSList(Project project, JpsHolder instance);
}
