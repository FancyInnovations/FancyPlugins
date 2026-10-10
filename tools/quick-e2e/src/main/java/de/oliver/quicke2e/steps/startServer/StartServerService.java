package de.oliver.quicke2e.steps.startServer;

import de.oliver.quicke2e.config.Context;

public class StartServerService {

    public void startServer(Context context) {
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", context.serverFileName());
        processBuilder.directory(context.serverEnvPath().toFile());

        try {
            Process process = processBuilder.start();
            context.setServerProcess(process);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
