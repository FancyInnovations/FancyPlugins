---
icon: dot
title: Moderation
---

![](../../static/commands.png)

# Commands for the moderation system

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/hytale-plugins/fancycore](https://fancyinnovations.com/docs/hytale-plugins/fancycore).
!!!

!!!warning
The command list is not complete and not all commands are properly documented yet.
Some commands might not be implemented yet.
!!!

## Report

### Report a Player

Description: Reports a player for misconduct.<br/>
Syntax: ``/Report (player) [reason]``<br/>
Permission: ``fancycore.commands.report``

### List Reports

Description: Lists all active player reports.<br/>
Syntax: ``/Reports list``<br/>
Permission: ``fancycore.commands.reports.list``

### View Report Details

Description: Views details of a specific report.<br/>
Syntax: ``/Reports view (reportID)``<br/>
Permission: ``fancycore.commands.reports.view``

### Close Report

Description: Closes a specific report.<br/>
Syntax: ``/Reports close (reportID)``<br/>
Permission: ``fancycore.commands.reports.close``

## Warn

### Warn a Player

Description: Issues a warning to a player.<br/>
Syntax: ``/Warn (player) [reason]``<br/>
Permission: ``fancycore.commands.warn``

## Mute

### Mute a Player

Description: Mutes a player, preventing them from sending chat messages.<br/>
Syntax: ``/Mute (player) [reason]``<br/>
Permission: ``fancycore.commands.mute``

### Temporary Mute a Player

Description: Temporarily mutes a player for a specified duration.<br/>
Syntax: ``/Tempmute (player) (duration) [reason]``<br/>
Permission: ``fancycore.commands.tempmute``

### Unmute a Player

Description: Unmutes a player.<br/>
Syntax: ``/Unmute (player)``<br/>
Permission: ``fancycore.commands.unmute``


## Kick

### Kick a Player

Description: Kicks a player from the server.<br/>
Syntax: ``/Kick (player) [reason]``<br/>
Permission: ``fancycore.commands.kick``

## Ban

### Ban a Player

Description: Bans a player from the server.<br/>
Syntax: ``/Ban (player) [reason]``<br/>
Permission: ``fancycore.commands.ban``

### Temporary Ban a Player

Description: Temporarily bans a player from the server for a specified duration.<br/>
Syntax: ``/Tempban (player) (duration) [reason]``<br/>
Permission: ``fancycore.commands.tempban``

### Unban a Player

Description: Unbans a player from the server.<br/>
Syntax: ``/Unban (player)``<br/>
Permission: ``fancycore.commands.unban``

## Logs

### View Moderation Logs

Description: Views the moderation logs.<br/>
Syntax: ``/Modlogs [player]``<br/>
Permission: ``fancycore.commands.modlogs``

### View Chat Logs

Description: Views the chat logs.<br/>
Syntax: ``/Chatlogs [player] [--withCommands]``<br/>
Permission: ``fancycore.commands.chatlogs``
