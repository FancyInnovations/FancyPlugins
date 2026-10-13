---
icon: dot
title: World
---

![](../../static/commands.png)

# Commands for the world management system

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/hytale-plugins/fancycore](https://fancyinnovations.com/docs/hytale-plugins/fancycore).
!!!

!!!warning
The command list is not complete and not all commands are properly documented yet.
Some commands might not be implemented yet.
!!!

## World Management

### Create a new world

Description: Creates a new world with the specified name.<br/>
Syntax: ``/CreateWorld (world name) [type]``<br/>
Permission: ``fancycore.commands.createworld``

### Copy an existing world

Description: Creates a copy of the specified world.<br/>
Syntax: ``/CopyWorld (source world name) (new world name)``<br/>
Permission: ``fancycore.commands.copyworld``

### Delete an existing world

Description: Deletes the specified world from the server.<br/>
Syntax: ``/DeleteWorld (world name)``<br/>
Permission: ``fancycore.commands.deleteworld``

### List all worlds

Description: Lists all available worlds on the server.<br/>
Syntax: ``/ListWorlds``, ``/Worlds``<br/>
Permission: ``fancycore.commands.listworlds``

### Teleport to a world

Description: Teleports you to the specified world.<br/>
Syntax: ``/World (world name)``<br/>
Permission: ``fancycore.commands.world``

## Utility Commands

### Set weather

Description: Sets the weather in the current world.<br/>
Syntax: ``/SetWeather (sun|rain)``<br/>
Permission: ``fancycore.commands.setweather``

### Set time

Description: Sets the time of day in the current world.<br/>
Syntax: ``/SetTime (time)``<br/>
Permission: ``fancycore.commands.settime``