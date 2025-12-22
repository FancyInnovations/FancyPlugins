---
icon: dot
order: 10
---

![](../../static/commands.png)

# /Npc

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/minecraft-plugins/fancynpcs](https://fancyinnovations.com/docs/minecraft-plugins/fancynpcs).
!!!

The `/npc` command is the main command for managing NPCs. It allows you to create, modify, and remove NPCs. You can also customize NPCs by changing their type, skin, equipment, and more.

## Manage npcs

### Help about commands

Shows help about all commands.

- **Syntax**:  `/npc help [page]`
- **Permissions**: `fancynpcs.command.npc.help`

### Create npc

Creates a new NPC. Can be customized with flags.

- **Syntax**: `/npc create (name) [--position (x y z)] [--world (world)] [--type (type)]`
- **Permissions**: `fancynpcs.command.npc.create`
- Name check is now more strict and only allows `A-Z a-z 0-9 _ - /` characters.

### Copy npc

Copies (duplicates) specified NPC.

- **Syntax**: `/npc copy (npc) (new_name)`
- **Permissions**: `fancynpcs.command.npc.copy`
- Name check is now more strict and only allows `A-Z a-z 0-9 _ - /` characters.

### Remove npc

Removes the specified NPC.

- **Syntax**: `/npc remove (npc)`
- **Permissions**: `fancynpcs.command.npc.remove`

### List all npcs

Lists all NPCs in all worlds. Can be filtered and sorted.

- **Syntax**:  `/npc list [--type (type)] [--sort (sort)]`
- **Permissions**: `fancynpcs.command.npc.list`

### Get npc information

Shows information about specified NPC.

- **Syntax**:  `/npc info (npc)`
- **Permissions**: `fancynpcs.command.npc.info`


## Customize npcs

### Set type

Changes the type of the NPC.

- **Syntax**:  `/npc type (npc) (type)`
- **Permissions**: `fancynpcs.command.npc.type`

### Set display name

Changes displayname of the NPC. Supports MiniMessage, PlaceholderAPI and MiniPlaceholders.

- **Syntax**:  `/npc displayname (npc) (@none | name)`
- **Permissions**: `fancynpcs.command.npc.displayname`
- Empty message placeholder is `@none`

### Set skin

Changes skin of the NPC.

- **Syntax**:  `/npc skin (npc) (@none | @mirror | name | url | file name) [--slim]`

``@none`` - Removes skin.

``@mirror`` - Mirrors the skin of the player viewing the npc.

``name`` / ``uuid`` - Changes skin to the specified player's skin.

``url`` - Changes skin to the specified URL. The URL must point to a valid skin-image.

``file name`` - Changes skin to the specified image. The image must be located in the ``plugins/FancyNpcs/skins``
directory. Please only provide the file name (e. g. "cool-skin.png").

``--slim`` - Only works for skins set by an URL or file.

- **Permissions**: `fancynpcs.command.npc.skin`

### Set equipment

``set`` - Sets equipment slot of the NPC to item currently held in main hand, none or a specific item type.

``clear`` - Clears equipment slot of the NPC.

``list`` - Lists all equipment slots of the NPC.

- **Syntax**: `/npc equipment (npc) (set | clear | list)`
- **Permissions**: `fancynpcs.command.npc.equipment.(sub)`
- Accepts either `@none`, `@hand` or valid item type as an argument.

### Toggle glowing

Changes glowing state and color of the NPC.

- **Syntax**:  `/npc glowing (npc) [disabled | color]`
- **Permissions**: `fancynpcs.command.npc.glowing`

### Show npc in tab

Changes whether the NPC is shown in the player-list. This works only on NPCs of PLAYER type.

Re-connecting to the server might be required for changes to take effect.

- **Syntax**:  `/npc show_in_tab (npc) [state]`
- **Permissions**: `fancynpcs.command.npc.show_in_tab`

### Toggle collidable

Changes whether the NPC can collide with other entities.

- **Syntax**:  `/npc collidable (npc) [state]`
- **Permissions**: `fancynpcs.command.npc.collidable`

### Edit size

Changes the scale of the size of the NPC.

!!!warning
Only available for 1.20.5 or newer.
!!!

- **Syntax**:  `/npc scale (npc) (factor)`
- **Permissions**: `fancynpcs.command.npc.scale`

### Set attributes

Sets an attribute of the NPC.

- **Syntax**:  `/npc attribute (npc) (set | list)`
- **Permissions**: `fancynpcs.command.npc.attribute.(sub)`

### Set visibility

Controls who can see the NPC through visibility modes.

- **Syntax**:  `/npc visibility (npc) (all | permission_required | manual)`
- **Permissions**: `fancynpcs.command.npc.visibility`

**Visibility Modes:**

`all` - Everyone can see the NPC (default behavior).

`permission_required` - Players need the permission `fancynpcs.npc.<npc_name>.see` to see this NPC.

`manual` - Players must be manually added through the API to see the NPC. This mode is intended for API usage and allows developers to control visibility programmatically.

!!!info
The `manual` visibility mode provides the most granular control and is designed for advanced integrations. See the [API documentation](../api/getting-started.md) for details on managing manual visibility through code.
!!!

## Npc location and rotation

### Turn npc to player

Changes whether the NPC should turn to the player when in range.

- **Syntax**:  `/npc turn_to_player (npc) [state]`
- **Permissions**: `fancynpcs.command.npc.turn_to_player`

### Turn npc to player distance

Changes the distance at which the NPC should turn to the player.

- **Syntax**:  `/npc turn_to_player_distance (npc) [distance]`
- **Permissions**: `fancynpcs.command.npc.turn_to_player_distance`

### Move npc to your location

Teleports specified NPC to your location.

- **Syntax**:  `/npc move_here (npc)`
- **Permissions**: `fancynpcs.command.npc.move_to`

### Move npc to a location

Teleports NPC to specified location.

- **Syntax**:  `/npc move_to (npc) (x) (y) (z) [world] [--look-in-my-direction]`
- **Permissions**: `fancynpcs.command.npc.move_to`

### Rotate npc

Sets the yaw and pitch of the specified NPC.

- **Syntax**:  `/npc rotate (npc) (yaw) (pitch)`
- **Permissions**: `fancynpcs.command.npc.rotate`

### Center npc location

Centers the NPC to the specified location.

- **Syntax**:  `/npc center (npc)`
- **Permissions**: `fancynpcs.command.npc.center`

### Show nearby npcs

Lists all NPCs in your world. Can be filtered and sorted.

- **Syntax**:  `/npc nearby [--radius (radius)] [--type (type)] [--sort (sort)]`
- **Permissions**: `fancynpcs.command.npc.nearby`

### Teleport to npc

Teleports you to the specified NPC.

- **Syntax**:  `/npc teleport (npc)`
- **Permissions**: `fancynpcs.command.npc.teleport`
- Now **teleports sender** to the NPC. Teleportng **the NPC** to specified location is now handled by `/npc move_to`
  command.

## Manage interactions

!!!
Learn more about actions and triggers in the [Action System](../tutorials/action-system.md) tutorial.
!!!

### Add action

Adds an action to the specified NPC's trigger.

- **Syntax**: `/npc action (npc) (trigger) add (actionType) [value]`
- **Permissions**: 
  - `fancynpcs.command.npc.action.add`
  - and `fancynpcs.command.npc.action.add.(actionType | *)` for the specific action type

### Add action before

Adds an action before the specified index in the NPC's action list for the given trigger.

- **Syntax**: `/npc action (npc) (trigger) add_before (index) (actionType) [value]`
- **Permissions**: 
  - `fancynpcs.command.npc.action.addBefore`
  - and `fancynpcs.command.npc.action.add.(actionType | *)` for the specific action type

### Add action after

Adds an action after the specified index in the NPC's action list for the given trigger.

- **Syntax**: `/npc action (npc) (trigger) add_after (index) (actionType) [value]`
- **Permissions**: 
  - `fancynpcs.command.npc.action.addAfter`
  - and `fancynpcs.command.npc.action.add.(actionType | *)` for the specific action type

### Set action

Sets an action at the specified number in the NPC's action list for the given trigger.

- **Syntax**: `/npc action (npc) (trigger) set (number) (actionType) [value]`
- **Permissions**: 
  - `fancynpcs.command.npc.action.set`
  - and `fancynpcs.command.npc.action.add.(actionType | *)` for the specific action type

### Remove action

Removes an action from the NPC's action list based on the specified number for the given trigger.

- **Syntax**: `/npc action (npc) (trigger) remove (number)`
- **Permissions**: `fancynpcs.command.npc.action.remove`

### Move action up

Moves the action at the specified number up in the NPC's action list for the given trigger.

- **Syntax**: `/npc action (npc) (trigger) move_up (number)`
- **Permissions**: `fancynpcs.command.npc.action.moveUp`

### Move action down

Moves the action at the specified number down in the NPC's action list for the given trigger.

- **Syntax**: `/npc action (npc) (trigger) move_down (number)`
- **Permissions**: `fancynpcs.command.npc.action.moveDown`

### Clear actions

Clears all actions from the NPC for the given trigger.

- **Syntax**: `/npc action (npc) (trigger) clear`
- **Permissions**: `fancynpcs.command.npc.action.clear`

### List actions

Lists all actions for the specified NPC and trigger.

- **Syntax**: `/npc action (npc) (trigger) list`
- **Permissions**: `fancynpcs.command.npc.action.list`

### Interaction cooldown

Changes duration between interactions (cooldown) of the NPC.

- **Syntax**:  `/npc interaction_cooldown (npc) (disabled | cooldown)`
- **Permissions**: `fancynpcs.command.npc.interaction_cooldown`
- Formerly known as `/npc interactionCooldown`.
- Uses time duration instead of a number of seconds eg. `2min`. Supported units:
    - milliseconds: `ms`
    - seconds: `s`
    - minutes: `min`
    - hours: `h`
    - days: `d`
    - months: `mo`
    - years: `y`
