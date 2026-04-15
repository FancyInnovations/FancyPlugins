![FancyEconomy Banner](https://fancyinnovations.com/logos-and-banners/fancyeconomy-banner.png)

# 

<div style="display: flex; justify-content:space-evenly">

![Latest Version](https://fancyspaces.net/api/v1/badges/latest-version?space_id=fancyeconomy)

[![Discord](https://img.shields.io/discord/899740810956910683?color=7289da&logo=Discord&label=Discord&style=flat-square)](https://discord.gg/ZUgYCEJUEx)

![GitHub Downloads](https://img.shields.io/github/downloads/FancyMcPlugins/FancyEconomy/total?logo=GitHub&style=flat-square)

[![Modrinth Downloads](https://img.shields.io/modrinth/dt/fancyeconomy?color=00AF5C&label=modrinth&style=flat&logo=modrinth)](https://modrinth.com/plugin/fancyeconomy/versions)

[![FancySpaces Downloads](https://fancyspaces.net/api/v1/badges/downloads?space_id=fancyeconomy)](https://fancyspaces.net/spaces/fancyeconomy)

</div>

Simple economy plugin that lets you add multiple currencies.

**Only supported for Paper and Folia 26.1.2**. <br>
_The plugin might also work in older versions and using forks of Paper._

## Features

* Multiple currencies
* MySQL or SQLite storage
* Withdraw money notes
* Vault integration
* Placeholders for PlaceholderAPI

## Get the plugin

You can download the latest versions at the following places:

- FancySpaces: https://fancyspaces.net/spaces/fancyeconomy
- Modrinth: https://modrinth.com/plugin/fancyeconomy
- Hangar: https://hangar.papermc.io/Oliver/FancyEconomy
- Build from source

## Commands

- /FancyEconomy - displays the commands for this Plugin.
- /FancyEconomy reload - plugin config reload
- /FancyEconomy version - checks for a new version of the plugin
- /FancyEconomy currencies - shows a list of all currencies
- /Balance - shows your balance (default currency)
- /Balance (target) - shows a players balance (default currency)
- /Pay (target) (amount) - pays a certain amount of (default currency) to a specified player
- /Withdraw (amount) - Withdraw a certain amount of money
- /Balancetop (page) - Shows the richest players

For each currency there will a command:

- /(currency) balance - Shows your balance
- /(currency) balance (player) - Shows a player's balance
- /(currency) pay (player) (amount) - Pays money to a certain player
- /(currency) withdraw (amount) - Withdraw a certain amount of money
- /(currency) balancetop (page) - Shows the richest players
- /(currency) set (player) (amount) - Sets the balance of a certain player
- /(currency) add (player) (amount) - Adds money to a certain player
- /(currency) remove (player) (amount) - Removes money to a certain player

## Permissions

- `FancyEconomy.balance` - access to the /balance command<br>
- `FancyEconomy.pay` - access to the /pay command<br>
- `FancyEconomy.withdraw` - access to the /withdraw command<br>
- `FancyEconomy.admin` - access to the /FancyEconomy command<br>
- `FancyEconomy.<currency>` - access to the /(currency) balance|pay commands<br>
- `FancyEconomy.<currency>.admin` - access to the /(currency) set|add|remove commands<br>

## Placeholders

- `%FancyEconomy_balance%` - displays the player's balance (default currency)<br>
- `%FancyEconomy_balance_raw%` - displays the player's balance in a raw format (e.g. "5127422587,43")<br>
- `%FancyEconomy_balance_<currency>%` - displays the player's balance for a certain currency
- `%FancyEconomy_balance_raw_<currency>%` - displays the player's balance for a certain currency in a raw format

## Build from source

1. Clone this repo and run `gradlew shadowJar`
2. The jar file will be in `build/libs/FancyEconomy-<version>.jar`
