---
title: FancyDialogs
icon: browser
order: 90
---

#

![](../static/logos-and-banners/fancydialogs-banner.png)

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/minecraft-plugins/fancydialogs](https://fancyinnovations.com/docs/minecraft-plugins/fancydialogs).
!!!

!!!
Simple and lightweight dialogs plugin for Paper servers and other plugin developers.
!!!

## Features

With this plugin you can create fancy dialogs for your server or plugin. 
FancyDialogs uses the new dialog feature, Minecraft introduced in **1.21.6** and combines it with the power of packets.

!!!warning
The plugin will only for **Paper** servers on **1.21.6** or newer!
!!!

**Core advantages of FancyDialogs:**
- Simple custom dialog creation (in JSON format or in code)
- A lot of different dialog components (text, buttons, input fields, etc.)
- Custom button actions (e.g. run commands, give items, open other dialogs)
- MiniMessages and PlaceholderAPI support

### For servers

If you use FancyDialogs as a plugin, you can take advantage of the following features:
- Dialog as welcome-screen for players joining the first time
- Custom tutorial dialogs
- FancyNpcs 'open_dialog' action for NPCs

### For plugin developers

If you are a plugin developer and want to spice up your plugin with dialogs, you can use FancyDialogs as a library.

You can define default dialogs, which will be persisted in the `plugins/FancyDialogs/data/dialogs` folder.
This allows server administrators to customize the dialogs for their server.
You can then use the dialogs in your plugin and show them to the players whenever you want.

Common use cases are:
- Help dialogs (e.g. for commands or features)
- Confirmation dialogs for critical actions
- Shop UIs (replacing inventories UIs)
- Dialogs for quests
