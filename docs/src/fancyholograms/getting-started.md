---
order: 10
icon: info
---
# Getting started

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/minecraft-plugins/fancyholograms](https://fancyinnovations.com/docs/minecraft-plugins/fancyholograms).
!!!

!!!danger
Be aware, that only Paper and Folia is supported, but the plugin should work on any of its forks (like Purpur or Pufferfish). Spigot, Bukkit, Sponge and Fabric is not supported.
!!!

## Installation

To install FancyHolograms, you need to download the latest version from one of the following sources:

[!button size="s" icon="download" iconAlign="left" text="Modrinth" target="blank"](https://modrinth.com/plugin/fancyholograms/versions)

[!button size="s" icon="download" iconAlign="left" text="Hangar" target="blank"](https://hangar.papermc.io/Oliver/FancyHolograms/versions)

After downloading the plugin, you can install it by placing the downloaded file in the `plugins` folder of your server.

Restart your server and you are ready to go!

To check if the plugin is installed correctly, you can use the command `/fancyholograms version`. If the plugin is installed correctly, it will show you the version of the plugin.

## Create your first hologram

1. To create your first hologram, you can use the command `/hologram create text myHologram`. This will create a new text hologram with the given name.
2. To add text to the hologram, you can use the command `/hologram edit myHologram addLine <green>My first hologram</green>`. This will add a new line to the hologram with the given text.
3. To edit the text of the hologram, you can use the command `/hologram edit myHologram setLine 1 <red>My first hologram</red>`. This will set the text of the first line to the given text.
4. To change the background of the hologram, you can use the command `/hologram edit myHologram background RED`. This will set the background of the hologram to red.
5. To move the hologram to your current location, you can use the command `/hologram moveHere myHologram`. This will move the hologram to your current location.

!!!
You can use MiniMessages in the hologram text. For more information about MiniMessages, you can visit the [MiniMessage documentation](https://docs.advntr.dev/minimessage/format.html).
!!!