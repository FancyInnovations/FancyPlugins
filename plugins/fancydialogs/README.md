<div align="center">

![Banner](../../../docs/src/static/logos-and-banners/fancydialogs-banner.png)

[![Supports Folia](https://img.shields.io/badge/folia-supported-%23F9D879?labelColor=%2313154E&color=%234A44A6)](https://papermc.io/software/folia)
[![Discord](https://img.shields.io/discord/899740810956910683?cacheSeconds=3600&logo=discord&logoColor=white&label=%20&labelColor=%235865F2&color=%23707BF4)](https://discord.gg/ZUgYCEJUEx)
[![Modrinth Downloads](https://img.shields.io/modrinth/dt/fancyholograms?logo=modrinth&logoColor=white&label=downloads&labelColor=%23139549&color=%2318c25f)](https://modrinth.com/plugin/fancydialogs)

[![Modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/modrinth_vector.svg)](https://modrinth.com/plugin/fancydialogs)
[![Hangar](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/hangar_vector.svg)](https://hangar.papermc.io/Oliver/FancyDialogs)

<br />

Simple and lightweight plugin to create and manage the new dialogs.
Because the dialogs were added in 1.21.6, only players on 1.21.6 or newer can view them.

You can create create dialogs (in JSON files) and then assign them to many cool features.

</div>

## Features

- Welcome dialog: shows when a player joins for the first time
- Tutorials: explain how your amazing server works in multiple chapters
- Quick action: assign one dialog to the quick-action hotkey ('G' by default)
- MiniMessage formatting is supported
- You can use placeholders by PlaceholderAPI and MiniPlaceholders
- Awesome API for other plugin developers to use

## Dialog structure

There are four different dialog types, you can choose from:

- Notice
- Confirmation
- Multi action
- Dialog list

Each dialog has the following properties:

- Title: will be shown at the top of the screen
- External title: will be shown when a button refers on this dialog
- Can close with tab: true / false
- Body: text and/or items
- Inputs: see section below

Input controls:

- Checkbox: can be true or false
- Slider (numbers): configure min, max and how many steps
- Combobox: choose one element from a list
- Text: the user can type any text (multiline boxes are supported)

## Installation

Paper **1.21.6** with **Java 21** (or higher) is required. Plugin should also work on **Paper** forks.

**Spigot** is **not** supported.

### Download

- **[Modrinth](https://modrinth.com/plugin/fancydialogs)**
- **[Hangar](https://hangar.papermc.io/Oliver/FancyDialogs)**
