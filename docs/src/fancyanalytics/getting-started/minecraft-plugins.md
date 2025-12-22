---
icon: dot
order: 400
---

# Minecraft Plugins

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/web-services/fancyanalytics](https://fancyinnovations.com/docs/web-services/fancyanalytics).
!!!

!!!danger
The FancyAnalytics platform is still in development. Expect breaking changes in the future.
!!!

If you are a Minecraft plugin developer, you might want to track metrics about your plugin usage.
So you know how many people are using your plugin and how they are using it.
We provide a Java SDK which is specifically designed for Minecraft plugins.

## Include the FancyAnalytics API

### Gradle

```kotlin
repositories {
    maven("https://repo.fancyinnovations.com/releases")
}
```

```kotlin
dependencies {
    implementation("de.oliver.fancyanalytics:mc-api:VERSION")
}
```

### Maven

```xml
<repository>
    <id>fancyplugins-releases</id>
    <name>FancyPlugins Repository</name>
    <url>https://repo.fancyinnovations.com/releases</url>
</repository>
```

```xml
<dependency>
    <groupId>de.oliver.FancyAnalytics</groupId>
    <artifactId>mc-api</artifactId>
    <version>VERSION</version>
</dependency>
```

!!!warning
Make sure to shade the API into your plugin! You can use the [Shade plugin](https://gradleup.com/shadow/) for this.
!!!

## Initialize the API

```java
FancyAnalyticsAPI fancyAnalytics = new FancyAnalyticsAPI("project-id", "api-token");
fancyAnalytics.registerMinecraftPluginMetrics();
fancyAnalytics.initialize();
```

You can find your project ID and api token in the project settings page.

### Custom metrics

You can also send custom metrics to the server:

```java
// Register a number metric to track the amount of npcs
fancyAnalytics.registerNumberMetric(new MetricSupplier<>("amount_npcs", () -> npcManager.getNpcs().size()));

// Register a string metric to track the used language
fancyAnalytics.registerStringMetric(new MetricSupplier<>("language", () -> languageManager.getLanguage()));
```

You can also send multiple values at once with the `registerStringArrayMetric` and `registerNumberArrayMetric` methods.
This is useful for tracking the player client's version for example.

Make sure to add the metrics on website at the project settings page (must be same name as in the code)!

### Events

Some things are better tracked as events. For example, purchases in a shop. You can send events like this:

```java
fancyAnalytics.sendEvent(
    new Event("PurchasedItem")
        .withProperty("player","Steve")
        .withProperty("item","Diamond")
        .withProperty("amount","1")
        .withProperty("price","5")
);
```

You can also add custom properties to the event. Each property has a key (string) and a value (string but can be
converted to a number if needed).

Once the first event is sent, there will be a new event-type created on the website. This event-type will have the name
and the keys of all properties of the event. You can then see the events on the website.

You do not need to add the events on the website, they will be created automatically and all properties will be updated
automatically as well.

### Error reporting

FancyAnalytics can also track errors in your project. All you need to do is register all relevant loggers:

```java
Logger myLogger = ...;
fancyAnalytics.getExceptionHandler().registerLogger(myLogger);
``` 

All exceptions that are thrown in the logger and are related to the plugin will be tracked. You can see all thrown
exceptions on the website.

### Done!

This is all you need to do to get started with FancyAnalytics. It will automatically send the data to the server.