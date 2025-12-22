---
icon: dot
order: 9
---

# Welcome dialog

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/minecraft-plugins/fancydialogs](https://fancyinnovations.com/docs/minecraft-plugins/fancydialogs).
!!!

You can configure a dialog to be shown to players when they join the server for the first time.
A default welcome dialog is included with FancyDialogs, but you can customize it or create your own welcome dialog.

You can find the default welcome dialog in the `plugins/FancyDialogs/data/dialogs/welcome.json` file.
Feel free to edit this file to customize the welcome dialog to your liking.

If you want to use another dialog as the welcome dialog, you can change the id in the `plugins/FancyDialogs/config.yml` file by changing the `welcome_dialog_id` option to the ID of your desired dialog.

To completely disable the welcome dialog, you can set the `disable-welcome-dialog` to true in the `plugins/FancyDialogs/featureFlags.yml` file.