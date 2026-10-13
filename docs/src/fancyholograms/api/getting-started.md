---
icon: dot
---

# Getting started

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/minecraft-plugins/fancyholograms](https://fancyinnovations.com/docs/minecraft-plugins/fancyholograms).
!!!

## Include the API in your project

To include the FancyHolograms API in your project, you need to add the following dependency to your `build.gradle.kts` or `pom.xml` file.

**Gradle:**
```kotlin
repositories {
    maven("https://repo.fancyinnovations.com/releases")
}
```

```kotlin
dependencies {
    compileOnly("de.oliver:FancyHolograms:VERSION")
}
```

**Maven:**
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
    <version>VERSION</version>
    <scope>provided</scope>
</dependency>
```

Replace `VERSION` with the version of the API you want to use. You can find the latest version on the download pages or in the GitHub releases.

## Create a new hologram

### 1. Create the hologram data

The TextHologramData class is used to store all the information about aa hologram. You can create a new instance of TextHologramData by providing a name, and the location where the hologram should be spawned.

```java
TextHologramData hologramData = new TextHologramData("hologram_name", location);
// Adjust the Hologram Data
hologramData.setBackground(TextColor.color(100, 255, 79));
hologramData.setBillboard(Display.Billboard.FIXED);
```

!!!
You can also use ItemHologramData or ItemHologramData to create a holograms with other types.
!!!

### 2. Create the hologram

You can use the TextHologramData object to create a new hologram. Because the implementation of the hologram different for every Minecraft version, FancyHolograms provides a factory to create the hologram.

```java
HologramManager manager = FancyHologramsPlugin.get().getHologramManager();
Hologram hologram = manager.create(data);
```

### 3. Register the hologram

To let FancyHolograms handle the hologram, you need to register it. FancyHolograms will take care of spawning, despawning, saving the hologram, and more.

```java
HologramManager manager = FancyHologramsPlugin.get().getHologramManager();
manager.addHologram(hologram);
```

!!!
If you don't want to persist the hologram, you can do the following: `hologram.setPersistent(false);`
!!!

## Modify an existing hologram

### 1. Get the hologram by name

You can get the hologram by its name. The name is unique and can be used to identify the hologram.

```java
HologramManager manager = FancyHologramsPlugin.get().getHologramManager();

Hologram hologram = manager.getHologram("hologram_name").orElse(null);
if (hologram == null) {
    // hologram not found
    return;
}
```

### 2. Modify the data

You can modify the hologram data object to change the holograms's properties.

```java
HologramData hologramData = hologram.getData();
hologramData.setBillboard(Display.Billboard.CENTER);

if (hologramData instanceof TextHologramData textData) {
        textData.setTextAlignment(TextDisplay.TextAlignment.LEFT);
}
```

### 3. Update the hologram

After modifying the hologram data, you need to update the hologram. This will apply the changes to the hologram.

```java
hologram.forceUpdate();
hologram.queueUpdate();
```

## Remove a hologram

To remove a hologram, you can use the `removeHologram` method of the `HologramManager`. This will remove the hologram and unregister it.

```java
HologramManager manager = FancyHologramsPlugin.get().getHologramManager();
manager.removeHologram("hologram_name");
```

## JavaDocs and help

You can find the JavaDocs for the FancyHolograms API [here](https://repo.fancyinnovations.com/javadoc/releases/de/oliver/FancyHolograms/latest).

Join the [FancyInnovations Discord](https://discord.gg/ZUgYCEJUEx) for help and support. There is a dedicated channel for help about the api (`#holograms-api`).
