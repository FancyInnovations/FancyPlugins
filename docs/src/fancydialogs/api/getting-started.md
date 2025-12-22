---
icon: dot
---

# Getting started

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/minecraft-plugins/fancydialogs](https://fancyinnovations.com/docs/minecraft-plugins/fancydialogs).
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
    compileOnly("de.oliver:FancyDialogs:VERSION")
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
    <artifactId>FancyDialogs</artifactId>
    <version>VERSION</version>
    <scope>provided</scope>
</dependency>
```

Replace `VERSION` with the version of the API you want to use. You can find the latest version on the download pages or in the GitHub releases.

## Show a notice dialog

```java
new NoticeDialog("title", "message").show(player);
// or
NoticeDialog.show(player, "message");
```

## Show a confirmation dialog

```java
new ConfirmationDialog("Are you sure you want to reload the configuration?")
    .withTitle("Confirm reload")
    .withOnConfirm(() -> player.sendMessage("Reloading configuration..."))
    .withOnCancel(() -> player.sendMessage("Reload cancelled."))
    .ask(player);
```

## JavaDocs and help

You can find the JavaDocs for the FancyDialogs API [here](https://repo.fancyinnovations.com/javadoc/releases/de/oliver/FancyDialogs/latest).

Join the [FancyInnovations Discord](https://discord.gg/ZUgYCEJUEx) for help and support. There is a dedicated channel for help about the api (`#dialogs-api`).
```