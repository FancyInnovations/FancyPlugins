<div align="center">

![FancyHolograms Banner](https://fancyinnovations.com/logos-and-banners/fancyholograms-banner.png)

<br />

Simple, lightweight and feature-rich hologram plugin for **[Paper](https://papermc.io/software/paper)** (and **[Folia](https://papermc.io/software/folia)**) servers using **[display entities](https://minecraft.wiki/w/Display)**
and packets.

</div>

## Features

With this plugin you can create holograms with customizable properties like:

- **Hologram Type** (text, item or block)
- **Position**, **Rotation** and **Scale**
- **Text Alignment**, **Background Color** and **Shadow**.
- **Billboard** (fixed, center, horizontal, vertical)
- **Multiple pages** (3 cycle modes)
- **Display file content** (any file path)
- **Interact with holograms** (send messages, run commands ...)
- **[MiniMessage](https://docs.papermc.io/adventure/minimessage/)** formatting (but also legacy formatting)
- **[PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI)** and **[MiniPlaceholders](https://github.com/MiniPlaceholders/MiniPlaceholders)** support
- **[FancyNpcs](https://modrinth.com/plugin/fancynpcs)** integration (link npcs with holograms)
- ...and much more!

Check out **[images section](#images)** down below.

<br />

## Installation

Paper **1.21.5** or newer with **Java 25** (or higher) is required.
The plugin should also work on **Paper** forks, but this is not tested.

**Spigot** is **not** supported.

<br />

## Documentation

Official documentation is hosted **[here](https://fancyinnovations.com/docs/minecraft-plugins/fancyholograms)**. Quick reference:

- **[Getting Started](https://fancyinnovations.com/docs/minecraft-plugins/fancyholograms/getting-started)**
- **[Command Reference](https://fancyinnovations.com/docs/minecraft-plugins/fancyholograms/commands/hologram)**
- **[Using API](https://fancyinnovations.com/docs/minecraft-plugins/fancyholograms/api/getting-started)**

**Have more questions?** Feel free to ask them on our **[Discord](https://discord.gg/ZUgYCEJUEx)** server.

<br />

## Developer API

More information can be found in **[Documentation](https://fancyinnovations.com/docs/minecraft-plugins/fancyholograms/api/getting-started)** and **[Javadocs](https://repo.fancyinnovations.com/javadoc/releases/de/oliver/FancyHolograms/latest)**.

### Maven

```xml
<repository>
    <id>fancyinnovations-releases</id>
    <name>FancyInnovations Repository</name>
    <url>https://repo.fancyinnovations.com/releases</url>
</repository>
```

```xml
<dependency>
    <groupId>de.oliver</groupId>
    <artifactId>FancyHolograms</artifactId>
    <version>[VERSION]</version>
    <scope>provided</scope>
</dependency>
```

### Gradle

```groovy
repositories {
    maven("https://repo.fancyinnovations.com/releases")
}

dependencies {
    compileOnly("de.oliver:FancyHolograms:[VERSION]")
}
```

<br />

## Images

Images showcasing the plugin, sent to us by our community.

![Screenshot 1](https://github.com/FancyMcPlugins/FancyHolograms/blob/main/images/screenshots/example1.jpeg?raw=true)  
<sup>Provided by [@OliverSchlueter](https://github.com/OliverSchlueter)</sup>

![Screenshot 2](https://github.com/FancyMcPlugins/FancyHolograms/blob/main/images/screenshots/example2.jpeg?raw=true)  
<sup>Provided by [@OliverSchlueter](https://github.com/OliverSchlueter)</sup>

![Screenshot 3](https://github.com/FancyMcPlugins/FancyHolograms/blob/main/images/screenshots/example3.jpeg?raw=true)  
<sup>Provided by [@OliverSchlueter](https://github.com/OliverSchlueter)</sup>

![Screenshot 4](https://github.com/FancyMcPlugins/FancyHolograms/blob/main/images/screenshots/example4.jpeg?raw=true)  
<sup>Provided by [@OliverSchlueter](https://github.com/OliverSchlueter)</sup>

![Screenshot 5](https://github.com/FancyMcPlugins/FancyHolograms/blob/main/images/screenshots/example5.jpeg?raw=true)  
<sup>Provided by [@OliverSchlueter](https://github.com/OliverSchlueter)</sup>

## Data collection

This plugin collects anonymous usage data to help us understand how the plugin is being used and to improve it. All collected data by all servers is aggregated, we don't store any IP addresses, server names, or any other information that could be used to identify a specific server or user.
Read more about that topic (used platforms, how to opt-out, list of metrics ...) in the [documentation](https://fancyinnovations.com/docs/minecraft-plugins/data-collection).