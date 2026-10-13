---
icon: dot
---

# Getting started

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/minecraft-plugins/fancynpcs](https://fancyinnovations.com/docs/minecraft-plugins/fancynpcs).
!!!

## Include the API in your project

To include the FancyNPCs API in your project, you need to add the following dependency to your `build.gradle.kts` or `pom.xml` file.

**Gradle:**
```kotlin
repositories {
    maven("https://repo.fancyinnovations.com/releases")
}
```

```kotlin
dependencies {
    compileOnly("de.oliver:FancyNpcs:VERSION")
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
    <artifactId>FancyNpcs</artifactId>
    <version>VERSION</version>
    <scope>provided</scope>
</dependency>
```

Replace `VERSION` with the version of the API you want to use. You can find the latest version on the download pages or in the GitHub releases.

## Create a new NPC

### 1. Create the NPC data

The `NpcData` class is used to store all the information about an NPC. You can create a new instance of `NpcData` by providing a name, the UUID of the creator, and the location where the NPC should be spawned.

```java
NpcData data = new NpcData("myNpc", creatorUUID, location);
data.setSkin("OliverHD"); // use skin of the player OliverHD
data.setDisplayName("<red>cool displayname</red>");
```

### 2. Create the NPC

You can use the NpcData object to create a new NPC. Because the implementation of the NPC is different for every Minecraft version, FancyNpcs provides a factory to create the NPC.

```java
Npc npc = FancyNpcsPlugin.get().getNpcAdapter().apply(data);
```

### 3. Register the NPC

To let FancyNpcs handle the NPC, you need to register it. FancyNpcs will take care of spawning, despawning, saving the NPC, and more.

```java
FancyNpcsPlugin.get().getNpcManager().registerNpc(npc);
```

!!!warning
Do not register npcs in the first few seconds after the server has started. You need to wait at least 10 seconds before registering npcs. Otherwise, the npcs will not get registered correctly.
You can also listen to the `NpcsLoadedEvent` to know when you can register npcs.
!!!

!!!
If you don't want to persist the npc, you can do the following: `npc.setSaveToFile(false);`
!!!

### 4. Initially spawn the NPC for all players

To spawn the NPC for all players, you can use the following methods.

```java
npc.create();
npc.spawnForAll();
```

## Modify an existing NPC

### 1. Get the NPC object by name

You can get an NPC object by its name. The name is unique for every NPC (unless the `player-npcs` feature flag is enabled). Alternatively, you can get an NPC by its ID.

```java
Npc npc = FancyNpcsPlugin.get().getNpcManager().getNpc("myNpc");
NpcData data = npc.getData();
```

### 2. Modify the NPC data

You can modify the NPC data object to change the NPC's properties.

```java
data.setDisplayName("<green>new displayname</green>");
data.setSkin("https://url-to-skin.com/skin.png");
```

### 3. Update the NPC

After you have modified the NPC data, you need to update the NPC for all players.

```java
npc.updateForAll();
```

!!!
Some changes require the NPC to be respawned. You can respawn the NPC for all players by doing the following:
```java
npc.removeForAll();
npc.spawnForAll();
```
!!!

## Remove an NPC

To remove an NPC, you can use the following method.

```java
FancyNpcsPlugin.get().getNpcManager().removeNpc(npc);
npc.removeForAll();
```

## JavaDocs and help

You can find the JavaDocs for the FancyNpcs API [here](https://repo.fancyinnovations.com/javadoc/releases/de/oliver/FancyNpcs/latest).

Join the [FancyInnovations Discord](https://discord.gg/ZUgYCEJUEx) for help and support. There is a dedicated channel for help about the api (`#npcs-api`).
```