---
icon: dot
order: 500
---

# Metrics

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/web-services/fancyanalytics](https://fancyinnovations.com/docs/web-services/fancyanalytics).
!!!

Metrics are **numerical values** that represent the state, performance, or behavior of your application over time. 
They are the core building blocks of the analytics platform — enabling insights, dashboards, alerts, and aggregation across many data sources.

## What is a Metric?

A **metric** is:

- Identified by a **name** (e.g. `cpu_usage_percent`, `online_players`, `npc_type_pig`)
- Reported by one or more **senders** (e.g., a game server, plugin, or application)
- Includes exact one **numeric value**

Each metric is sent at a point in time and represents a measurable value from your application or environment.

## What Can Be a Metric?

Anything that can be measured numerically:

| Metric Name         | Example Value | Description                         |
|---------------------|---------------|-------------------------------------|
| `cpu_usage_percent` | `42.5`        | Current CPU usage in percent        |
| `online_players`    | `10`          | Number of players online            |
| `npc_count`         | `123`         | Total NPCs spawned on a game server |
| `npc_count_pig`     | `5`           | Number of pig NPCs                  |
| `plugin_enabled`    | `1` (true)    | Boolean values are converted to 1/0 |
| `tps`               | `20`          | Ticks per second                    |
| `network_in_kb`     | `512`         | Incoming network traffic            |

