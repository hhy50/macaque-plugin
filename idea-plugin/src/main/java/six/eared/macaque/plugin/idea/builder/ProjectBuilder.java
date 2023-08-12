package six.eared.macaque.plugin.idea.builder;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.task.ProjectTaskManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.concurrency.AsyncPromise;
import org.jetbrains.concurrency.Promise;

import java.lang.reflect.Method;
import java.util.function.Consumer;

public interface ProjectBuilder {

    /**
     * rebuild
     */
    public Promise<ProjectTaskManager.Result> buildAll(Project project);

    default Promise<ProjectTaskManager.Result> buildScope(DataContext dataContext, Project project) {
        final Module module = dataContext.getData(LangDataKeys.MODULE_CONTEXT);
        if (module != null) {
            return ProjectTaskManager.getInstance(project).rebuild(module);
        }
        else {
            VirtualFile[] files = getCompilableFiles(project, dataContext.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY));
            if (files.length > 0) {
                return ProjectTaskManager.getInstance(project).compile(files);
            }
        }

        // 这里应该快速失败
        return new CompileFailPromise();
    }

    default VirtualFile[] getCompilableFiles(Project project, VirtualFile[] files) {
        try {
            Class<?> clazz = Class.forName("com.intellij.compiler.actions.CompileAction");
            Object compileAction = clazz.newInstance();

            Method getCompilableFiles = clazz.getDeclaredMethod("getCompilableFiles", Project.class, VirtualFile[].class);
            getCompilableFiles.setAccessible(true);
            return (VirtualFile[]) getCompilableFiles.invoke(compileAction);
        } catch (Exception e) {
            // 可能是 sdk版本不一致
        }
        return new VirtualFile[0];
    }

    class CompileFailPromise extends AsyncPromise<ProjectTaskManager.Result> {
        @Override
        public boolean isDone() {
            return true;
        }

        @NotNull
        @Override
        public AsyncPromise<ProjectTaskManager.Result> onError(@NotNull Consumer<? super Throwable> rejected) {
            rejected.accept(new RuntimeException("Compile error"));
            return this;
        }
    }
}
