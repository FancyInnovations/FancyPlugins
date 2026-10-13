---
icon: dot
---

# Action system

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/minecraft-plugins/fancynpcs](https://fancyinnovations.com/docs/minecraft-plugins/fancynpcs).
!!!

The NPC Action System in FancyNPCs allows server administrators to create interactive and engaging NPCs by assigning actions that trigger based on player interactions or custom events. 
This article explains the general concept of the system and provides a guide on how server admins can leverage it through commands.

## Triggers

The NPC Action System revolves around assigning actions to an NPC based on triggers. Currently, the system supports the following triggers:

- **RIGHT_CLICK**: This trigger activates when a player right-clicks on the NPC.
- **LEFT_CLICK**: This trigger activates when a player left-clicks on the NPC.
- **ANY_CLICK**: This trigger activates when a player either right-clicks or left-clicks on the NPC.
- **CUSTOM**: Custom events triggered through the API.

Each trigger can execute a series of actions. Actions determine what the NPC does when the trigger is activated, such as sending messages, executing commands, teleporting players, or waiting for a certain duration before performing the next action. 
Administrators can add, modify, and reorder these actions using commands. (See the [commands page](../commands/npc.md#manage-interactions) for more details)

## Actions

The NPC Action System supports a variety of actions that can be executed when a trigger is activated. The following actions are currently available.

### message

Sends a message to the player who triggered the action. The message can be customized using any placeholder by PlaceholderAPI.

Syntax: `message (message ...)`

Example: `/npc action (npc) (trigger) add message <green>Hi, <b>%player_name%</b>! Welcome to our server!</green>`

### console_command

Executes a command on the server console. This action is useful for performing server-side operations or executing commands that require console privileges.

Syntax: `console_command (command ...)`

Example: `/npc action (npc) (trigger) add console_command give {player} diamond 1`

!!!
The `{player}` placeholder will be replaced with the name of the player who triggered the action. (PlaceholderAPI is not required for this specific placeholder)
!!!

### player_command

Executes a command as the player who triggered the action. This action is useful for performing player-specific operations or executing commands that require player privileges.

Syntax: `player_command (command ...)`

Example: `/npc action (npc) (trigger) add player_command warp spawn`

### player_command_as_op

Executes a command as the player who triggered the action, but with operator privileges. This action is useful for performing player-specific operations that require operator permissions.

!!!warning
FancyNpcs will gives the player operator permissions for the duration of the command execution. This means that the player will have access to all commands and permissions during that time.
This can be a security risk if not used carefully, so make sure to use this action only when necessary and with trusted players.
!!!

Syntax: `player_command_as_op (command ...)`

Example: `/npc action (npc) (trigger) add player_command_as_op give diamond 1`


### send_to_server

Sends the player to a different server. This action uses the plugin-messaging system to send the player to the specified server. Using Velocity as proxy is recommended for this action to work properly.

Syntax: `send_to_server (server name)`

Example: `/npc action (npc) (trigger) add send_to_server skyblock`

!!!info
Make sure to set `bungee-plugin-message-channel` to `true` in the velocity.toml config file. Otherwise, this action might not work properly.
!!!

### execute_random_action

Picks a random action from the remaining actions in the list and executes it. This action is useful for creating random behavior in NPCs.

Syntax: `execute_random_action`

Example:
- `/npc action (npc) (trigger) add message <gold>Hello!</gold>`
- `/npc action (npc) (trigger) add execute_random_action`
- `/npc action (npc) (trigger) add message <green>Message one</green>`
- `/npc action (npc) (trigger) add message <red>Message two</red>`
- `/npc action (npc) (trigger) add message <blue>Message three</blue>`

When the player interacts with the NPC, it will always send the "Hello!" message and then randomly picks one of remaining three messages and sends it to the player.

### wait

Pauses the action execution for a specified duration. This action is useful for creating delays between actions before proceeding.

Syntax: `wait (duration in seconds)`

Example: `/npc action (npc) (trigger) add wait 5` (waits for 5 seconds before executing the next action)

### block_until_done

When this action is added to the action list, a player will not be able to interact with the NPC until the previous action list is finished. This action is useful for preventing players from spamming actions or interacting with the NPC while an action is in progress.

Syntax: `block_until_done`

Example:
- `/npc action (npc) (trigger) add block_until_done`
- `/npc action (npc) (trigger) add message <green>Hi, <b>%player_name%</b>! Welcome to our server!</green>`
- `/npc action (npc) (trigger) add wait 5`
- `/npc action (npc) (trigger) add message <red>Goodbye!</red>`

When the player interacts with the NPC, it will send the "Hi!" message, wait for 5 seconds, and then send the "Goodbye!" message. During this time, the player will not be able to interact with the NPC again until the action list is finished.

### need_permission

Checks if the player has a specific permission. If the player does not have the permission, the action list will be canceled. This action is useful for creating permission-based interactions with NPCs.

Syntax: `need_permission (permission)`

Example: `/npc action (npc) (trigger) add need_permission my.cool.permission`

!!!info
If you add the `!` prefix to the permission, the action will be inverted. This means that the action list will be canceled if the player has the permission.
!!!

### play_sound

Plays a sound to the player. This action is useful for creating audio feedback for the player when interacting with the NPC.

Syntax: `play_sound (sound)`

Example: `/npc action (npc) (trigger) add play_sound entity.experience_orb.pickup`