package six.eared.macaque.plugin.idea.builder;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.task.ProjectTaskManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.concurrency.AsyncPromise;
import org.jetbrains.concurrency.Promise;

import java.util.function.Consumer;

public interface ProjectBuilder {

    /**
     * rebuild
     */
    public Promise<ProjectTaskManager.Result> buildAll(Project project);

    default Promise<ProjectTaskManager.Result> buildScope(Module module) {
        if (module != null) {
            return ProjectTaskManager.getInstance(module.getProject()).rebuild(module);
        }

        // 这里应该快速失败
        return new CompileFailPromise();
    }


    class CompileFailPromise extends AsyncPromise<ProjectTaskManager.Result> {
        @Override
        public boolean isDone() {
            return true;
        }

        @NotNull
        @Override
        public AsyncPromise<ProjectTaskManager.Result> onSuccess(@NotNull Consumer<? super ProjectTaskManager.Result> handler) {
            return this;
        }

        @NotNull
        @Override
        public AsyncPromise<ProjectTaskManager.Result> onError(@NotNull Consumer<? super Throwable> rejected) {
            rejected.accept(new RuntimeException("Compile error"));
            return this;
        }
    }
}
