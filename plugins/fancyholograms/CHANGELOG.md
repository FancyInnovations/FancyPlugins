# FancyHolograms v3 — changes from v2

FancyHolograms v3 rewrites the plugin's commands, storage, and developer API. This is an overview of the initial v3 update for users upgrading from v2.

The main changes are:

- Rewritten commands with configurable translations, hologram selection, and more editing options
- JSON storage with folders for organizing holograms and automatic backups
- Traits for interactions, multiple pages, file content, and custom behavior
- Text opacity, block-state customization, and colored outlines for item and block holograms
- A new API with builders, a hologram registry, a visibility controller, and extensible traits

Text, item, and block holograms, MiniMessage and legacy formatting, PlaceholderAPI, MiniPlaceholders, and NPC linking remain available.

## How to update

### Requirements

- Java 25 or newer
- Paper or Folia on a supported Minecraft version: 1.21.7 - 26.3
- FancyNpcs if you use NPC linking or the interaction trait

Spigot is not supported. Support for development versions of Minecraft depends on the FancyHolograms build you use.

### Migration from v2

1. Stop the server and back up the entire `plugins/FancyHolograms/` folder and your v2 plugin jar
2. Check that plugins using the FancyHolograms API support v3, then replace the v2 jar with the v3 jar
3. Move your old `config.yml` and `featureFlags.yml` into your backup. Leave `holograms.yml` in `plugins/FancyHolograms/` so v3 can find it
4. Start the server. v3 generates its configuration and converts `holograms.yml` to JSON files under `plugins/FancyHolograms/data/holograms/migrated/`. The original file is renamed to `holograms-old.yml`
5. Reapply your configuration using the [new configuration paths](#new-configuration), update command permissions and scripts, and restart the server
6. Check your holograms, formatting, visibility, and NPC links, and confirm that the converted data has been saved

Hologram data is migrated automatically. Settings from `config.yml` and `featureFlags.yml` are **not** migrated automatically. Feature flags now belong in `config.yml`.

The v3 API is incompatible with the v2 API. Plugins built against v2 must be updated; see [New API](#new-api).

There is no automatic conversion back to v2. To downgrade, stop the server and restore your pre-upgrade plugin folder and compatible plugin jars. A v3 data backup does not replace that pre-upgrade backup.

## Commands and translations

### Command framework and migration

Commands now use the [Lamp command framework](https://github.com/Revxrsal/Lamp). Hologram commands use `/hologram`, administration commands use `/fancyholograms`, and trait configuration uses `/hologramtrait`.

In the examples below, `<argument>` is required and `[argument]` is optional.

Many edit commands now use underscores. Update scripts, command actions, and permission grants that use the v2 syntax.

| v2 command | v3 command |
| --- | --- |
| `/hologram create <type> <name>` | `/hologram create <name> [--type <type>]`; defaults to `TEXT` |
| `/hologram edit <hologram> movehere` or `position` | `/hologram edit <hologram> move_here` |
| `/hologram edit <hologram> moveto <x> <y> <z> [yaw] [pitch]` | `/hologram edit <hologram> move_to <x> <y> <z> [yaw] [pitch]` |
| `/hologram edit <hologram> rotate <angle>` | `/hologram edit <hologram> rotation yaw <angle>` |
| `/hologram edit <hologram> rotatepitch <angle>` | `/hologram edit <hologram> rotation pitch <angle>` |
| `/hologram edit <hologram> addline <text>` | `/hologram edit <hologram> add_line <text>` |
| `/hologram edit <hologram> setline <line> <text>` | `/hologram edit <hologram> set_line <line> <text>` |
| `/hologram edit <hologram> removeline <line>` | `/hologram edit <hologram> remove_line [line]` |
| `/hologram edit <hologram> insertbefore <line> <text>` | `/hologram edit <hologram> add_line_before <line> <text>` |
| `/hologram edit <hologram> insertafter <line> <text>` | `/hologram edit <hologram> add_line_after <line> <text>` |
| `/hologram edit <hologram> textshadow <boolean>` | `/hologram edit <hologram> text_shadow <boolean>` |
| `/hologram edit <hologram> textalignment <alignment>` | `/hologram edit <hologram> text_alignment <alignment>` |
| `/hologram edit <hologram> seethrough <boolean>` | `/hologram edit <hologram> see_through <boolean>` |
| `/hologram edit <hologram> visibilitydistance <distance>` | `/hologram edit <hologram> visibility_distance <distance>` |
| `/hologram edit <hologram> updatetextinterval <interval>` | `/hologram edit <hologram> update_text_interval <duration>`, e.g. `1s` |
| `/hologram edit <hologram> shadowradius <radius>` | `/hologram edit <hologram> shadow_radius <radius>` |
| `/hologram edit <hologram> shadowstrength <strength>` | `/hologram edit <hologram> shadow_strength <strength>` |
| `/hologram edit <hologram> linkwithnpc <npc>` | `/hologram edit <hologram> link_with_npc <npc>` |
| `/hologram edit <hologram> unlinkwithnpc` | `/hologram edit <hologram> unlink_with_npc` |
| `/fancyholograms save` | `/fancyholograms storage save` |
| `/fancyholograms reload` | `/fancyholograms config reload` and `/fancyholograms storage load` |

### Administration commands

| Command | Description |
| --- | --- |
| `/fancyholograms version` | Shows the installed version and checks for an update. |
| `/fancyholograms config show` | Shows settings, experimental flags, current values, and defaults. |
| `/fancyholograms config reload` | Reloads `config.yml` and updates the log level. |
| `/fancyholograms storage save` | Saves persistent holograms. |
| `/fancyholograms storage load` | Unloads current holograms and loads the saved data from disk. |
| `/fancyholograms backup list` | Lists available backups. |
| `/fancyholograms backup create` | Creates a data backup. |
| `/fancyholograms backup restore <file>` | Replaces the current data with a backup. |
| `/fancyholograms backup delete <file>` | Deletes a backup. |

Save any changes you want to keep before loading data from disk. Configuration reload does not reload language files or recreate scheduled tasks; restart after changing the language or scheduling settings.

### Permissions

v3 checks permissions per command. The old `fancyholograms.admin` permission alone does not grant all command access.

Most command permissions begin with `fancyholograms.commands.`, but some use different paths. Examples:

| Command or action | Permission |
| --- | --- |
| Create a hologram | `fancyholograms.commands.hologram.create` |
| Select a hologram | `fancyholograms.commands.hologram.select` |
| Change scale | `fancyholograms.commands.hologram.edit.scale` |
| Change rotation | `fancyholograms.commands.hologram.rotation` |
| Change glowing | `fancyholograms.commands.hologram.glowing` |
| Move or swap text lines | `fancyholograms.hologram.edit.move_line` |
| Attach a trait | `fancyholograms.commands.hologram.trait.attach` |
| Reload configuration | `fancyholograms.commands.fancyholograms.config.reload` |
| Load hologram data | `fancyholograms.commands.fancyholograms.storage.load` |
| Restore a backup | `fancyholograms.commands.fancyholograms.backup.restore` |

For holograms using `PERMISSION_REQUIRED` visibility, the viewing permission remains `fancyholograms.viewhologram.<name>`.

The [v3 command source](https://github.com/FancyInnovations/FancyPlugins/tree/main/plugins/fancyholograms/src/main/java/com/fancyinnovations/fancyholograms/commands) contains the full command signatures and permission nodes.

### Translations

Plugin messages can now be customized through language files. The default messages are in `plugins/FancyHolograms/languages/default.yml`.

Copy that file into the same folder using a filename such as `nl.yml` or `pl.yml`. Set the copy's top-level `language_name` value to a unique name, such as `nl`, edit its messages, and set `settings.language` in `config.yml` to that same name. Restart the server to load the language.

Keep custom translations in your own language file; `default.yml` is refreshed from the plugin at startup.

### Selecting holograms

Use `/hologram select <hologram>` to select a hologram. Creating a hologram also selects it automatically.

Use `.selected` in place of the hologram name in subsequent commands:

```text
/hologram select my_hologram
/hologram edit .selected billboard FIXED
/hologram edit .selected set_line 1 <gold>Welcome!
```

Use `.nearest` to target the nearest hologram in your current world:

```text
/hologram info .nearest
```

Both selectors require a player and work wherever a command accepts an existing hologram.

### Editing improvements

Text line numbers start at 1.

| Command | Description |
| --- | --- |
| `/hologram edit <hologram> move_line_up <line>` | Moves a text line up one position. |
| `/hologram edit <hologram> move_line_down <line>` | Moves a text line down one position. |
| `/hologram edit <hologram> swap_lines <line1> <line2>` | Exchanges two text lines. |
| `/hologram edit <hologram> remove_line [line]` | Removes the specified line, or the last line if omitted. |
| `/hologram edit <hologram> scale <factor>` | Sets the same scale on all three axes. |
| `/hologram edit <hologram> scale <x> <y> <z>` | Sets each axis independently. |

When editing a text line, tab completion suggests its current text. Uniform scaling remains available alongside per-axis scaling.

The `move_to` command supports absolute coordinates, offsets from the hologram with `~`, and offsets from the player with `~~`. Optional yaw and pitch arguments follow the coordinates.

```text
/hologram edit .selected move_to ~ ~1 ~
/hologram edit .selected move_to ~~ ~~2 ~~
```

The first example moves the hologram up one block. The second uses the player's coordinates plus two blocks on the Y axis, within the hologram's current world.

Text update intervals accept durations such as `500ms`, `1s`, or `1m`. Use `off` to disable periodic text updates:

```text
/hologram edit .selected update_text_interval 1s
/hologram edit .selected update_text_interval off
```

## New display options

### Text opacity

Text holograms support configurable text opacity:

```text
/hologram edit <hologram> text_opacity <opacity>
/hologram edit welcome text_opacity 75%
```

The command accepts percentages from 0 to 100, with or without the `%` suffix. This controls the text itself; background transparency is configured separately.

The API and JSON storage represent opacity as a value from 0 to 255. See the [builder examples](#builders).

### Block states

Block holograms can display block-state properties, such as the facing direction of stairs or whether a lamp is lit:

```text
/hologram edit <hologram> blockstate <property> <value>
/hologram edit lamp block REDSTONE_LAMP
/hologram edit lamp blockstate lit true
```

Available properties and values depend on the hologram's block material. Tab completion suggests properties and their values. Full block data, including state properties, is saved with the hologram.

### Glowing outlines

Item and block holograms support colored glowing outlines:

```text
/hologram edit <hologram> glowing [color]
/hologram edit reward glowing GOLD
/hologram edit reward glowing DISABLED
```

Omitting the color toggles glowing, using white when enabling it. Text holograms do not support this option.

## New storage system

### File structure

v3 replaces the single YAML file with JSON files under `plugins/FancyHolograms/data/holograms/`.

You can organize holograms into subfolders, and each JSON file can contain multiple holograms. Files are loaded recursively; files whose names begin with `.` or `_` are skipped.

Example folder structure:

```text
plugins/FancyHolograms/data/holograms/
├── rules.json
├── migrated/
│   └── welcome.json
├── spawn/
│   ├── shops.json
│   └── quests.json
└── events/
    └── summer.json
```

Each file contains an array of holograms. A text hologram might look like this; change the world name and coordinates to match your server:

```json
[
  {
    "hologram_data": {
      "name": "welcome",
      "type": "TEXT",
      "location": {
        "world": "world",
        "x": 0.5,
        "y": 65.0,
        "z": 0.5,
        "yaw": 0.0,
        "pitch": 0.0
      },
      "world_name": "world",
      "visibility_distance": 20,
      "visibility": "ALL",
      "linked_npc_name": null,
      "traits": []
    },
    "display_data": {
      "scale": { "x": 1.0, "y": 1.0, "z": 1.0 },
      "translation": { "x": 0.0, "y": 0.0, "z": 0.0 },
      "shadow_radius": 0.0,
      "shadow_strength": 0.0,
      "brightness": null,
      "billboard": "CENTER",
      "glowing_color": "DISABLED"
    },
    "text_data": {
      "text": ["<gold>Welcome!", "Enjoy your stay."],
      "text_shadow": true,
      "see_through": false,
      "text_alignment": "CENTER",
      "text_update_interval": 1000,
      "background_color": "#40000000",
      "text_opacity": 255
    }
  }
]
```

The components are:

- `hologram_data`: name, type, location, world, visibility, NPC link, and attached trait names
- `display_data`: scale, translation, billboard, brightness, shadows, and glowing
- `text_data`: text, alignment, background, shadow, update interval, and opacity

Item and block holograms use `item_data` or `block_data` instead of `text_data`. Item data is serialized as Base64, and block data includes the material and any state properties.

Stop the server before reorganizing or manually editing files so a running save cannot overwrite your changes.

### Backups

v3 creates automatic data backups at a configured interval, defaulting to 24 hours, and retains them for 30 days by default. Empty data folders are skipped.

Backups are ZIP files in `plugins/FancyHolograms/backups/`. They contain the contents of `plugins/FancyHolograms/data/`, including holograms and trait data. They do **not** include `config.yml`, language files, or plugin jars.

Use the [administration commands](#administration-commands) to list, create, restore, or delete backups. Save persistent holograms with `/fancyholograms storage save` first if you want to include unsaved changes. For restore and delete, pass a filename from `backup list`, including its `.zip` extension.

Restoring a backup replaces the current data folder and loaded holograms with the saved state. Create a backup first if you want to keep the current state. Archives are validated before the current data is replaced.

To restore manually:

1. Stop the server
2. Extract the chosen backup into a separate folder and check its contents
3. Move the current `plugins/FancyHolograms/data/` folder into a separate backup location
4. Create a new `data/` folder and copy the extracted contents into it. The `holograms/` and `traits/` folders, when present, belong directly inside `data/`
5. Start the server and check the restored holograms

## New configuration

Configuration remains in `plugins/FancyHolograms/config.yml`. Regular settings now use the `settings` section, and feature flags use `experimental_features`.

For example:

```yaml
settings:
  language: default
  refresh_holograms_on_locale_change: false
  backups:
    interval: 24
    retention: 30
experimental_features:
  enable_rotation_improvement: false
```

The tables below use full dotted paths. Reapply v2 settings using these paths; copying the old configuration unchanged does not migrate it.

### Settings

| Option | Description | Default |
| --- | --- | --- |
| `settings.language` | The selected language file's `language_name` value. | `default` |
| `settings.logging.level` | Log level: `DEBUG`, `INFO`, `WARN`, or `ERROR`. | `INFO` |
| `settings.logging.version_notification` | Mutes version notifications when `true`. | `false` |
| `settings.register_commands` | Command registration setting; see the implementation notes below. | `true` |
| `settings.spawn_delay_on_join_ms` | Delay after joining before holograms spawn, in milliseconds. | `300` |
| `settings.visibility_distance` | Default hologram visibility distance, in blocks. | `20` |
| `settings.refresh_holograms_on_locale_change` | Refreshes a player's holograms after their client language changes. | `false` |
| `settings.backups.interval` | Interval between automatic backups, in hours. | `24` |
| `settings.backups.retention` | Backup retention, in days. | `30` |
| `settings.saving.save_on_changed` | Saves holograms when command edits are made. | `true` |
| `settings.saving.autosave.enabled` | Enables the autosave task at startup. | `true` |
| `settings.saving.autosave.interval` | Configured autosave interval, described as minutes; see the implementation notes below. | `15` |
| `settings.performance.hologram_update_interval_ms` | Polling interval for periodic text updates, in milliseconds. Lower values increase polling frequency. | `200` |

Locale refresh remains available from v2 and is now configured under `settings.refresh_holograms_on_locale_change`.

The current implementation has two configuration limitations: commands are registered regardless of `settings.register_commands`, and autosave repeats every 120 seconds, using `settings.saving.autosave.interval` only as the initial delay in seconds. Restart after changing startup or scheduling settings.

### Experimental features

| Option | Description | Default |
| --- | --- | --- |
| `experimental_features.disable_holograms_for_old_clients` | Hides holograms from clients older than Minecraft 1.19.4. | `false` |
| `experimental_features.disable_holograms_for_bedrock_players` | Hides holograms from Bedrock players. | `false` |
| `experimental_features.enable_rotation_improvement` | Applies rotation through display entity data instead of the entity's location rotation. | `false` |
| `experimental_features.enable_folia_visibility_fix` | Respawns holograms after 100 ms when spawning them on Folia. | `false` |

The old Lamp command toggle has been removed. v3 uses the rewritten commands by default.

## Traits

Traits extend a hologram's behavior and can store their own configuration. Attach, detach, or inspect traits with:

```text
/hologram edit <hologram> trait attach <trait>
/hologram edit <hologram> trait detach <trait>
/hologram edit <hologram> trait list
```

Use the registered trait identifier when attaching it. The configuration command group uses a shorter name:

| Trait | Attach identifier | Configuration command group |
| --- | --- | --- |
| Interaction | `interaction_trait` | `/hologramtrait interaction` |
| Multiple pages | `multiple_pages_trait` | `/hologramtrait multiple_pages` |
| File content | `file_content_trait` | `/hologramtrait file_content` |

For example:

```text
/hologram edit my_hologram trait attach multiple_pages_trait
/hologramtrait multiple_pages my_hologram mode CYCLE
/hologramtrait multiple_pages my_hologram delay 5s
```

### Interaction trait

Requires FancyNpcs. This trait creates an invisible `INTERACTION` NPC with a hitbox around the hologram and runs FancyNpcs actions when a player interacts with it.

Available commands:

- `/hologramtrait interaction <hologram> info`: shows the current configuration.
- `/hologramtrait interaction <hologram> update_hitbox`: recalculates the hitbox.
- `/hologramtrait interaction <hologram> add_action <action> <value>`: appends an action.
- `/hologramtrait interaction <hologram> remove_action <index>`: removes an action.
- `/hologramtrait interaction <hologram> set_action <index> <action> <value>`: replaces an action.
- `/hologramtrait interaction <hologram> clear_actions`: removes all actions.

Action indices start at 1. Actions registered by other plugins with FancyNpcs are also available.

The configuration is stored in `plugins/FancyHolograms/data/traits/interaction_trait/<hologram name>.json`.

### Multiple pages trait

For text holograms. This trait displays several pages using one of three modes:

- `MANUAL`: choose the displayed page with `current_index`
- `CYCLE`: advance through the pages in order and return to the first page after the last
- `RANDOM`: choose a random page between updates

Available commands:

- `/hologramtrait multiple_pages <hologram> info`: shows the configuration.
- `/hologramtrait multiple_pages <hologram> mode <mode>`: changes the mode.
- `/hologramtrait multiple_pages <hologram> delay <duration>`: changes the cycle delay, e.g. `5s`.
- `/hologramtrait multiple_pages <hologram> current_index <index>`: selects a page in manual mode.
- `/hologramtrait multiple_pages <hologram> add_line <page> <text>`: appends a line to an existing page.
- `/hologramtrait multiple_pages <hologram> set_line <page> <line> <text>`: replaces a line.
- `/hologramtrait multiple_pages <hologram> remove_line <page> <line>`: removes a line.

Page and line numbers in commands start at 1. Use a positive duration for the cycle delay.

The configuration is stored in `plugins/FancyHolograms/data/traits/multiple_pages_trait/<hologram name>.json`. Add or remove whole pages by editing its `pages` array while the server is stopped. The stored `cycleDelay` value is in milliseconds.

### File content trait

For text holograms. This trait displays the contents of a file as the hologram's text.

Available commands:

- `/hologramtrait file_content <hologram> info`: shows the configuration
- `/hologramtrait file_content <hologram> file_path <file_path>`: changes the file path
- `/hologramtrait file_content <hologram> refresh_interval <duration>`: changes the refresh interval, e.g. `5s`
- `/hologramtrait file_content <hologram> update`: reads the file immediately

Relative paths are resolved from the server's working directory, usually the folder containing the server jar. The default path is `server.properties`; choose a file whose contents are intended to be visible to hologram viewers.

The configuration is stored in `plugins/FancyHolograms/data/traits/file_content_trait/<hologram name>.json`. Its `refreshInterval` value is in milliseconds.

### Custom component provider trait

For developers, `CustomComponentProviderTrait` supplies an Adventure component per player, replacing the normal string-based text:

```java
import com.fancyinnovations.fancyholograms.api.data.property.CustomComponentProviderTrait;
import net.kyori.adventure.text.minimessage.MiniMessage;

hologram.getData().getTraitTrait().addTrait(
        new CustomComponentProviderTrait(viewer ->
                MiniMessage.miniMessage().deserialize("<rainbow>My custom component</rainbow>"))
);
```

The provider function is supplied in code and must be attached again when your integration recreates or loads the hologram; it cannot be restored from JSON.

## New API

The API package namespace changes from `de.oliver.fancyholograms.api` to `com.fancyinnovations.fancyholograms.api`. The old manager-based integration must be updated to use the new builders, registry, controller, and trait APIs.

The [v3 API source](https://github.com/FancyInnovations/FancyPlugins/tree/main/plugins/fancyholograms/fh-api/src/main/java/com/fancyinnovations/fancyholograms/api) is the reference for the interfaces and types described below.

### Getting started

For the initial v3 release, use the following Maven repository and dependency:

```kotlin
repositories {
    maven {
        name = "fancyinnovationsReleases"
        url = uri("https://repo.fancyinnovations.com/releases")
    }
}

dependencies {
    compileOnly("com.fancyinnovations:FancyHolograms:3.0.0")
}
```

When targeting a development build, use the matching `3.0.0-SNAPSHOT.*` version from `https://repo.fancyinnovations.com/snapshots` instead.

### Builders

There is one builder per hologram type: `TextHologramBuilder`, `ItemHologramBuilder`, and `BlockHologramBuilder`.

Example of creating a text hologram, where `player` is an online Bukkit player:

```java
import com.fancyinnovations.fancyholograms.api.data.builder.TextHologramBuilder;
import com.fancyinnovations.fancyholograms.api.data.property.Visibility;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;

Hologram hologram = TextHologramBuilder.create("Test", player.getLocation())
        .text("<gold>Welcome!", "Enjoy your stay.")
        .background(Color.BLACK)
        .textAlignment(TextDisplay.TextAlignment.LEFT)
        .textShadow(true)
        .seeThrough(false)
        .textOpacity(255)
        .updateTextInterval(1000)
        .visibilityDistance(32)
        .visibility(Visibility.ALL)
        .persistent(false)
        .billboard(Display.Billboard.CENTER)
        .scale(1, 1, 1)
        .translation(0, 0.25f, 0)
        .brightness(15, 15)
        .shadowRadius(0.5f)
        .shadowStrength(0.7f)
        .interpolationDuration(5)
        .build();
```

`build()` creates the hologram without registering it. `buildAndRegister()` also registers it with FancyHolograms.

Text update intervals are measured in **milliseconds**; display interpolation durations are measured in **ticks**. Text opacity uses 0–255; the byte overload also accepts `-1` for fully opaque text.

Call type-specific methods such as `textOpacity` or `blockState` before shared methods such as `visibilityDistance`, because shared methods return the base `HologramBuilder` type.

Block holograms can be created with state properties:

```java
import com.fancyinnovations.fancyholograms.api.data.builder.BlockHologramBuilder;
import org.bukkit.Material;

Hologram lamp = BlockHologramBuilder.create("Lamp", player.getLocation())
        .block(Material.REDSTONE_LAMP, "lit", "true")
        .billboard(Display.Billboard.FIXED)
        .buildAndRegister();
```

The builder also offers `blockData(BlockData)` for a complete Bukkit block state and `blockState(property, value)` for individual properties.

### Registry and controller

The old `HologramManager` is replaced by `HologramRegistry` and `HologramController`.

The registry registers, finds, and unregisters holograms:

```java
import com.fancyinnovations.fancyholograms.api.FancyHolograms;
import java.util.Optional;

FancyHolograms.get().getRegistry().register(hologram);

Optional<Hologram> found = FancyHolograms.get().getRegistry().get("Test");
Hologram required = FancyHolograms.get().getRegistry().mustGet("Test");
```

`get()` returns an empty optional if the hologram is absent; `mustGet()` throws an `IllegalArgumentException`.

Persistent holograms participate in autosave and are saved during a normal server shutdown. Registration alone does not immediately save them. The text example above uses `persistent(false)` for a temporary hologram; leave persistence enabled for holograms that should survive a restart.

The controller refreshes visibility for a player or a collection of players:

```java
FancyHolograms.get().getController().refreshHologram(hologram, players);
```

A refresh spawns or despawns the hologram according to visibility conditions and distance. It is a visibility check, not a request to force a display-data update.

To unregister and delete a hologram's saved data:

```java
FancyHolograms.get().getRegistry().unregister(hologram);
```

### Custom traits

Create a trait by extending `HologramTrait` and annotating it with `@HologramTraitClass(traitName = "my_trait")`. Traits restored from storage need a public no-argument constructor.

Override lifecycle callbacks such as `onAttach`, `onSpawn`, `onDespawn`, `onUpdate`, `onModify`, `onRegister`, and `onUnregister`, plus `load` and `save` as needed. Clean up scheduled tasks or other resources in `onUnregister`.

Register your trait class with `FancyHolograms.get().getTraitRegistry().register(MyTrait.class)` before holograms using it are loaded. Attach it through the builder's `trait(MyTrait.class)` method or `hologram.getData().addTrait(MyTrait.class)`.

Each attached trait receives a JDB storage object rooted at `plugins/FancyHolograms/data/traits/<trait name>/`. Paths passed to that object are relative to the trait's folder.

See the [built-in trait implementations](https://github.com/FancyInnovations/FancyPlugins/tree/main/plugins/fancyholograms/src/main/java/com/fancyinnovations/fancyholograms/trait/builtin) and the [JDB source](https://github.com/FancyInnovations/FancyPlugins/tree/main/libraries/jdb) for examples.
