package com.fancyinnovations.strata.plugin;


import com.fancyinnovations.strata.plugin.tasks.StrataTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class StrataGradlePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getTasks().register("start", StrataTask.class);
    }

}
