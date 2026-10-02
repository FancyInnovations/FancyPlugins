package com.fancyinnovations.fancyworlds.dialogs;

import com.fancyinnovations.fancydialogs.api.data.DialogButton;
import com.fancyinnovations.fancydialogs.api.data.inputs.DialogCheckbox;
import com.fancyinnovations.fancydialogs.api.data.inputs.DialogInputs;
import com.fancyinnovations.fancydialogs.api.data.inputs.DialogSelect;
import com.fancyinnovations.fancydialogs.api.data.inputs.DialogTextField;import com.fancyinnovations.fancyworlds.dialogs.WorldsDialogController.Choice;
import com.fancyinnovations.fancyworlds.dialogs.WorldsDialogController.Choice;
import com.fancyinnovations.fancyworlds.worlds.service.WorldCreationService;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/** World creation form and input validation. */
final class WorldCreationDialog {

    private static final Pattern VALID_SEED = Pattern.compile("-?[0-9]+");

    private final WorldsDialogController controller;

    WorldCreationDialog(WorldsDialogController controller) {
        this.controller = controller;
    }

    void openCreateForm(Player player, int page) {
        if (!controller.allowed(player, "world.menu") || !controller.allowed(player, "world.create")) return;

        List<DialogSelect.Entry> environments = Stream.of(World.Environment.values())
                .map(value -> new DialogSelect.Entry(value.name(), value.name(), value == World.Environment.NORMAL))
                .toList();

        List<DialogSelect.Entry> generators = List.of(
                new DialogSelect.Entry("default", "default", true), new DialogSelect.Entry("flat", "flat", false),
                new DialogSelect.Entry("amplified", "amplified", false), new DialogSelect.Entry("large_biomes", "large_biomes", false)
        );

        DialogInputs inputs = new DialogInputs(
                List.of(
                        new DialogTextField("name", controller.tr("create.name"), 1, "", 64, 1, Map.of(), 220),
                        new DialogTextField("seed", controller.tr("create.seed"), 2, "", 20, 1, Map.of(), 220)
                ),
                List.of(
                        new DialogSelect("environment", controller.tr("create.environment"), 3, environments, Map.of(), 220),
                        new DialogSelect("generator", controller.tr("create.generator"), 4, generators, Map.of(), 220)
                ),
                List.of(
                        new DialogCheckbox("structures", controller.tr("create.structures"), 5, false, Map.of()))
        );

        List<DialogButton> buttons = new ArrayList<>();
        Map<String, Choice> choices = new HashMap<>();
        controller.add(buttons, choices, controller.tr("create.submit"), "create_submit", "", page);
        controller.add(buttons, choices, controller.tr("common.back"), "world_page", String.valueOf(page), page);

        controller.show(player, controller.tr("create.title"), List.of(controller.line(controller.tr("create.description"))), inputs, buttons, choices);
    }

    void createWorld(Player player, int page, Map<String, String> inputs) {
        if (!controller.allowed(player, "world.create") || !controller.allowed(player, "world.menu")) return;

        String name = inputs.getOrDefault("name", "").trim();

        String seedText = inputs.getOrDefault("seed", "").trim();
        Long seed = null;
        if (!seedText.isEmpty()) {
            if (!VALID_SEED.matcher(seedText).matches()) {
                controller.send(player, "dialogs.create.invalid_seed");
                openCreateForm(player, page);
                return;
            }
            try {
                seed = Long.parseLong(seedText);
            } catch (NumberFormatException ex) {
                controller.send(player, "dialogs.create.invalid_seed");
                openCreateForm(player, page);
                return;
            }
        }

        World.Environment environment;
        try {
            environment = World.Environment.valueOf(inputs.getOrDefault("environment", "NORMAL"));
        } catch (IllegalArgumentException ex) {
            controller.send(player, "dialogs.create.invalid_option");
            openCreateForm(player, page);
            return;
        }

        String generator = inputs.getOrDefault("generator", "default");
        if (!List.of("default", "flat", "amplified", "large_biomes").contains(generator)) {
            controller.send(player, "dialogs.create.invalid_option");
            openCreateForm(player, page);
            return;
        }

        String structuresText = inputs.getOrDefault("structures", "false");
        if (!structuresText.equals("true") && !structuresText.equals("false")) {
            controller.send(player, "dialogs.create.invalid_option");
            openCreateForm(player, page);
            return;
        }

        controller.discard(player);

        WorldCreationService.Result result = WorldCreationService.create(name, seed, environment, generator, Boolean.parseBoolean(structuresText));
        String key = switch (result.status()) {
            case CREATED -> "commands.world.create.success";
            case INVALID_NAME -> "commands.world.create.invalid_name";
            case ALREADY_EXISTS -> "commands.world.create.already_exists";
            case DISK_EXISTS -> "commands.world.create.disk_exists";
            case FAILED -> "commands.world.create.failed";
        };

        controller.send(player, key, "worldName", name);

        if (result.status() == WorldCreationService.Status.CREATED) {
            controller.openWorldDetail(player, result.world().getID().toString(), page);
        } else {
            openCreateForm(player, page);
        }
    }
}
