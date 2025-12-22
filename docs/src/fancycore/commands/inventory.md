---
icon: dot
title: Inventory
---

![](../../static/commands.png)

# Commands for the inventory system

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/hytale-plugins/fancycore](https://fancyinnovations.com/docs/hytale-plugins/fancycore).
!!!

!!!warning
The command list is not complete and not all commands are properly documented yet.
Some commands might not be implemented yet.
!!!

## Kits

### Create Kit

Description: Creates a new kit with the specified name. It contains all items in your inventory<br/>
Syntax: ``/CreateKit (name)``<br/>
Permission: ``fancycore.commands.createkit``

### Delete Kit

Description: Deletes the specified kit.<br/>
Syntax: ``/DeleteKit (name)``<br/>
Permission: ``fancycore.commands.deletekit``

### Give Kit

Description: Gives the specified kit to the targeted player(s).<br/>
Syntax: ``/Kit (name) [player]``<br/>
Permission: ``fancycore.commands.kit.(name)``

### List Kits

Description: Lists all available kits.<br/>
Syntax: ``/Kits`` or ``/ListKits``<br/>
Permission: ``fancycore.commands.listkits``

## Backpacks

### Open Backpack

Description: Opens the specified backpack of the targeted player.<br/>
Syntax: ``/Backpack (name) [player]``<br/>
Permission: ``fancycore.commands.backpack``

### Create Backpack

Description: Creates a new backpack with the specified name and size.<br/>
Syntax: ``/CreateBackpack (name) (size)``<br/>
Permission: ``fancycore.commands.createbackpack``

### Delete Backpack

Description: Deletes the specified backpack.<br/>
Syntax: ``/DeleteBackpack (name)``<br/>
Permission: ``fancycore.commands.deletebackpack``

### List Backpacks

Description: Lists all available backpacks.<br/>
Syntax: ``/Backpacks`` or ``/ListBackpacks``<br/>
Permission: ``fancycore.commands.listbackpacks``

## Utilities

### Clear Inventory

Description: Clears the inventory of the targeted player(s).<br/>
Syntax: ``/ClearInventory [player]``<br/>
Permission: ``fancycore.commands.clearinventory``

### Open another Player's Inventory

Description: Opens the inventory of the targeted player.<br/>
Syntax: ``/OpenInventory (player)`` or ``/Invsee (player)``<br/>
Permission: ``fancycore.commands.openinventory``