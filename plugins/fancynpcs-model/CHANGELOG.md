# 1.2.0

## New features

- Added support for ModelEngine (Model Engine R4.1.1+) as a second model provider next to BetterModel
- Model providers are now resolved automatically: unprefixed model names are looked up in BetterModel first, then in ModelEngine
- Model names can be prefixed to force a specific provider (e.g. `bm:my_model` / `bettermodel:my_model` or `me:my_model` / `modelengine:my_model`)
- BetterModel is no longer a required dependency - the plugin works with BetterModel, ModelEngine or both installed
- `/npc custom_model`, `/npc play_animation` and the `play_animation_once` / `play_animation_loop` actions work with both providers

## Notes

- When a ModelEngine model is applied, the NPC entity is made invisible and a ModelEngine dummy carrying the model is kept in sync with the NPC's location
- Clicks on ModelEngine hitboxes and on the (invisible) NPC itself both trigger the NPC's interactions
- Like with BetterModel, the following NPC features don't affect ModelEngine models: displayname, equipment, glowing, skin, turn_to_player
