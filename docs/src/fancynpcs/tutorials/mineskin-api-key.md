---
icon: dot
---

# MineSkin API Key

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/minecraft-plugins/fancynpcs](https://fancyinnovations.com/docs/minecraft-plugins/fancynpcs).
!!!

When you have many npcs with skins (especially skins by file or url), you will notice that it takes a while once they
are all loaded. The reason for that is the rate-limit by MineSkin. We use MineSkin to generate the necessary skin data.
At the moment they have a limit of **10 skins per minute**. Using an MineSkin API Key does increase the limit to **30
skins per minute**. So if you have many skins, I recommend to get a MineSkin API key and use it.

### How to get a MineSkin API Key

1. Login on the [MineSkin Website](https://account.mineskin.org/keys/)
2. Click on the plus icon to create a new key
3. Give the key a name (can be anything)
4. Copy the key
5. Open the `plugins/FancyNpcs/config.yml` config file
6. Set the key at `mineskin_api_key`
7. Restart the Minecraft server
