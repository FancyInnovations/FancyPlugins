---
icon: dot
order: 10
---

# Getting started

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/hytale-plugins/fancycore](https://fancyinnovations.com/docs/hytale-plugins/fancycore).
!!!

## Include the API in your project

To include the FancyCore API in your project, you need to add the following dependency to your `build.gradle.kts` or `pom.xml` file.

**Gradle:**
```kotlin
repositories {
    maven("https://repo.fancyinnovations.com/releases")
}
```

```kotlin
dependencies {
    compileOnly("com.fancyinnovations:FancyCore:VERSION")
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
    <groupId>com.fancyinnovations</groupId>
    <artifactId>FancyCore</artifactId>
    <version>VERSION</version>
    <scope>provided</scope>
</dependency>
```

Replace `VERSION` with the version of the API you want to use. You can find the latest version on the download pages or in the GitHub releases.


## JavaDocs and help

You can find the JavaDocs for the FancyDialogs API [here](https://repo.fancyinnovations.com/javadoc/releases/com/fancyinnovations/FancyCore/latest).

Join the [FancyInnovations Discord](https://discord.gg/ZUgYCEJUEx) for help and support. There is a dedicated channel for help about the api (`#core-api`).
```