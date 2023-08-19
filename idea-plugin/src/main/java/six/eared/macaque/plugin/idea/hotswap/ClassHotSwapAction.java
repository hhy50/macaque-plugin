package six.eared.macaque.plugin.idea.hotswap;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.task.ProjectTaskManager;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.concurrency.Promise;
import six.eared.macaque.plugin.idea.api.ServerApi;
import six.eared.macaque.plugin.idea.builder.CompileOptions;
import six.eared.macaque.plugin.idea.builder.CompilerPaths;
import six.eared.macaque.plugin.idea.builder.NomalProjectBuilder;
import six.eared.macaque.plugin.idea.builder.ProjectBuilder;
import six.eared.macaque.plugin.idea.notify.Notify;

import java.io.File;
import java.util.function.BiFunction;

public class ClassHotSwapAction extends AnAction {

    private ServerApi serverApi;

    private String pid;

    private final ProjectBuilder builder = new NomalProjectBuilder();

    public ClassHotSwapAction(ServerApi serverApi, String pid, String processName) {
        super(String.format("%s | %s", pid, processName));
        this.serverApi = serverApi;
        this.pid = pid;
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        // 获取右击的文件
        Project project = event.getProject();
        PsiFile psiFile = event.getDataContext().getData(CommonDataKeys.PSI_FILE);
        if (psiFile != null) {
            try {
                int option = Messages.showDialog("Whether to compile immediately?", "Choose Compile Option",
                        CompileOptions.OPTIONS, CompileOptions.DEFAULT, null);

                BiFunction<ProjectBuilder, PsiFile,
                        Promise<ProjectTaskManager.Result>> handler = CompileOptions.getOption(option);
                handler.apply(builder, psiFile)
                        .onSuccess(result -> {
                            if (!result.hasErrors()) {
                                int confirm = Messages.showYesNoCancelDialog(
                                        "This operation will replace the class already loaded in the target process",
                                        "Warning", null);
                                if (confirm == 0) {
                                    serverApi.doRedefine(getCompiledClassFile(psiFile), pid);
                                }
                            }
                        })
                        .onError(error -> {
                            Notify.error(error.getMessage());
                        });
            } catch (Exception e) {
                Notify.error(e.getMessage());
            }
        }

    }

    public File getCompiledClassFile(PsiFile psiFile) {
        final Module module = ModuleUtilCore.findModuleForFile(psiFile.getVirtualFile(),
                psiFile.getProject());

        String moduleOutputPath = CompilerPaths.getModuleOutputPath(module, false);
        if (StringUtils.isNotBlank(moduleOutputPath)) {
            VirtualFile[] sourceRootUrls = ModuleRootManager.getInstance(module).getSourceRoots();
            String javaFilePath = psiFile.getVirtualFile().getParent().getPath();

            String sourceRoot = null;
            for (VirtualFile root : sourceRootUrls) {
                if (javaFilePath.startsWith(root.getPath())) {
                    sourceRoot = root.getPath();
                }
            }
            if (sourceRoot != null) {
                String relative = javaFilePath.substring(sourceRoot.length());
                String classFileName = psiFile.getName().split("\\.")[0] + ".class";
                return new File(moduleOutputPath, relative + File.separator + classFileName);
            }
        }
        return null;
    }
}
