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
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DialogImpl extends Dialog {

    public DialogImpl(String id, DialogData data) {
        super(id, data);
    }

    private FS_Dialog buildForPlayer(Player player) {
        List<FS_DialogBody> body = new ArrayList<>();
        for (DialogBodyData bodyData : data.body()) {
            FS_DialogTextBody fsDialogTextBody = new FS_DialogTextBody(
                    ChatColorHandler.translate(bodyData.text(), player, ParserTypes.placeholder()),
                    200 // default text width
            );
            body.add(fsDialogTextBody);
        }

        List<FS_DialogInput> inputs = new ArrayList<>();
        if (data.inputs() != null) {
            for (DialogInput input : data.inputs().all()) {
                FS_DialogInputControl control = null;
                if (input instanceof DialogTextField textField) {
                    control = new FS_DialogTextInput(
                            200, // default width
                            ChatColorHandler.translate(textField.getLabel(), player, ParserTypes.placeholder()),
                            !textField.getLabel().isEmpty(),
                            ChatColorHandler.translate(textField.getPlaceholder(), player, ParserTypes.placeholder()),
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
                                        ChatColorHandler.translate(entry.value(), player, ParserTypes.placeholder()),
                                        ChatColorHandler.translate(entry.display(), player, ParserTypes.placeholder()),
                                        entry.initial()
                                )
                        );
                    }
                    control = new FS_DialogSingleOptionInput(
                            200, // default width
                            entries,
                            ChatColorHandler.translate(select.getLabel(), player, ParserTypes.placeholder()),
                            !select.getLabel().isEmpty()
                    );
                } else if (input instanceof DialogCheckbox checkbox) {
                    control = new FS_DialogBooleanInput(input.getLabel(), checkbox.isInitial(), "true", "false");
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
            FS_DialogActionButton fsDialogActionButton = new FS_DialogActionButton(
                    new FS_CommonButtonData(
                            ChatColorHandler.translate(button.label(), player, ParserTypes.placeholder()),
                            ChatColorHandler.translate(button.tooltip(), player, ParserTypes.placeholder()),
                            150 // default button width
                    ),
                    new FS_DialogCustomAction(
                            "fancydialogs_dialog_action",
                            Map.of(
                                    "dialog_id", id,
                                    "button_id", button.id()
                            )
                    )
            );
            actions.add(fsDialogActionButton);
        }

        if (actions.isEmpty()) {
            return new FS_NoticeDialog(
                    new FS_CommonDialogData(
                            ChatColorHandler.translate(data.title(), player, ParserTypes.placeholder()),
                            ChatColorHandler.translate(data.title(), player, ParserTypes.placeholder()),
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
                        ChatColorHandler.translate(data.title(), player, ParserTypes.placeholder()),
                        ChatColorHandler.translate(data.title(), player, ParserTypes.placeholder()),
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
        FancySitula.PACKET_FACTORY
                .createShowDialogPacket(buildForPlayer(player))
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
