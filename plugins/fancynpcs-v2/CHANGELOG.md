- Added support for Minecraft 26.1
- Removed support for 1.21 and 1.21.1 (1.21.2 and 1.21.3 support will be removed in late April)
- Added Citizens -> FancyNpcs converter ([see tutorial](https://fancyinnovations.com/docs/minecraft-plugins/fancynpcs/tutorials/citizens-converter))
- Added `need_item` action ([see tutorial](https://fancyinnovations.com/docs/minecraft-plugins/fancynpcs/tutorials/action-system#need_item))
- Internally refactored the action system to support [Kite](https://echonine.dev/kite/getting-started/) scripts ([see tutorial](https://fancyinnovations.com/docs/minecraft-plugins/fancynpcs/tutorials/action-scripting))
- Made registry based attributes support identifiers from namespaces other than 'minecraft' ([#203](https://github.com/FancyInnovations/FancyPlugins/pull/203))
- Fixed some npc visibility issues when using Folia

**Reminder to everyone who uses the FancyNpcs Java API:**

If you're still using the old URL to our maven repository, please update to the new one.

Old repo url: `https://repo.fancyplugins.de/releases`
New repo url: `https://repo.fancyinnovations.com/releases`

Both URLs point to the same underlying repo, so nothing should break. You just need to replace the url.

The old repo url will stop working in about **60 days**.