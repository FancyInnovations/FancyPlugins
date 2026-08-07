# FancyNpcsModel (fork with ModelEngine support)

Addon for [FancyNpcs](https://modrinth.com/plugin/fancynpcs) that lets you attach custom 3D models to NPCs.

This is a fork of [FancyNpcs Custom Models](https://modrinth.com/plugin/fancynpcscustommodels) that adds
**[ModelEngine](https://mythiccraft.io/index.php?resources/model-engine%E2%80%94ultimate-entity-model-manager.389/)**
support next to the existing **[BetterModel](https://modrinth.com/plugin/bettermodel)** support.

## Requirements

- Paper/Folia for Minecraft 26.2
- FancyNpcs 2.10.1+
- At least one model plugin:
    - BetterModel 3.1.0+ and/or
    - ModelEngine R4.1.1+

## Usage

1. Import/register your models in BetterModel or ModelEngine as usual.
2. Create an NPC and make sure it is of type `player`.
3. Apply a model: `/npc custom_model (npc) (model name)`
4. Remove the model again with `/npc custom_model (npc) @none`

### Provider resolution

- Unprefixed model names are looked up in **BetterModel first**, then in **ModelEngine**.
- To force a specific provider, prefix the model name:
    - `bm:my_model` or `bettermodel:my_model`
    - `me:my_model`, `meg:my_model` or `modelengine:my_model`

### Animations

- `/npc play_animation (npc) (animation)` - optionally add `--loop` to loop the animation
- NPC actions: `play_animation_once` and `play_animation_loop` (the animation name is the action value)

## How the ModelEngine integration works

FancyNpcs NPCs are packet based and don't really exist in the world, so ModelEngine cannot track
them like regular entities. When a ModelEngine model is applied:

- the NPC entity is made invisible (the nametag stays - use `/npc displayname (npc) <empty>` to hide it),
- a ModelEngine `Dummy` base entity carrying the model is created at the NPC's location,
- the dummy follows the NPC's location (e.g. after `/npc teleport` or `/npc moveHere`),
- clicks on the model's hitbox and on the invisible NPC both trigger the NPC's interactions,
- the NPC's `scale` is applied to the model and its hitbox.

Limitations (same as with BetterModel): `displayname`, `equipment`, `glowing`, `skin` and
`turn_to_player` don't affect the model. Clear the NPC's equipment before applying a model,
otherwise held/worn items will still be rendered on the invisible NPC.

## Building

```
./gradlew :plugins:fancynpcs-model:shadowJar
```

The jar is created in `plugins/fancynpcs-model/build/libs/`.
