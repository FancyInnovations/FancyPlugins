---
order: 6
icon: book
---
# FAQ

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/minecraft-plugins/fancyholograms](https://fancyinnovations.com/docs/minecraft-plugins/fancyholograms).
!!!

### How to modify each line in a hologram?

Per-line settings (such as scale or background) are not supported in FancyHolograms due to a limitation with display entities.

A separate hologram will need to be created for each line.

### How to add a blank line?

To add a blank line in a hologram, use `<reset>` on a new line.

### How to make holograms clickable?

Holograms currently aren't clickable themselves, but [here's](tutorials/clickable-holograms.md) a workaround

### How to make the hologram not to rotate?

To make a hologram not rotate, the billboarding must be set to FIXED.

Example: `/holo edit <hologram> billboard FIXED`

Once complete, you must set the hologram's rotation with the rotate and rotatepitch commands.

### How to edit the holograms via the data file?

1. Run /fancyholograms save
2. Back up the holograms.yml file in case something goes wrong
3. Edit your holograms.yml file as desired
4. Run /fancyholograms reload after saving the file