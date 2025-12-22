---
icon: dot
title: Permissions
---

![](../../static/commands.png)

# Commands for the permissions system

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/hytale-plugins/fancycore](https://fancyinnovations.com/docs/hytale-plugins/fancycore).
!!!

!!!warning
The command list is not complete and not all commands are properly documented yet.
Some commands might not be implemented yet.
!!!

## Groups

### Create Group

Description: Creates a new group with the specified name.<br/>
Syntax: ``/Groups create (name)``<br/>
Permission: ``fancycore.commands.creategroup``

### Delete Group

Description: Deletes the specified permission group.<br/>
Syntax: ``/Groups delete (name)``<br/>
Permission: ``fancycore.commands.deletegroup``

### List Groups

Description: Lists all available permission groups.<br/>
Syntax: ``/Groups list``<br/>
Permission: ``fancycore.commands.listgroups``

### Info Group

Description: Displays information about the specified permission group.<br/>
Syntax: ``/Groups info (name)``<br/>
Permission: ``fancycore.commands.infogroup``

### Set Parent Group

Description: Sets the parent group for the specified permission group.<br/>
Syntax: ``/Groups parent (group) set (parent)``<br/>
Permission: ``fancycore.commands.setparentgroup``

### Add Permission to Group

Description: Adds the specified permission to the permission group.<br/>
Syntax: ``/Groups permission (group) add (permission)``<br/>
Permission: ``fancycore.commands.addpermtogroup``

### Remove Permission from Group

Description: Removes the specified permission from the permission group.<br/>
Syntax: ``/Groups permission (group) remove (permission)``<br/>
Permission: ``fancycore.commands.removepermfromgroup``

### Set Prefix for Group

Description: Sets the prefix for the specified permission group.<br/>
Syntax: ``/Groups prefix (group) set (prefix)``<br/>
Permission: ``fancycore.commands.setprefixforgroup``

### Set Suffix for Group

Description: Sets the suffix for the specified permission group.<br/>
Syntax: ``/Groups suffix (group) set (suffix)``<br/>
Permission: ``fancycore.commands.setsuffixforgroup``

### Add Player to Group

Description: Adds the specified player to the permission group.<br/>
Syntax: ``/Groups player (player) add (group)``<br/>
Permission: ``fancycore.commands.addplayertogroup``

### Remove Player from Group

Description: Removes the specified player from the permission group.<br/>
Syntax: ``/Groups player (player) remove (group)``<br/>
Permission: ``fancycore.commands.removeplayerfromgroup``

## Player Permissions

### Add Permission to Player

Description: Adds the specified permission to the targeted player.<br/>
Syntax: ``/Permission add (permission) [player]``<br/>
Permission: ``fancycore.commands.addpermtoplayer``

### Remove Permission from Player

Description: Removes the specified permission from the targeted player.<br/>
Syntax: ``/Permission remove (permission) [player]``<br/>
Permission: ``fancycore.commands.removepermfromplayer``

### Check Player Permission

Description: Checks if the targeted player has the specified permission.<br/>
Syntax: ``/Permission check (permission) [player]``<br/>
Permission: ``fancycore.commands.checkplayerperm``

### List Player Permissions

Description: Lists all permissions of the targeted player.<br/>
Syntax: ``/Permission list [player]``<br/>
Permission: ``fancycore.commands.listplayerperms``
