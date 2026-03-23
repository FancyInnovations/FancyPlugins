package com.fancyinnovations.strata.plugin.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class StrataTask extends DefaultTask {

    public StrataTask() {
        setGroup("strata");
        setDescription("Runs the Strata code generation process.");
    }

    @TaskAction
    public void runStrata() {
        System.out.println("Running Strata code generation...");
    }

}
