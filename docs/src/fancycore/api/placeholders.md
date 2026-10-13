---
icon: dot
order: 7
---

# Placeholder API

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/hytale-plugins/fancycore](https://fancyinnovations.com/docs/hytale-plugins/fancycore).
!!!

FancyCore includes a Placeholders API that allows you to create and manage custom placeholders for use in various plugins.

## Parse placeholders

To parse placeholders in a string, use the `PlaceholderService`'s `parse` method. Here's an example:

```java
PlaceholderService placeholderService = PlaceholderService.get();
String parsedString = placeholderService.parse(player, "Hello, {player_name}! Your rank is {player_rank}.");
```

## Creating a custom placeholder

To create a custom placeholder, implement the `PlaceholderProvider` interface and register it with the `PlaceholderService`. Here's an example of creating a simple placeholder that returns a player's rank:

```java
public class PlayerRankPlaceholder implements PlaceholderProvider {

    @Override
    public String getName() {
        return "Player Rank Placeholder";
    }

    @Override
    public String getIdentifier() {
        return "player_rank";
    }

    @Override
    public String parse(FancyPlayer player, String input) {
        return player.getRank().getName();
    }
}
```

You can then register your placeholder provider with the `PlaceholderService`:

```java
PlaceholderService placeholderService = PlaceholderService.get();
placeholderService.registerPlaceholderProvider(new PlayerRankPlaceholder());
```