---
icon: dot
order: 10
---

# JSON Schema

!!!danger
This is the old documentation site for FancyInnovations, which is no longer maintained.
The new documentation site can be found at [fancyinnovations.com/docs/minecraft-plugins/fancydialogs](https://fancyinnovations.com/docs/minecraft-plugins/fancydialogs).
!!!

In this tutorial, we will explore the JSON schema used by FancyDialogs to define dialogs. 
This schema provides a structured way to create fancy dialogs.

## Example Dialog

The easiest way to create a new dialog is to copy an existing dialog and modify it to suit your needs.
You can find some example dialogs in the `plugins/FancyDialogs/data/dialogs` folder on your server.

Below is an example of a simple dialog defined using the FancyDialogs JSON schema:

```json
{
  "id": "my_dialog",
  "title": "My Fancy Dialog",
  "canCloseWithEscape": true,
  "body": [
    { "text": "This is my first dialog created with FancyDialogs!" }
  ],
  "inputs": {
    "textFields": [
      {
        "key": "fav_color",
        "order": 1,
        "label": "<color:#ff7300>What is your favorite color?</color>",
        "placeholder": "gold",
        "maxLength": 50,
        "maxLines": 1
      }
    ]
  },
  "buttons": [
    {
      "label": "Show favorite color",
      "tooltip": "Click to show your favorite color",
      "actions": [
        {
          "name": "message",
          "data": "Your favorite color is: <color:{fav_color}>{fav_color}</color>"
        }
      ]
    }
  ]
}
```

## Fields Explained

### Common Fields

`id`: The unique identifier for the dialog, which is used to reference the dialog in commands or other dialogs

`title`: The title of the dialog, which is displayed at the top of the dialog (supports MiniMessage & PlaceholderAPI)

`canCloseWithEscape`: Whether the dialog can be closed with the Escape key (default: true)

`body`: The body of the dialog - see [Body Section](#body-fields) for details

`inputs`: The inputs of the dialog - see [Input Section](#input-fields) for details

`buttons`: The buttons of the dialog - see [Button Section](#button-fields) for details

### Body fields

`text`: The text to display in the body of the dialog (supports MiniMessage & PlaceholderAPI)

!!!info
Items will be supported in the body section in a future release.
!!!

### Input fields

`textFields`: A list of text fields - see [Text Fields](#text-fields) for details

`selects`: A list of select fields - see [Select Fields](#select-fields) for details

!!!info
More input types will be added in future releases, such as checkboxes and number sliders.
!!!

#### Text Fields

`key`: The key to use to store the input value (can be used as a placeholder in actions)

`order`: The order of the text field in the dialog

`label`: The label to display above the text field (supports MiniMessage & PlaceholderAPI)

`placeholder`: The initial text to display in the text field

`maxLength`: The maximum length of the input

`maxLines`: The maximum number of lines for the input (greater than 1 will create a multiline text field)

#### Select Fields

`key`: The key to use to store the input value (can be used as a placeholder in actions)

`order`: The order of the select field in the dialog

`label`: The label to display above the select field (supports MiniMessage & PlaceholderAPI)

`options`: A list of options that can be selected by the player
- `value`: The value that will be returned when the player selects this option
- `display`: The text to display in the select field (supports MiniMessage & PlaceholderAPI)
- `initial`: Whether this option is selected by default (default: false)

### Button fields

- `label`: The text to display on the button (supports MiniMessage & PlaceholderAPI)
- `tooltip`: The tooltip to display when hovering over the button (supports MiniMessage & PlaceholderAPI)
- `actions`: A list of actions that will be executed when the button is clicked - see [Actions](#actions) for details

#### Actions

- `name`: The name of the action (see below for a list of available actions)
- `data`: The data for the action, which depends on the action type

Available actions include:
- `message`: Sends a message to the player (set `data` to the message)
- `console_command`: Executes a command as the console (set `data` to the command)
- `player_command`: Executes a command as the player (set `data` to the command)
- `open_dialog`: Opens another dialog (set `data` to the ID of the dialog to open)
- `open_random_dialog`: Opens another dialog (set `data` to a list of dialog IDs separated by commas: 'dialog1,dialog2,dialog3')
- `send_to_server`: Sends the player to another server (requires BungeeCord or Velocity) (set `data` to the server name)