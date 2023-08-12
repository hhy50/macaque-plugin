package six.eared.macaque.plugin.idea.builder;


import com.intellij.openapi.project.Project;
import com.intellij.task.ProjectTaskManager;
import org.jetbrains.concurrency.Promise;

public class NomalProjectBuilder implements ProjectBuilder {

    @Override
    public Promise<ProjectTaskManager.Result> buildAll(Project project) {
        return ProjectTaskManager.getInstance(project)
                .rebuildAllModules();
    }
}
