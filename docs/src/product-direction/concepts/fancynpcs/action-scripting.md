---
icon: dot
---

# Action Scripting

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/general](https://fancyinnovations.com/docs/general).
!!!


Currently, the npc action system is quite limited with the available action types. There is no way to create conditional actions or to have loops.
This is where action scripting comes in. Action scripting allows you to create complex actions using a scripting language.

The idea is to have a directory (`plugins/FancyNpcs/scripts`) where Kotlin scripts can be placed. These scripts can then be executed by the npc action system.

There will be a new default action type called `run_script`, which will execute a script. The script file name will be passed as an argument to the action. 

The script will have full access to the current action context, which includes the npc, the player, and the action list. The script can also access to the Paper API.