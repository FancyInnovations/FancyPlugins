## Citizens -> FancyNpcs converter

How to use:
1. Have the latest version of Citizens (I used `2.0.41-b4129`) installed
2. Have the latest dev build of FancyNpcs installed
3. Run the `/npcconvert citizens` command
4. Check the result (`/fancynpcs:npc list`)
5. You can now delete the Citizens npcs and/or the Citizens plugin

These properties get converted:
* Npc owner
* Npc name (white spaces will be replaced with underscores)
* Mob type
* Skin (only tested with usernames so far)
* Look close (turn_to_player in fancynpcs)
* Glowing & glowing color
* Equipment
* Text -> `message` action with ANY_CLICK trigger
* Command -> `console_command` action with trigger defined as Hand

The converter does not convert mob type specific attributes yet.

Please leave feedback, bugs and issues about the converter in our Discord server.