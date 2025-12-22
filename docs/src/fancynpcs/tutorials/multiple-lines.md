---
icon: dot
---

# Multiple display name lines

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/minecraft-plugins/fancynpcs](https://fancyinnovations.com/docs/minecraft-plugins/fancynpcs).
!!!

To have multiple lines as the display name of an NPC follow the following steps:

1. Download the [FancyHolograms](https://modrinth.com/plugin/fancyholograms/versions) plugin and put it in the `plugins`
   folder
2. Start your server and make sure the FancyNpcs and FancyHolograms plugin have both loaded
3. Create a hologram with multiple lines
4. Create a npc
5. Link the hologram with the npc `/hologram edit <hologram> linkWithNpc <npc>`
6. You can now move the npc around and the hologram will always follow
7. To unlink simply run `/hologram edit <hologram> unlinkWithNpc`