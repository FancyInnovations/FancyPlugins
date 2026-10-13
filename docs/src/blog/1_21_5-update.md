---
authors:
  - name: Oliver Schlüter
    email: oliver@fancyinnovations.com
    link: https://github.com/OliverSchlueter
    avatar: https://avatars.githubusercontent.com/u/79666085?v=4

date: 2025-04-19
title: 1.21.5 Update (Spring to Life)
description: Everything you need to know about the 1.21.5 update.
---

![](../static/1_21_5-update.png)

In this article, we will talk about the changes in the 1.21.5 update, what that means for our plugins and how we will benefit from the Paper hardfork.

## :icon-log: Changes in 1.21.5

!!!
Official release notes can be found [here](https://www.minecraft.net/en-us/article/minecraft-java-edition-1-21-5).
!!!

### New Animal Variants

**Pigs, Cows, and Chickens** now have **warm and cold variants**, determined by the biome they spawn in.

- **Warm Variants**: Found in deserts, jungles, badlands, etc.
- **Cold Variants**: Found in snowy and End biomes.
- Chickens lay new **Blue (cold)** and **Brown (warm)** eggs.
- **Sheep** now spawn with wool color based on the biome's temperature:
  - **Cold**: Mostly black
  - **Warm**: Mostly brown
  - **Temperate**: Mostly white

### New Wolf Sound Personalities

Wolves now randomly get one of **seven unique sound sets**:
- Classic
- Big
- Cute
- Puglin
- Angry
- Grumpy
- Sad

Each variant has distinct ambient, growl, pant, whine, hurt, and death sounds.

### Spawn Egg Visual Overhaul

Spawn Eggs now visually reflect the **mob's personality and size**, improving readability and accessibility.

### New World Generation Features

**Fallen Trees**
- Found in most forested biomes.
- Come in **oak, birch, jungle**, and **spruce** variants.
- Sometimes decorated with mushrooms or vines.

**Firefly Bush**
- Grows near water in Swamps, Badlands, and Mangrove Swamps.
- Emits light and glowing particles at night.
- Can be bonemealed to spread.

**Leaf Litter**
- Found in Forests and Dark Forests.
- Placed in up to 4 layers per block.
- Tinted based on biome and usable as fuel or compost.

**Wildflowers**
- Found in Birch Forests, Meadows.
- Stackable flowers that can be bonemealed.
- Craftable into Yellow Dye.

**Bushes**
- Appear in patches in Windswept, Birch, and River biomes.
- Collectable with shears or Silk Touch.

**Dry Grass**
- Short and Tall variants now generate in deserts and badlands.
- Bonemealable and compostable.
- Eaten by Sheep.

**Cactus Flowers**
- Grow on Cactus blocks.
- Used for Pink Dye or compost.
- Require open space on all sides.

### Falling Leaf Particles

All **Leaf blocks** now randomly emit gentle falling leaf particles for added ambience.

### New Ambient Sounds

- **Desert and Badlands** blocks like sand and terracotta now emit subtle ambient sounds.
- Dead Bushes can also trigger audio ambiance when placed on terracotta or sand.

### Visual & Gameplay Tweaks

- **New main menu panorama** for Spring to Life.
- **Beacon beams** now render up to **2048 blocks high** and are thicker at long distances.
- **Lodestones** have a new crafting recipe and spawn in Ruined Portals.
- Updated textures for **Cows, Mooshrooms, and Sheep**.

### Cartographer & Wandering Trader Updates

**Cartographer**
- Sells 7 new maps pointing to structures in different biomes.
- Village-specific map offerings and colored banners.

**Wandering Trader**
- Now buys **basic supplies** like water bottles, hay bales.
- Offers new trades including **logs, potions**, and **enchanted tools**.

### Mob Spawning & Biomes

- **Camels** now spawn in Deserts.
- **Cows, Pigs, Chickens, and Sheep** can spawn in **Badlands**.
- **Woodland Mansions** can generate in the **Pale Garden** biome.
- **Meadows** have slightly sparser short grass.

###  Technical & Gameplay Improvements

- **Game Tests** are now accessible via datapacks using the `/test` command.
- **Simulation distance** improvements: tick behavior for crops, snow, and cauldrons extended to all loaded chunks.
- Various **networking, UI, and tooltip** enhancements.
- Sound rebalancing and expanded **Iron block sound types**.

### Dev Features & Fixes

- New `--renderDebugLabels` argument for developers.
- Enhanced **Game Test automation** with new command-line tools.
- Reverted fix to sprint slowdown mechanics to improve parkour experience.
- Minor fixes to **items, sounds, and Realms uploads**.

### And yes, as always...

> **Removed Herobrine.**

### Packet changes

Little has changed in the packet system.

**Removed packets**
- `ClientboundAddExperienceOrb`

**Added packets**
- `ClientboundTestInstanceBlocksStatus`
- `ServerboundSetTestBlock`
- `ServerboundTestInstanceBlockAction`

## :icon-megaphone: Changes in the plugins

### FancyNpcs

FancyNpcs now supports 1.21.5. Update to version [2.5.0](https://modrinth.com/plugin/fancynpcs/version/2.5.0) to get the new features and changes.

**New Attributes:**

| Npc Type | Attr. Name   | Possible Values       |
|----------|--------------|-----------------------|
| Pig      | variant      | temperate, cold, warm |
| Cow      | variant      | temperate, cold, warm |
| Chicken  | variant      | temperate, cold, warm |
| Cat      | collar_color | (all colors)          |
| Wolf     | collar_color | (all colors)          |


### FancyHolograms

FancyHolograms now supports 1.21.5. Update to version [2.5.0](https://modrinth.com/plugin/fancyholograms/version/2.5.0) to get the new features and changes.

There are no other changes in FancyHolograms.

## :icon-repo-forked: Paper Hardfork

You can read about the reasons and changes in this article on the PaperMC forum: [The future of Paper - Hard fork](https://forums.papermc.io/threads/the-future-of-paper-hard-fork.1451/).
It basically allows the Paper team to work on adding support for new versions after without having to wait on Spigot to update. This means that Paper is able to also update to snapshots, pre-releases and release candidates.

This is perfect for us, because we can as well update our plugins faster to new versions and snapshots faster. Once the Paper team has updated to a new version (realase or snapshot), we can immediately start working on updating our plugins.


## Conclusion

We are excited about the new changes and the Paper hardfork. We are looking forward to updating our plugins to the new version and snapshots faster than before.

Read the full release notes of the 1.21.5 update [here](https://www.minecraft.net/en-us/article/minecraft-java-edition-1-21-5).