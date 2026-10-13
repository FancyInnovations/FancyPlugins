---
icon: dot
title: Teleportation
---

![](../../static/commands.png)

# Commands for the teleportation system

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/hytale-plugins/fancycore](https://fancyinnovations.com/docs/hytale-plugins/fancycore).
!!!

!!!warning
The command list is not complete and not all commands are properly documented yet.
Some commands might not be implemented yet.
!!!

## Player Teleportation

### Teleport to another player

Description: Teleports you or the specified player to another player's location.<br/>
Syntax: ``/TP (target player) [destination player]`` or ``/Teleport (target player) [destination player]``<br/>
Permission: ``fancycore.commands.tp``

### Teleport another player to you

Description: Teleports the specified player to your location.<br/>
Syntax: ``/TPHere (player)`` or ``/TeleportHere (player)``<br/>
Permission: ``fancycore.commands.tphere``

### Teleport all players to you

Description: Teleports all players on the server to your location.<br/>
Syntax: ``/TPAll`` or ``/TeleportAll``<br/>
Permission: ``fancycore.commands.tpall``

### Teleport to specific coordinates

Description: Teleports you to the specified coordinates<br/>
Syntax: ``/TPPos (x) (y) (z) [world]`` or ``/TeleportPos (x) (y) (z) [world]``<br/>
Permission: ``fancycore.commands.tppos``

### Request teleport to another player

Description: Sends a teleport request to another player to teleport to their location.<br/>
Syntax: ``/TPR (player)``, ``/TPRequest (player)`` or ``/TeleportRequest (player)``<br/>
Permission: ``fancycore.commands.tprequest``

### Accept teleport request

Description: Accepts a pending teleport request from another player.<br/>
Syntax: ``/TPA [player]``, ``/TPAccept [player]`` or ``/TeleportAccept [player]``<br/>
Permission: ``fancycore.commands.tpaccept``

### Deny teleport request

Description: Denies a pending teleport request from another player.<br/>
Syntax: ``/TPD [player]``, ``/TPDeny [player]`` or ``/TeleportDeny [player]``<br/>
Permission: ``fancycore.commands.tpdeny``

### Teleport back to previous location

Description: Teleports you back to your previous location before your last teleport.<br/>
Syntax: ``/Back``<br/>
Permission: ``fancycore.commands.back``

### Teleport to last death location

Description: Teleports you to the location where you last died.<br/>
Syntax: ``/DeathBack``, ``/DeathTP`` or ``/DeathTeleport``<br/>
Permission: ``fancycore.commands.deathback``

## Spawn

### Set spawn point

Description: Sets the server's spawn point to your current location.<br/>
Syntax: ``/SetSpawn``<br/>
Permission: ``fancycore.commands.setspawn``

### Teleport to spawn point

Description: Teleports you to the server's spawn point.<br/>
Syntax: ``/Spawn``<br/>
Permission: ``fancycore.commands.spawn``

## Warps

### Create a warp point

Description: Creates a warp point at your current location with the specified name.<br/>
Syntax: ``/CreateWarp (name)``<br/>
Permission: ``fancycore.commands.createwarp``

### Delete a warp point

Description: Deletes the warp point with the specified name.<br/>
Syntax: ``/DeleteWarp (name)``<br/>
Permission: ``fancycore.commands.deletewarp``

### Teleport to a warp point

Description: Teleports you to the warp point with the specified name.<br/>
Syntax: ``/Warp (name)``<br/>
Permission: ``fancycore.commands.warp.(name)``

### List all warp points

Description: Lists all available warp points on the server.<br/>
Syntax: ``/ListWarps``, ``/Warps``<br/>
Permission: ``fancycore.commands.listwarps``

## Homes

### Set home point

Description: Sets your home point to your current location.<br/>
Syntax: ``/SetHome (name)``<br/>
Permission: ``fancycore.commands.sethome``

### Delete home point

Description: Deletes your home point with the specified name.<br/>
Syntax: ``/DeleteHome (name)``<br/>
Permission: ``fancycore.commands.deletehome``

### Teleport to home point

Description: Teleports you to your home point with the specified name or the first home if no name is provided.<br/>
Syntax: ``/Home [name]``<br/>
Permission: ``fancycore.commands.home``