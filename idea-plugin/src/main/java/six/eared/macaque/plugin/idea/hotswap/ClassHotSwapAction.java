package six.eared.macaque.plugin.idea.hotswap;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;
import six.eared.macaque.plugin.idea.builder.NomalProjectBuilder;
import six.eared.macaque.plugin.idea.builder.ProjectBuilder;
import six.eared.macaque.plugin.idea.http.interfaces.HotSwap;
import six.eared.macaque.plugin.idea.notify.Notify;
import six.eared.macaque.plugin.idea.settings.Settings;

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
            final DataContext dataContext = event.getDataContext();
            final Project project = event.getData(CommonDataKeys.PROJECT);

            // 获取右击的文件
            PsiFile psiFile = event.getDataContext().getData(CommonDataKeys.PSI_FILE);
            if (psiFile != null) {
                try {
                    // 优先 recompile, 失败再build整个项目
                    builder.buildScope(dataContext, project)
                            .onSuccess((result) -> {
                                requestRedefine(psiFile);
                            })
                            .onError((err) -> {
                                expansionCompile(project, psiFile);
                            });
                } catch (Exception e) {
                    Notify.error(e.getMessage());
                }
            }
        }
    }

    private void expansionCompile(Project project, PsiFile psiFile) {
        builder.buildAll(project)
                .onSuccess((compileResult) -> {
                    if (compileResult.hasErrors()) {
                        Notify.error("Compile project error");
                        return;
                    }
                    requestRedefine(psiFile);
                }).onError((err) -> {
                    Notify.error(err.getMessage());
                });
    }


    public void requestRedefine(PsiFile psiFile) {
        Project project = psiFile.getProject();
        Settings settings = Settings.getInstance(project);

        if (settings != null) {
            try {
                // TODO IDEA 文件刷新有延迟
                byte[] fileBytes = psiFile.getVirtualFile().contentsToByteArray();

                HotSwap hotSwap = new HotSwap(settings.getState().getUrl());
                hotSwap.setPid(pid);
                hotSwap.setFileType("java");
                hotSwap.setFileName(psiFile.getName());
                hotSwap.setFileData(fileBytes);

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
}
