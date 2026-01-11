package com.fancyinnovations.fancydialogs.dialog;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import com.fancyinnovations.fancydialogs.api.Dialog;
import com.fancyinnovations.fancydialogs.api.data.DialogBodyData;
import com.fancyinnovations.fancydialogs.api.data.DialogButton;
import com.fancyinnovations.fancydialogs.api.data.DialogData;
import com.fancyinnovations.fancydialogs.api.data.inputs.DialogCheckbox;
import com.fancyinnovations.fancydialogs.api.data.inputs.DialogInput;
import com.fancyinnovations.fancydialogs.api.data.inputs.DialogSelect;
import com.fancyinnovations.fancydialogs.api.data.inputs.DialogTextField;
import de.oliver.fancysitula.api.dialogs.FS_CommonDialogData;
import de.oliver.fancysitula.api.dialogs.FS_Dialog;
import de.oliver.fancysitula.api.dialogs.FS_DialogAction;
import de.oliver.fancysitula.api.dialogs.actions.FS_CommonButtonData;
import de.oliver.fancysitula.api.dialogs.actions.FS_DialogActionButton;
import de.oliver.fancysitula.api.dialogs.actions.FS_DialogActionButtonAction;
import de.oliver.fancysitula.api.dialogs.actions.FS_DialogCopyToClipboardAction;
import de.oliver.fancysitula.api.dialogs.actions.FS_DialogCustomAction;
import de.oliver.fancysitula.api.dialogs.body.FS_DialogBody;
import de.oliver.fancysitula.api.dialogs.body.FS_DialogTextBody;
import de.oliver.fancysitula.api.dialogs.inputs.*;
import de.oliver.fancysitula.api.dialogs.types.FS_MultiActionDialog;
import de.oliver.fancysitula.api.dialogs.types.FS_NoticeDialog;
import de.oliver.fancysitula.api.entities.FS_RealPlayer;
import de.oliver.fancysitula.factories.FancySitula;
import org.bukkit.entity.Player;
import org.lushplugins.chatcolorhandler.ChatColorHandler;
import org.lushplugins.chatcolorhandler.parsers.ParserTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DialogImpl extends Dialog {

    public DialogImpl(String id, DialogData data) {
        super(id, data);
    }

    /**
     * Replaces argument placeholders ({arg:0}, {arg:1}, etc.) in the given text.
     */
    private String replaceArgs(String text, String[] args) {
        if (args == null || args.length == 0 || text == null) {
            return text;
        }
        String result = text;
        for (int i = 0; i < args.length; i++) {
            result = result.replace("{arg:" + i + "}", args[i] != null ? args[i] : "");
        }
        return result;
    }

    private FS_Dialog buildForPlayer(Player player, String[] args) {
        List<FS_DialogBody> body = new ArrayList<>();
        for (DialogBodyData bodyData : data.body()) {

            int textWidth = (bodyData.width() != null && bodyData.width() > 0)
                    ? bodyData.width()
                    : 200;

            String bodyText = replaceArgs(bodyData.text(), args);
            FS_DialogTextBody fsDialogTextBody = new FS_DialogTextBody(
                    ChatColorHandler.translate(bodyText, player, ParserTypes.placeholder()),
                    textWidth
            );
            body.add(fsDialogTextBody);
        }

        List<FS_DialogInput> inputs = new ArrayList<>();
        if (data.inputs() != null) {
            for (DialogInput input : data.inputs().all()) {
                FS_DialogInputControl control = null;
                if (input instanceof DialogTextField textField) {
                    String label = replaceArgs(textField.getLabel(), args);
                    String placeholder = replaceArgs(textField.getPlaceholder(), args);
                    control = new FS_DialogTextInput(
                            200, // default width
                            ChatColorHandler.translate(label, player, ParserTypes.placeholder()),
                            !label.isEmpty(),
                            ChatColorHandler.translate(placeholder, player, ParserTypes.placeholder()),
                            textField.getMaxLength(),
                            textField.getMaxLines() > 0 ?
                                    new FS_DialogTextInput.MultilineOptions(textField.getMaxLines(), null) :
                                    null
                    );
                } else if (input instanceof DialogSelect select) {
                    List<FS_DialogSingleOptionInput.Entry> entries = new ArrayList<>();
                    for (DialogSelect.Entry entry : select.getOptions()) {
                        entries.add(
                                new FS_DialogSingleOptionInput.Entry(
                                        ChatColorHandler.translate(replaceArgs(entry.value(), args), player, ParserTypes.placeholder()),
                                        ChatColorHandler.translate(replaceArgs(entry.display(), args), player, ParserTypes.placeholder()),
                                        entry.initial()
                                )
                        );
                    }
                    String selectLabel = replaceArgs(select.getLabel(), args);
                    control = new FS_DialogSingleOptionInput(
                            200, // default width
                            entries,
                            ChatColorHandler.translate(selectLabel, player, ParserTypes.placeholder()),
                            !selectLabel.isEmpty()
                    );
                } else if (input instanceof DialogCheckbox checkbox) {
                    String checkboxLabel = replaceArgs(input.getLabel(), args);
                    control = new FS_DialogBooleanInput(checkboxLabel, checkbox.isInitial(), "true", "false");
                }

                if (control == null) {
                    throw new IllegalArgumentException("Unsupported input type: " + input.getClass().getSimpleName());
                }

                FS_DialogInput fsDialogInput = new FS_DialogInput(input.getKey(), control);
                inputs.add(fsDialogInput);
            }
        }

        List<FS_DialogActionButton> actions = new ArrayList<>();
        for (DialogButton button : data.buttons()) {
            FS_DialogActionButtonAction buttonAction;

            if (button.actions().size() == 1 &&
                button.actions().get(0).name().equals("copy_to_clipboard")) {
                String clipboardData = replaceArgs(button.actions().get(0).data(), args);
                String text = ChatColorHandler.translate(
                        clipboardData,
                        player,
                        ParserTypes.placeholder()
                );
                buttonAction = new FS_DialogCopyToClipboardAction(text);
            } else {
                // Build payload with dialog_id, button_id, and all args
                Map<String, String> payload = new HashMap<>();
                payload.put("dialog_id", id);
                payload.put("button_id", button.id());
                if (args != null) {
                    for (int i = 0; i < args.length; i++) {
                        payload.put("arg:" + i, args[i] != null ? args[i] : "");
                    }
                }
                buttonAction = new FS_DialogCustomAction(
                        "fancydialogs_dialog_action",
                        payload
                );
            }

            String buttonLabel = replaceArgs(button.label(), args);
            String buttonTooltip = replaceArgs(button.tooltip(), args);
            FS_DialogActionButton fsDialogActionButton = new FS_DialogActionButton(
                    new FS_CommonButtonData(
                            ChatColorHandler.translate(buttonLabel, player, ParserTypes.placeholder()),
                            ChatColorHandler.translate(buttonTooltip, player, ParserTypes.placeholder()),
                            150 // default button width
                    ),
                    buttonAction
            );
            actions.add(fsDialogActionButton);
        }

        String title = replaceArgs(data.title(), args);
        String translatedTitle = ChatColorHandler.translate(title, player, ParserTypes.placeholder());

        if (actions.isEmpty()) {
            return new FS_NoticeDialog(
                    new FS_CommonDialogData(
                            translatedTitle,
                            translatedTitle,
                            data.canCloseWithEscape(),
                            false,
                            FS_DialogAction.CLOSE,
                            body,
                            inputs
                    ),
                    new FS_DialogActionButton(
                            new FS_CommonButtonData(
                                    "Close",
                                    null,
                                    150 // default button width
                            ),
                            new FS_DialogCustomAction(
                                    "fancydialogs_dialog_action--none",
                                    Map.of())
                    )
            );
        }

        return new FS_MultiActionDialog(
                new FS_CommonDialogData(
                        translatedTitle,
                        translatedTitle,
                        data.canCloseWithEscape(),
                        false,
                        FS_DialogAction.CLOSE,
                        body,
                        inputs
                ),
                actions, // actions
                null,
                2
        );
    }

    @Override
    public void open(Player player) {
        open(player, new String[0]);
    }

    @Override
    public void open(Player player, String... args) {
        FancySitula.PACKET_FACTORY
                .createShowDialogPacket(buildForPlayer(player, args))
                .send(new FS_RealPlayer(player));

        addViewer(player);

        FancyDialogsPlugin.get().getFancyLogger().debug("Opened dialog " + id + " for player " + player.getName());
    }

    @Override
    public void close(Player player) {
        FancySitula.PACKET_FACTORY
                .createClearDialogPacket()
                .send(new FS_RealPlayer(player));

        removeViewer(player);

        FancyDialogsPlugin.get().getFancyLogger().debug("Closed dialog " + id + " for player " + player.getName());
    }

    @Override
    public boolean isOpenedFor(UUID uuid) {
        if (uuid == null) {
            return false;
        }

        if (!viewers.containsKey(uuid)) {
            return false;
        }

        long openedAt = viewers.get(uuid);
        long now = System.currentTimeMillis();
        if (now - openedAt > FancyDialogsPlugin.get().getFancyDialogsConfig().getCloseTimeout()) {
            viewers.remove(uuid);
            return false;
        }

        return true;
    }

}
