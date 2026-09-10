## How to update

v3 is basically a complete rewrite of the plugin. 
It is not compatible with v2 and earlier versions.
Not everything will be migrated automatically, so you will have to do some manual work.

Things that will be migrated automatically:
- All your hologram data (holograms.yml)

Things that will **not** be migrated automatically:
- All configurations from config.yml
- All configurations from featureFlags.yml

The API has changed significantly, so you will have to update your code accordingly.
Read more about the new API [here](#new-api).

## New Commands & Translations

In v3, all commands have been rewritten and simplified.

### Framework

If you're using FancyNpcs, you will notice that the commands are now more consistent with the FancyNpcs commands.
It's now using the [Lamp command framework](https://github.com/Revxrsal/Lamp), which allows for better command handling and easier command creation.

You can find documentation about the new commands [here](https://fancyinnovations.com/docs/minecraft-plugins/fancyholograms/commands/hologram).

### Translations

Not only the command structure has changed, but also the responses.
All responses are now configurable. 
You can find the default responses in the `plugins/FancyHolograms/languages/default.yml` file.

You can create your own language files in the `plugins/FancyHolograms/languages/` folder, by copying the `default.yml` file and renaming it to your desired language code (e.g. `nl.yml`, `pl.yml`, etc.).

Remember to set the `language` option in the `config.yml` file to your language code.

### Selecting holograms

One of the new features in v3 is the ability to select a hologram and perform actions on it without having to specify its name every time.

To select a hologram, you can use the `/fancyholograms select <hologram_name>` command.
Once a hologram is selected, you can perform actions on it without having to specify its name again.
Simply use `.selected` as the hologram name in the command, and it will use the selected hologram.

Example:
1. Select a hologram: `/hologram select my_hologram`
2. Edit something: `/hologram edit .selected billboard FIXED`

You can also use `.nearest` to select the nearest hologram to you.

`.selected` and `.nearest` can be used in any command that requires a hologram name.

## New Storage System

The storage system has been completely rewritten.
It now uses a more efficient and flexible system that allows for better performance and easier management of holograms.

The file format is now JSON instead of YAML, which allows for better compatibility with other systems and easier parsing.

### File Structure

Many of you are annoyed by the fact that the hologram data was stored in a single file (`holograms.yml`).
If you had a lot of holograms, this file could become huge and difficult to manage.
This has been changed in v3.

The hologram data is no longer stored in a single file, but in multiple files.
The new place for hologram data is in the `plugins/FancyHolograms/data/holograms/` folder.
In this folder, you can create subfolders to organize your holograms.
Every JSON file in this folder will be loaded (recursively).
Each JSON file can contain multiple holograms, which allows for better organization and management of holograms.

Example folder structure:

- `plugins/FancyHolograms/data/holograms/`
  - `rules-hologram.json`
  - `info-hologram.json`
  - `spawn-holograms/`
    - `shop-holograms.json`
    - `quest-holograms.json`
    - `crates/`
      - `crate1.json`
      - `crate2.json`
      - `crate3.json`
  - `event-holograms/`
    - `event1.json`
    - `event2.json`
    - `event3.json`

Looking at one JSON file, it will look like this:

```json
[
    {
      first hologram ...
    },
    {
      second hologram ...
    }
]
```

With each hologram containing the following "components":

```json
{
  "hologram_data": { ... },
  "display_data": { ... },
  "text_data": { ... },
}
```

If the hologram is a block or item hologram, it will have the `block_data` or `item_data` component instead of `text_data`.

The `hologram_data` component contains the basic information about the hologram, such as its name, location and visibility.
The `display_data` component contains the display settings for the hologram, such as its scale, shadow and billboard.
The `text_data` component contains the text settings for the hologram, such as the text itself, text alignment and background color.

### Backups

FancyHolograms v3 will automatically create a backup of your hologram data every 24h (configurable) and keeps every backup for 30 days (configurable).
You can manage the backups with the `/fancyholograms backup <list|create|restore|delete>` commands. Backups are stored in the `plugins/FancyHolograms/backups/` folder.

Backup intervals and retention can be configured in the `config.yml` file.

You can manually restore a backup:
1. Stop the server
2. Delete all files in the `plugins/FancyHolograms/data` folder
3. Unzip the backup file
4. Copy the contents of the backup file to the `plugins/FancyHolograms/data` folder
5. Start the server

## New Configuration

**Disclaimer:** all configuration options do not migrate automatically. You will have to update your configuration files manually.

The feature flags are now stored in the `config.yml` too. You can find them under the `experimental_features` section.

Configuration options (settings):

| Option                                    | Description                                                                                                                                           | Default     |
|-------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|-------------|
| `language`                                | The language to use for the plugin.                                                                                                                   | default     |
| `logging.level`                           | The log level for the plugin (DEBUG, INFO, WARN, ERROR).                                                                                              | INFO        |
| `logging.version_notification`            | Whether version notifications are muted.                                                                                                              | false       |
| `register_commands`                       | Whether the plugin should register its commands.                                                                                                      | true        |
| `spawn_delay_on_join_ms`                  | The delay after player join before holograms are spawned, in milliseconds.                                                                            | 300 (ms)    |
| `visibility_distance`                     | The default visibility distance for holograms.                                                                                                        | 20 (blocks) |
| `backups.interval`                        | The interval at which backups are created (in hours).                                                                                                 | 24 (hours)  |
| `backups.retention`                       | How long backups are kept (in days).                                                                                                                  | 30 (days)   |
| `saving.save_on_changed`                  | Whether the plugin should save holograms when they are changed.                                                                                       | true        |
| `saving.autosave.enabled`                 | Whether autosave is enabled.                                                                                                                          | true        |
| `saving.autosave.interval`                | The interval at which autosave is performed in minutes.                                                                                               | 15 (mins)   |
| `performance.hologram_update_interval_ms` | The interval at which holograms check for text updates (in milliseconds). Lower values = more responsive but higher CPU usage. Recommended: 200-500ms | 200 (ms)    |

Configuration options (experimental features):

| Option                                                        | Description                                                                                             | Default |
|---------------------------------------------------------------|---------------------------------------------------------------------------------------------------------|---------|
| `experimental_features.disable_holograms_for_old_clients`     | Do not show holograms to clients with a version older than 1.19.4.                                      | false   |
| `experimental_features.disable_holograms_for_bedrock_players` | Do not show holograms to bedrock players.                                                               | false   |
| `experimental_features.enable_rotation_improvement`           | When enabled, hologram rotation will be done on display entities instead of the normal entity location. | false   |
| `experimental_features.enable_folia_visibility_fix`           | When enabled, all holograms will respawn after 100ms when they should spawn.                            | false   |

## Traits

Traits are a new feature in v3 that allows you to extend the functionality of holograms.
A trait is basically an extension that can be added to a hologram to add new features or functionality.
Traits have access to the hologram's data and lifecycle, allowing them to modify the hologram's behavior or appearance.
Each trait can have its own configuration and can be enabled or disabled individually.

You can add / remove traits to holograms using the `/hologram edit (hologram) traits (atttach|detach) (trait)` command.

There are several built-in traits that come with FancyHolograms v3:

### Interaction Trait

This trait will spawn a npc (via FancyNpcs) with the type `INTERACTION` and position it around the hologram.
Interaction entities are completely invisible but have a hitbox that can be interacted with.
You can configure actions that will be executed when a player interacts with the interaction entity / hologram.
You can use any action supported by FancyNpcs, such as running a command or sending a message.

When the interaction trait is attached to a hologram, you have access to the following commands:
- `/hologramtrait interaction <hologram> info`: shows information about the current interaction trait configuration
- `/hologramtrait interaction <hologram> update_hitbox`: updates the hitbox of the interaction entity to match the hologram's size
- `/hologramtrait interaction <hologram> add_action <action> <value>`: adds an action to the interaction trait
- `/hologramtrait interaction <hologram> remove_action <action index>`: removes an action from the interaction trait
- `/hologramtrait interaction <hologram> set_action <index> <action> <value>`: sets an action at a specific index in the interaction trait
- `/hologramtrait interaction <hologram> clear_actions`: removes all actions from the interaction trait

You can also manually edit the action in a file located at `plugins/FancyHolograms/data/traits/interaction_trait/<hologram name>.json`.

Take a look on the [FancyNpcs wiki](https://fancyinnovations.com/docs/minecraft-plugins/fancynpcs/tutorials/action-system#actions) for more information about the available actions.
(Custom actions registered to FancyNpcs are also available!)

### Multiple Pages Trait

This trait adds the ability to have multiple pages in one hologram.

There are three modes to cycle through the pages:
1. MANUAL mode: you set the page number manually (there is a command, see below)
2. CYCLE mode: it will go to the next page every X seconds and will start at the first page after it reaches the last one
3. RANDOM: it will show a random page every X seconds

You can change the mode, delay and pages with the following commands:
- `/hologramtrait multiple_pages <hologram> info`: shows information the current multiple pages trait configuration
- `/hologramtrait multiple_pages <hologram> mode <mode>`: changes the mode
- `/hologramtrait multiple_pages <hologram> delay <delay>`: changes the delay
- `/hologramtrait multiple_pages <hologram> current_index <index>`: sets the current page index (useful for MANUAL mode)
- `/hologramtrait multiple_pages <hologram> add_line <page> <text>`: adds a line to a page (use page index starting from 1)
- `/hologramtrait multiple_pages <hologram> set_line <page> <line> <text>`: sets a line in a page
- `/hologramtrait multiple_pages <hologram> remove_line <page> <line>`: removes a line from a page

You can also manually edit the pages in a file located at `plugins/FancyHolograms/data/traits/multiple_pages_trait/<hologram name>.json`.

### File Content Trait

If this trait is attached to a hologram, it will read the content of a file and display it as the hologram's text.

You can configure the file path and the update interval with the following commands:
- `/hologramtrait file_content <hologram> info`: shows information about the current file content trait configuration
- `/hologramtrait file_content <hologram> update`: forcefully updates the hologram's text from the file
- `/hologramtrait file_content <hologram> file_path <file path>`: sets the file path (relative to the folder where your server jar is located)
- `/hologramtrait file_content <hologram> refresh_interval <interval>`: sets the update interval

Be careful what file you set as the file path, as it will be read by the server and displayed to all players.

### Custom Component Provider Trait

This trait is only meant to be used by developers.

This trait allows other developers to use custom Adventure components instead of the default list of strings as hologram text.
Example:
```java
hologram.getData().getTraitTrait().addTrait(new CustomComponentProviderTrait(player -> {
    return MiniMessage.miniMessage().deserialize("<rainbow>My custom component</rainbow>");
}));
```

## New API

The API has been completely rewritten.
You can now create holograms using builders, which allows for better readability and easier creation of holograms.
With traits, you can extend the functionality of holograms and add new features.

You can view the Javadocs [here](https://fancyspaces.net/javadoc/fi/releases/de.oliver:FancyHolograms/3.0.0/index.html).

### Getting Started

Repository:
```kotlin
maven {
  name = "fancyinnovationsReleases"
  url = uri("https://repo.fancyinnovations.com/releases")
}
```

Dependency:
```kotlin
compileOnly("com.fancyinnovations:FancyHolograms:3.0.0")
```

### Builders

The new API introduces builders for creating holograms.
There is one builder for each type of hologram: `TextHologramBuilder`, `ItemHologramBuilder` and `BlockHologramBuilder`.

Example of creating a text hologram:

```java
Hologram hologram = TextHologramBuilder.create("Test", player.getLocation())
                .text("Custom line")
                .background(Color.BLACK)
                .textAlignment(TextDisplay.TextAlignment.LEFT)
                .textShadow(true)
                .seeThrough(true)
                .updateTextInterval(420)
                .visibilityDistance(42)
                .visibility(Visibility.ALL)
                .persistent(false)
                .linkedNpcName("TestNPC")
                .trait(DebugTrait.class)
                .billboard(Display.Billboard.FIXED)
                .scale(3, 5, 6)
                .translation(1, 2, 3)
                .brightness(7, 3)
                .shadowRadius(0.5f)
                .shadowStrength(0.7f)
                .interpolationDuration(100)
                .build();
```

You can also call `buildAndRegister()` to create and register the hologram in one go.

### Registry and Controller

The HologramManager has been replaced with a HologramRegistry and a HologramController.
The HologramRegistry is responsible for registering and unregistering holograms.
The HologramController is responsible for managing the hologram's visibility and updating the holograms.

You can either register your hologram using the `buildAndRegister()` method in the builder, or you can register it manually using the `HologramRegistry`:

```java
FancyHolograms.get().getRegistry().register(hologram);
```

You can get a registered hologram using its name:

```java
Optional<Hologram> hologram = FancyHolograms.get().getRegistry().get("Test");
// or
Hologram hologram = FancyHolograms.get().getRegistry().mustGet("Test");
```

You can manually refresh the hologram's visibility using the `HologramController`:

```java
FancyHolograms.get().getController().refreshHologram(hologram, players);
```

This will spawn the hologram for the specified players if they meet the visibility requirements or despawn it if they don't.

### Traits

Read more about the trait feature below.

You can also create your own traits by extending the `HologramTrait` class.
View the [Javadocs](https://fancyspaces.net/javadoc/fi/releases/com.fancyinnovations:FancyHolograms/3.0.0/index.html) for more information about how the `HologramTrait` class is structured.
You can override all the `on` methods, as well as the `load` and `save` methods.

Every trait has a `storage` JDB (JSON Database) object that can be used to store data related to the trait.
The JDB will save the data to the `plugins/FancyHolograms/data/traits/<trait name>/` folder, all paths are relative to this folder.
You can read more about the JDB [here](https://fancyspaces.net/javadoc/fi/releases/de.oliver:JDB/1.0.4/index.html).

You can view the source code of the built-in traits to see how they are implemented [here](https://github.com/FancyInnovations/FancyPlugins/tree/main/plugins/fancyholograms/src/main/java/com/fancyinnovations/fancyholograms/trait/builtin).