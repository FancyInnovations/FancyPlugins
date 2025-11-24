---
authors:
  - name: Oliver Schlüter
    email: oliver@fancyinnovations.com
    link: https://github.com/OliverSchlueter
    avatar: https://avatars.githubusercontent.com/u/79666085?v=4

date: 2025-11-24
title: "Introducing Hytale Plugins"
description: "The first Hytale plugin by FancyInnovations will be: FancyCore, a powerful plugin that provides a wide range of features and tools for Hytale server owners."
---

![](../static/blogs/introducing-hytale-plugins/hytale-plugins.png)

[Hytale](https://hytale.com/) is finally heading into early access, and I couldn’t be more excited. With that, I’m thrilled to announce that **FancyInnovations will be creating server-side plugins for the Hytale ecosystem**.

The moment I read the [“Hytale is saved!”](https://hytale.com/news/2025/11/hytale-is-saved) announcement, my brain immediately jumped into brainstorming mode.
After digging through the currently available information about Hytale modding, one thing became very clear:

**Hytale needs a plugin that provides a strong set of essential features — a foundation others can build on.**

And that’s exactly what I’m creating.

## Introducing FancyCore

With FancyCore I want to create a platform where gamemode specific plugins can be built on. It will cover many features you probably would expect from a core plugin.

Let’s look at some features:

- Teleportation (teleport, teleport request, teleport all, spawn, warps, player homes, …)
- World (create, teleport, change day time, weather, spawn protection, …)
- Inventory (kits, virtual backpacks, …)
- Economy (balance, pay, sell items, shops, …)
- Chat (chat format, player-to-player messages, nicknames, help, rules, broadcast, mute chat, clear chat, chat rooms, …)
- Moderation (report, warn, kick, mute, ban, chat / command history, vanish, sudo, …)
- Permissions (groups, permissions, …)
- Server (list online players, uptime, tps, mspt, cpu usage, ram usage, disk usage, …)
- Player (gamemode, playtime, fly, heal, feed, god, ping, …)
- Easy to use API

Notable API features:

- Permissions API
- Economy API
- Placeholder API

Other features for the future:

- Discord integration
- Built-in WebUI

This is a long list of features and not all will be available on day 1.
As I am writing this blog post, there is no public Hytale API documentation, so all I can do at the moment is write all the business logic and implement the actual commands and other Hytale specific features as soon as the API documentation is available.

For now, it will be a big all-in-one plugin, in the future I might create separate plugins for each category, but that's not final yet.

**FancyCore will be a Hytale only plugin**, I don’t plan to create a Minecraft plugin out of this at the moment. 
For now the plan is to get FancyCore working for Hytale as soon as possible while, but while keeping a good quality.

The plugin will be free and open-source, just like all Minecraft plugins. You can already take a look at the new mono-repo for Hytale plugins [here](https://github.com/FancyInnovations/HytalePlugins). 
Some parts that don’t require the Hytale server dependency are already being worked on.
Feel free to take a look and contribute to it if you want.

Parts of the documentation is also already written (mostly the commands). You can take a look at it here.

From what I’ve seen, you won’t be able to upload Hytale plugins to Modrinth (just speculations). 
The only popular platform that supports distribution of Hytale plugins is Curseforge. 
I'm currently not sure if I want to distribute FancyCore there.
In the meanwhile I’ve been programming a little download service myself to distribute Hytale plugins, so they will for sure be available on the fancyinnovations.com website.
I know the community is currently working on alternative platforms as well, so we will see how that develops.

## FancyAnalytics

Alongside FancyCore, I’ll also be releasing FancyAnalytics for Hytale — a toolset for gathering analytics from your Hytale server or plugins.

It will include:
* A Hytale plugin for collecting metrics such as online players, TPS, entity counts, CPU usage, and more
* An SDK for plugin developers to track usage metrics, including support for custom data points

This gives developers powerful insight into how their plugins are used — and helps server owners better understand performance.

## Conclusion

If you’re as excited as I am about Hytale — and FancyCore — then join our Discord community!
Assign yourself the “FancyCore” role to get all the latest updates.
I'm currently working on it daily and will share progress updates there.

If you have any suggestions or feature requests for FancyCore, feel free to share them in the `#core-discussion` channel or DM me directly (`real_oliver`).

There’s also a dedicated channel for general Hytale news.
(The release date for early access will be announced this week!)

*Oliver*