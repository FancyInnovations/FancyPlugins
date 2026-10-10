package de.oliver.quicke2e;

import de.oliver.quicke2e.config.Configuration;
import de.oliver.quicke2e.config.Context;
import de.oliver.quicke2e.steps.copyFile.CopyFileService;
import de.oliver.quicke2e.steps.eula.EulaService;
import de.oliver.quicke2e.steps.gradle.GradleService;
import de.oliver.quicke2e.steps.ops.OPsService;
import de.oliver.quicke2e.steps.paper.PaperDownloadService;
import de.oliver.quicke2e.steps.startScript.StartScriptService;
import de.oliver.quicke2e.steps.startServer.StartServerService;
import org.jetbrains.annotations.NotNull;

public class Main {

    public static void main(String[] args) {
        Configuration config = new Configuration(
                "paper",
                "1.21.4",
                "latest",
                new String[]{},
                true,
                new String[]{"OliverHD"},
                "25565"
        );

        Context context = createContext(config);

        StartServerService startServer = new StartServerService();
        startServer.startServer(context);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (context.serverProcess().isAlive()) {
                context.serverProcess().destroy();
            }
        }));

        if (context.serverProcess() != null && context.serverProcess().isAlive()) {
            try {
                context.serverProcess().waitFor();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static @NotNull Context createContext(Configuration config) {
        Context context = new Context(config);

        PaperDownloadService paper = new PaperDownloadService();
        paper.downloadServerFile(context);

        EulaService eula = new EulaService();
        if (config.eula()) {
            eula.setEulaToTrue(String.format("%s/eula.txt", context.serverEnvPath().toString()));
        }

        OPsService ops = new OPsService();
        ops.makePlayersOP(context);

        StartScriptService startScript = new StartScriptService();
        startScript.writeStartScript(context);

        GradleService gradle = new GradleService();
//        gradle.runTask(":plugins:fancynpcs:shadowJar");
//        gradle.runTask(":plugins:fancyholograms:shadowJar");
//        gradle.runTask(":plugins:fancyvisuals:shadowJar");

        CopyFileService copyFile = new CopyFileService();
        copyFile.createDirectory(context, "plugins");
        copyFile.copyFile(context, "plugins/fancynpcs/build/libs", "FancyNpcs-.*\\.jar", "plugins/FancyNpcs.jar");
        copyFile.copyFile(context, "plugins/fancyholograms-v2/build/libs", "FancyHolograms-.*\\.jar", "plugins/FancyHolograms.jar");
        copyFile.copyFile(context, "plugins/fancyvisuals/build/libs", "FancyVisuals-.*\\.jar", "plugins/FancyVisuals.jar");
        return context;
    }

}
