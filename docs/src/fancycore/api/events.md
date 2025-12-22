---
icon: dot
order: 9
---

# Events API

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/hytale-plugins/fancycore](https://fancyinnovations.com/docs/hytale-plugins/fancycore).
!!!

FancyCore has its own event system that allows you to listen to various events that happen in the FancyCore system.

## Registering an event listener

Example for registering a listener for the `PlayerReportedEvent`:

```java
EventService eventService = EventService.get();

eventService.registerListener(PlayerReportedEvent.class, (event) -> {
    System.out.println("PlayerReportedEvent fired with report id: " + event.getReport().id());
});
```

## Available events

### Player events

| Event                 | Description                                                                   |
|-----------------------|-------------------------------------------------------------------------------|
| `PlayerModifiedEvent` | Event fired when a player's data is modified (through FancyPlayer's setters). |
| `PlayerReportedEvent` | Fired when a player is reported.                                              |
| `PlayerPunishedEvent` | Fired when a player receives a punishment (warning, kick, mute or ban).       |
