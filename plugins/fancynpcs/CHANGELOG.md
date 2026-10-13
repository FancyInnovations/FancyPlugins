# FancyNPCs 3.0.0

Major release - FancySitula migration and new entity attributes.

---

## Breaking Changes

### Package Restructure
- `de.oliver.fancynpcs` -> `com.fancyinnovations.fancynpcs`
- All API imports require update


### Removed Modules
12 per-version implementation modules removed:
- `implementation_1_20`, `implementation_1_20_1`, `implementation_1_20_2`, `implementation_1_20_4`, `implementation_1_20_6`
- `implementation_1_21_1`, `implementation_1_21_3`, `implementation_1_21_4`, `implementation_1_21_5`, `implementation_1_21_6`, `implementation_1_21_9`, `implementation_1_21_11`

Replaced by FancySitula packet abstraction.

---

## New Features

### New API
- `NpcBuilder` - Fluent builder for NPC creation
- `NpcController` - Player visibility and data updates
- `NpcRegistry` - Centralized NPC registration
- `NpcStorage` - Pluggable storage backend

### New Events
- `NpcDeleteEvent` - Before NPC removal
- `NpcDespawnEvent` - NPC hidden from player

### New Entity Attributes

| Entity                  | Attributes           |
|-------------------------|----------------------|
| Snow Golem              | `pumpkin`            |
| Bat                     | `resting`            |
| Polar Bear              | `standing`           |
| Pufferfish              | `puff_state`         |
| Salmon                  | `salmon_size`        |
| Enderman                | `creepy`             |
| Ghast                   | `charging`           |
| Guardian/Elder Guardian | `moving`             |
| Warden                  | `anger_level`        |
| Turtle                  | `laying_egg`         |
| Witch                   | `drinking`           |
| Creeper                 | `powered`, `ignited` |
| Strider                 | `saddle`, `cold`     |
| Zombie/Husk             | `converting`         |
| Skeleton                | `stray_conversion`   |

### Extended Attributes
- `saddle` - Now supports Skeleton Horse, Zombie Horse, Donkey, Mule, Camel

---

## Architecture

- FancySitula packet abstraction replaces per-version NMS
- Single `Attributes.java` for all versions
- `NpcAttributeHandler` applies attributes via packets
- Unified attribute system across all supported versions

---

## Compatibility

**Versions**: 1.21.3, 1.21.4, 1.21.5, 1.21.6, 1.21.9, 1.21.11

**Java**: 21+

**Server**: Paper (Spigot not supported)

---

## Migration

1. Update imports: `de.oliver.fancynpcs` -> `com.fancyinnovations.fancynpcs`
2. Use `NpcBuilder.create()` for NPC creation
3. Update event listener package paths
