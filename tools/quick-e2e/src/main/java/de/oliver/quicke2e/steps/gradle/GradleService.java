package de.oliver.quicke2e.steps.gradle;

public class GradleService {

    public void runTask(String task) {
        String[] taskCommand = task.split(" ");
        String[] command = new String[taskCommand.length + 1];
        command[0] = "./gradlew";
        System.arraycopy(taskCommand, 0, command, 1, taskCommand.length);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

        try {
            Process process = processBuilder.start();
            process.waitFor();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
