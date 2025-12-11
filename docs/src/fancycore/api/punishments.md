---
icon: dot
order: 8
---

# Punishment API

FancyCore provides a comprehensive Punishment API that allows you to manage player punishments such as warnings, kicks, mutes, and bans.

## Using the Punishment Service

With the Punishment Service, you can easily issue various types of punishments to players. Below are examples of how to use the Punishment Service:

```java
PunishmentService punishmentService = PunishmentService.get();

punishmentService.warnPlayer(player, staff, "reason", duration);

punishmentService.mutePlayer(player, staff, "reason"); // Permanent mute
punishmentService.mutePlayer(player, staff, "reason", 1000*60*60*4); // Temporary mute (4 hours)

punishmentService.kickPlayer(player, staff, "reason");

punishmentService.banPlayer(player, staff, "reason", duration); // Permanent ban
punishmentService.banPlayer(player, staff, "reason", 1000*60*60*24); // Temporary ban (1 day)
```

You can also retrieve existing punishments for a player:

```java
List<Punishment> punishments = punishmentService.getPunishmentsForPlayer(player);
```

Player reports are also managed through the Punishment Service:

```java
punishmentService.reportPlayer(player, staff, "reason");
```