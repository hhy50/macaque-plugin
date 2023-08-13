package six.eared.macaque.plugin.idea.hotswap;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.apache.commons.lang.StringUtils;
import six.eared.macaque.plugin.idea.builder.CompilerPaths;
import six.eared.macaque.plugin.idea.builder.NomalProjectBuilder;
import six.eared.macaque.plugin.idea.builder.ProjectBuilder;
import six.eared.macaque.plugin.idea.http.interfaces.HotSwap;
import six.eared.macaque.plugin.idea.notify.Notify;
import six.eared.macaque.plugin.idea.settings.Settings;

import java.io.File;

public class ClassHotSwapAction extends AnAction {

    private String pid;

    private final ProjectBuilder builder = new NomalProjectBuilder();

    public ClassHotSwapAction(String pid, String processName) {
        super(String.format("%s | %s", pid, processName));

        this.pid = pid;
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        int confirm = Messages.showYesNoCancelDialog(
                "This operation will replace the class already loaded in the target process",
                "Warning", null);
        if (confirm == 0) {
            // 获取右击的文件
            PsiFile psiFile = event.getDataContext().getData(CommonDataKeys.PSI_FILE);
            if (psiFile != null) {
                final Module module = ModuleUtilCore.findModuleForFile(psiFile.getVirtualFile(),
                        psiFile.getProject());
                try {
                    // 优先 recompile, 失败再build整个项目
                    builder.buildScope(module)
                            .onSuccess((compileResult) -> {
                                if (compileResult.hasErrors()) {
                                    compileFailHandler(module, psiFile);
                                } else {
                                    requestRedefine(getCompiledClassFile(module, psiFile), module.getProject());
                                }
                            })
                            .onError((err) -> {
                                compileFailHandler(module, psiFile);
                            });
                } catch (Exception e) {
                    Notify.error(e.getMessage());
                }
            }
        }
    }

    public void compileFailHandler(Module module, PsiFile psiFile) {
        if (Messages.showYesNoCancelDialog(
                "Incremental compilation module failed, do you want to build all module",
                "Warning", null) == Messages.YES) {
            expansionCompile(module, psiFile);
        } else {
            Notify.error("Compile project error");
        }
    }

    private void expansionCompile(Module module, PsiFile psiFile) {
        builder.buildAll(module.getProject())
                .onSuccess((compileResult) -> {
                    if (compileResult.hasErrors()) {
                        Notify.error("Compile project error");
                        return;
                    }
                    requestRedefine(getCompiledClassFile(module, psiFile), module.getProject());
                }).onError((err) -> {
                    Notify.error(err.getMessage());
                });
    }

    public void requestRedefine(File file, Project project) {
        Settings settings = Settings.getInstance(project);
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
                    }
                });
            } catch (Exception e) {
                Notify.error(e.getMessage());
            }
        }
    }

    public File getCompiledClassFile(Module module, PsiFile psiFile) {
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
        Notify.error("class file '" + psiFile.getName() + "' not found");
        return null;
    }
}
