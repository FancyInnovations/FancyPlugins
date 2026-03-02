## Added arguments ([#182](https://github.com/FancyInnovations/FancyPlugins/pull/182))

- Add support for dynamic arguments in dialogs via `dialog.open(player, args)`
- Arguments can be referenced in dialog content using `{arg:0}`, `{arg:1}`, etc.
- Arguments work in title, body text, input labels/placeholders, button labels/tooltips, and clipboard actions
- Updated `/dialog open` command to accept optional arguments
- Updated `open_dialog` action and FancyNpcs integration to support arguments

## Added requirements ([#201](https://github.com/FancyInnovations/FancyPlugins/pull/201))

Created a new configuration option to Buttons & Inputs (Checkbox, Select, Textfield) to have a requirement for it to appear in a dialog. Currently these requirements are permission or string match - being
```
requirements: {
  "type": "permission",
  "permission": "my.permission"
}
```
or
```
requirements: {
  "type": "stringMatch",
  "input": "%player_name%",
  "output": "mesemi"
}
```
resulting in the item displaying if conditions are met (perm is held, or %player_name% resolves to mesemi)

## Exit actions, widths and columns ([#204](https://github.com/FancyInnovations/FancyPlugins/pull/204))

- Added width for buttons, selects and textfields
- Added columns for dialogs (for dialogs that match multi_action, a "column" parameter can be used to change columns)
- Added exitAction for multi_action dialogs (basically a footer button with custom action, but mainly for close buttons)

Thank you to WiFlow and mesemi for their contributions to these features!