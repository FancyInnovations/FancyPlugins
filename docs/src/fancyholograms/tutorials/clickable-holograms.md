---
icon: dot
---

# Clickable holograms

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/minecraft-plugins/fancyholograms](https://fancyinnovations.com/docs/minecraft-plugins/fancyholograms).
!!!

Interactions with holograms is not a feature of FancyHolograms yet, but there is a workaround for this.

1. Create your hologram with the text you want.
2. Install [FancyNpcs](../../fancynpcs/getting-started.md)
3. Create a new npc and set the type to "INTERACTION" (`/npc type (npc name) INTERACTION`)
4. Remove the name tag of the npc (`/npc displayname (npc name) <empty>`)
5. Position the npc to the same location as the hologram (press F3+B to see the hitbox)
6. Modify the size of the hitbox to fit the hologram (`/npc attributes (npc name) set (width/height) (size)`)
7. Add an interaction action to the npc ([action system](../../fancynpcs/tutorials/action-system.md))