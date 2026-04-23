This version primarily has API and internal changes, with no new features or bug fixes directly for FancyNpcs.

These changes made it possible to create the new FancyNpcsModel plugin, which is a separate addon for FancyNpcs that allows you to attach custom 3D models to npcs.
Currently, it only supports BetterModel, but in the future, it may support other plugins of this kind as well.

You can download FancyNpcsModel from Modrinth: https://modrinth.com/plugin/fancynpcsmodel

Please read the plugin description to see how to use it.

API Changes:

- Added NpcData#removeAttribute method to remove an attribute from an NPC
- Added option to provide possible values via Supplier in NpcAttribute
- Added NpcPreInteractEvent (primarily used for internal purposes) 
- Added FancyNpcsPlugin#registerCommand to register commands (from Cloud) to the FancyNpcs command system (useful for addons of FancyNpcs)
