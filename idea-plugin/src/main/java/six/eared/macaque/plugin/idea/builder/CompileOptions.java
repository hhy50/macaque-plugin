package six.eared.macaque.plugin.idea.builder;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.psi.PsiFile;
import com.intellij.task.ProjectTaskManager;
import org.jetbrains.concurrency.Promise;

import java.util.function.BiFunction;

public class CompileOptions {

    public final static String[] OPTIONS = {"Use Compiled", "Recompile", "Rebuild project"};

    public static final int DEFAULT = 1;

    public static BiFunction<ProjectBuilder, PsiFile, Promise<ProjectTaskManager.Result>> getOption(int option) {
        switch (option) {
            case 0: // Use Compiled
                return (builder, psiFile) -> {
                    return new ProjectBuilder.CompileSuccessPromise();
                };
            case 1: // recompile
                return (builder, psiFile) -> {
                    final Module module = ModuleUtilCore.findModuleForFile(psiFile.getVirtualFile(),
                            psiFile.getProject());
                    return builder.buildScope(module)
                            .onError(result -> {
                                builder.buildAll(module.getProject());
                            });
                };
            case 2: // Rebuild project
                return (builder, psiFile) -> {
                    return builder.buildAll(psiFile.getProject());
                };
        }
        return null;
    }
}
