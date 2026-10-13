---
icon: dot
order: 400
---

# Events

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/web-services/fancyanalytics](https://fancyinnovations.com/docs/web-services/fancyanalytics).
!!!

**Events** are time-stamped, structured logs that describe something that **happened** in your application. 
They are flexible, contextual, and great for tracking user behavior, system actions, feature usage, and more.

## What is an Event?

An **event** is:

- Identified by an **event name** (e.g. `npc_spawned`, `command_used`, `plugin_loaded`)
- Has a **timestamp**
- Sent by a **sender** (e.g., server, client, device)
- It Can include any number of **properties** (flexible key-value pairs)

Unlike metrics, events are **not numeric by default**, but rather represent **something that occurred**, and may include rich context.

## Example Use Cases

| Event Name          | Properties                      | Description                              |
|---------------------|---------------------------------|------------------------------------------|
| `npc_spawned`       | world_name, npc_type            | Fired whenever an NPC is spawned in-game |
| `feature_flag_used` | feature_flag                    | Tracked when a feature is enabled/used   |
| `plugin_loaded`     | plugin_version, server_software | When the plugin successfully loads       |
| `command_executed`  | command, player                 | A player runs a specific command         |
| `player_joined`     | player, location                | A player joins the server                |
