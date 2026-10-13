---
icon: dot
title: Server
---

![](../../static/commands.png)

# Commands for the server system

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/hytale-plugins/fancycore](https://fancyinnovations.com/docs/hytale-plugins/fancycore).
!!!

!!!warning
The command list is not complete and not all commands are properly documented yet.
Some commands might not be implemented yet.
!!!

## Server control

### Restart

Description: Restarts the server after a specified delay.<br/>
Syntax: ``/Restart (delay)``<br/>
Permission: ``fancycore.commands.restart``

### Stop

Description: Stops the server after a specified delay.<br/>
Syntax: ``/Stop (delay)``<br/>
Permission: ``fancycore.commands.stop``

### Save All

Description: Forces the server to save all data immediately.<br/>
Syntax: ``/SaveAll``<br/>
Permission: ``fancycore.commands.saveall``

## Server health

### Health

Description: Displays an overview of the server's health status.<br/>
Syntax: ``/Health``<br/>
Permission: ``fancycore.commands.health``

### TPS

Description: Displays the current ticks per second (TPS) of the server.<br/>
Syntax: ``/Health tps``<br/>
Permission: ``fancycore.commands.health.tps``

### Memory

Description: Displays the current memory usage of the server.<br/>
Syntax: ``/Health memory``<br/>
Permission: ``fancycore.commands.health.memory``

### Uptime

Description: Displays the server's uptime since the last restart.<br/>
Syntax: ``/Health uptime``<br/>
Permission: ``fancycore.commands.health.uptime``

### CPU Usage

Description: Displays the current CPU usage of the server.<br/>
Syntax: ``/Health cpu``<br/>
Permission: ``fancycore.commands.health.cpu``

### Entity Count

Description: Displays the total number of entities currently loaded on the server.<br/>
Syntax: ``/Health entities``<br/>
Permission: ``fancycore.commands.health.entities``

### Player Count

Description: Displays the current number of players online on the server.<br/>
Syntax: ``/Health players``<br/>
Permission: ``fancycore.commands.health.players``

## Players

### List online players

Description: Lists all players currently online on the server.<br/>
Syntax: ``/OnlinePlayers``<br/>
Permission: ``fancycore.commands.onlineplayers``

### List all registered players

Description: Lists all players registered on the server.<br/>
Syntax: ``/RegisteredPlayers``<br/>
Permission: ``fancycore.commands.registeredplayers``