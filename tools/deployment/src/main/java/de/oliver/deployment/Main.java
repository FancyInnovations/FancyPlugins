package de.oliver.deployment;

import com.google.gson.Gson;
import de.oliver.deployment.git.GitService;
import de.oliver.deployment.hangar.HangarService;
import de.oliver.deployment.modrinth.ModrinthService;
import de.oliver.deployment.notification.DiscordWebhook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {

    private static final Gson GSON = new Gson();

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Usage: java -jar deployment.jar <platform> <config_file path> [send_notification]");
            System.exit(1);
        }

        String platform = args[0];

        String configFilePath = args[1];
        String configData = Files.readString(Path.of(configFilePath));
        Configuration configuration = GSON.fromJson(configData, Configuration.class);

        boolean sendNotification = args.length > 2 && Boolean.parseBoolean(args[2]);

        if (platform.equalsIgnoreCase("modrinth")) {
            deployToModrinth(configuration);
        } else if (platform.equalsIgnoreCase("hangar")) {
            deployToHangar(configuration);
        } else {
            System.err.println("Unknown platform: " + platform);
            System.exit(1);
        }

        if (sendNotification) {
            sendDiscordNotification(configuration);
            System.out.println("Deployment completed and notification sent.");
        } else {
            System.out.println("Deployment completed without sending notification.");
        }
    }

    private static void deployToModrinth(Configuration configuration) {
        String modrinthApiKey = System.getenv("MODRINTH_API_KEY");
        ModrinthService modrinthService = new ModrinthService(modrinthApiKey);
        try {
            modrinthService.deployPlugin(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void deployToHangar(Configuration configuration) {
        String hangarApiKey = System.getenv("HANGAR_API_KEY");
        HangarService hangarService = new HangarService(hangarApiKey);
        try {
            hangarService.deployPlugin(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendDiscordNotification(Configuration configuration) {
        String discordWebhookUrl = System.getenv("DISCORD_WEBHOOK_URL");
        if (discordWebhookUrl != null) {
            DiscordWebhook.Data data = new DiscordWebhook.Data("Deployment completed", List.of(
                    new DiscordWebhook.Data.Embed(
                            "New version of " + configuration.projectName(),
                            """
                                    **Version:** %s
                                    **Channel:** %s
                                    **Commit:** [%s](https://github.com/FancyMcPlugins/fancyplugins/commit/%s)
                                    **Download:** %s
                                    """.formatted(
                                    configuration.readVersion(),
                                    configuration.channel(),
                                    GitService.getCommitHash().substring(0, 7),
                                    GitService.getCommitHash(),
                                    "https://modrinth.com/plugin/" + configuration.projectName() + "/version/" + configuration.readVersion()),

                            0x00FF00
                    )
            ));
            DiscordWebhook discordWebhook = new DiscordWebhook();
            discordWebhook.sendWebhook(discordWebhookUrl, data);
        } else {
            System.out.println("Discord webhook URL not set. Skipping notification.");
        }
    }

}
