package de.oliver.fancysitula.versions.v1_21_11.packets;

import de.oliver.fancysitula.api.dialogs.FS_CommonDialogData;
import de.oliver.fancysitula.api.dialogs.FS_Dialog;
import de.oliver.fancysitula.api.dialogs.FS_DialogAction;
import de.oliver.fancysitula.api.dialogs.actions.FS_CommonButtonData;
import de.oliver.fancysitula.api.dialogs.actions.FS_DialogActionButton;
import de.oliver.fancysitula.api.dialogs.actions.FS_DialogCustomAction;
import de.oliver.fancysitula.api.dialogs.body.FS_DialogBody;
import de.oliver.fancysitula.api.dialogs.body.FS_DialogItemBody;
import de.oliver.fancysitula.api.dialogs.body.FS_DialogTextBody;
import de.oliver.fancysitula.api.dialogs.inputs.*;
import de.oliver.fancysitula.api.dialogs.types.FS_ConfirmationDialog;
import de.oliver.fancysitula.api.dialogs.types.FS_DialogListDialog;
import de.oliver.fancysitula.api.dialogs.types.FS_MultiActionDialog;
import de.oliver.fancysitula.api.dialogs.types.FS_NoticeDialog;
import de.oliver.fancysitula.api.entities.FS_RealPlayer;
import de.oliver.fancysitula.api.packets.FS_ClientboundShowDialogPacket;
import de.oliver.fancysitula.versions.v1_21_11.utils.VanillaPlayerAdapter;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundShowDialogPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.dialog.*;
import net.minecraft.server.dialog.action.Action;
import net.minecraft.server.dialog.action.CustomAll;
import net.minecraft.server.dialog.body.DialogBody;
import net.minecraft.server.dialog.body.ItemBody;
import net.minecraft.server.dialog.body.PlainMessage;
import net.minecraft.server.dialog.input.*;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientboundShowDialogPacketImpl extends FS_ClientboundShowDialogPacket {

    public ClientboundShowDialogPacketImpl(FS_Dialog dialog) {
        super(dialog);
    }

    @Override
    public Object createPacket() {
        Holder<Dialog> holder = Holder.direct(toNms(dialog));
        return new ClientboundShowDialogPacket(holder);
    }

    @Override
    protected void sendPacketTo(FS_RealPlayer player) {
        ClientboundShowDialogPacket packet = (ClientboundShowDialogPacket) createPacket();

        ServerPlayer vanillaPlayer = VanillaPlayerAdapter.asVanilla(player.getBukkitPlayer());
        vanillaPlayer.connection.send(packet);
    }

    private Dialog toNms(FS_Dialog dialog) {
        if (dialog instanceof FS_NoticeDialog notice) {
            return noticeToNms(notice);
        } else if (dialog instanceof FS_ConfirmationDialog confirmation) {
            return confirmationToNms(confirmation);
        } else if (dialog instanceof FS_DialogListDialog dialogList) {
            return dialogListToNms(dialogList);
        } else if (dialog instanceof FS_MultiActionDialog multiActionDialog) {
            return multiActionDialogToNms(multiActionDialog);
        }

        return null;
    }

    private Dialog noticeToNms(FS_NoticeDialog notice) {
        CommonDialogData common = commonToNms(notice.getDialogData());
        ActionButton actionButton = actionButtonToNms(notice.getActionButton());

        return new NoticeDialog(common, actionButton);
    }

    private Dialog confirmationToNms(FS_ConfirmationDialog notice) {
        CommonDialogData common = commonToNms(notice.getDialogData());
        ActionButton yes = actionButtonToNms(notice.getYesButton());
        ActionButton no = actionButtonToNms(notice.getNoButton());

        return new ConfirmationDialog(common, yes, no);
    }

    private Dialog dialogListToNms(FS_DialogListDialog dialogList) {
        CommonDialogData common = commonToNms(dialogList.getDialogData());
        List<Holder<Dialog>> dialogs = new ArrayList<>();

        for (FS_Dialog dialog : dialogList.getDialogs()) {
            dialogs.add(Holder.direct(toNms(dialog)));
        }

        HolderSet<Dialog> dialogSet = HolderSet.direct(dialogs);

        Optional<ActionButton> exitButton = dialogList.getExitButton() != null ?
                Optional.of(actionButtonToNms(dialogList.getExitButton())) :
                Optional.empty();

        return new DialogListDialog(common, dialogSet, exitButton, dialogList.getColumns(), dialogList.getButtonWidth());
    }

    private Dialog multiActionDialogToNms(FS_MultiActionDialog multiActionDialog) {
        CommonDialogData common = commonToNms(multiActionDialog.getDialogData());
        List<ActionButton> actionButtons = new ArrayList<>();

        for (FS_DialogActionButton actionButton : multiActionDialog.getActions()) {
            actionButtons.add(actionButtonToNms(actionButton));
        }

        Optional<ActionButton> exitAction = multiActionDialog.getExitAction() != null ?
                Optional.of(actionButtonToNms(multiActionDialog.getExitAction())) :
                Optional.empty();

        return new MultiActionDialog(common, actionButtons, exitAction, multiActionDialog.getColumns());
    }

    private CommonDialogData commonToNms(FS_CommonDialogData dialogData) {
        Component title = PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize(dialogData.getTitle()));

        Optional<Component> externalTitle = dialogData.getExternalTitle() != null ?
                Optional.of(PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize(dialogData.getExternalTitle()))) :
                Optional.empty();

        return new CommonDialogData(
                title,
                externalTitle,
                dialogData.isCanCloseWithEscape(),
                dialogData.isPause(),
                actionToNms(dialogData.getAfterAction()),
                bodyToNms(dialogData.getBody()),
                inputsToNms(dialogData.getInputs())
        );
    }

    private DialogAction actionToNms(FS_DialogAction dialogAction) {
        return switch (dialogAction) {
            case CLOSE -> DialogAction.CLOSE;
            case NONE -> DialogAction.NONE;
            case WAIT_FOR_RESPONSE -> DialogAction.WAIT_FOR_RESPONSE;
        };
    }

    private List<DialogBody> bodyToNms(List<FS_DialogBody> bodies) {
        List<DialogBody> nmsBodies = new ArrayList<>();

        for (FS_DialogBody body : bodies) {
            if (body instanceof FS_DialogTextBody textBody) {
                nmsBodies.add(new PlainMessage(
                        PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize(textBody.getText())),
                        textBody.getWidth()
                ));
            } else if (body instanceof FS_DialogItemBody itemBody) {
                Optional<PlainMessage> description = itemBody.getDescription() != null ?
                        Optional.of(new PlainMessage(
                                PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize(itemBody.getDescription().getText())),
                                itemBody.getDescription().getWidth()
                        )) :
                        Optional.empty();

                nmsBodies.add(new ItemBody(
                        CraftItemStack.asNMSCopy(itemBody.getItem()),
                        description,
                        itemBody.isShowDecorations(),
                        itemBody.isShowTooltip(),
                        itemBody.getWidth(),
                        itemBody.getHeight()
                ));
            }
        }

        return nmsBodies;
    }

    private List<Input> inputsToNms(List<FS_DialogInput> inputs) {
        List<Input> nmsInputs = new ArrayList<>();

        for (FS_DialogInput input : inputs) {
            String key = input.getKey();

            InputControl control = null;
            if (input.getControl() instanceof FS_DialogBooleanInput booleanInput) {
                control = new BooleanInput(
                        PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize(booleanInput.getLabel())),
                        booleanInput.isInitial(),
                        booleanInput.getOnTrue(),
                        booleanInput.getOnFalse()
                );
            } else if (input.getControl() instanceof FS_DialogNumberRangeInput numberRangeInput) {
                control = new NumberRangeInput(
                        numberRangeInput.getWidth(),
                        PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize(numberRangeInput.getLabel())),
                        numberRangeInput.getLabelFormat(),
                        new NumberRangeInput.RangeInfo(
                                numberRangeInput.getStart(),
                                numberRangeInput.getEnd(),
                                numberRangeInput.getInitial() != null ? Optional.of(numberRangeInput.getInitial()) : Optional.empty(),
                                numberRangeInput.getStep() != null ? Optional.of(numberRangeInput.getStep()) : Optional.empty()
                        )
                );
            } else if (input.getControl() instanceof FS_DialogSingleOptionInput singleOptionInput) {
                List<SingleOptionInput.Entry> nmsEntries = new ArrayList<>();
                for (FS_DialogSingleOptionInput.Entry entry : singleOptionInput.getEntries()) {
                    nmsEntries.add(new SingleOptionInput.Entry(
                            entry.getId(),
                            entry.getDisplay() != null ? Optional.of(PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize(entry.getDisplay()))) : Optional.empty(),
                            entry.isInitial()
                    ));
                }

                control = new SingleOptionInput(
                        singleOptionInput.getWidth(),
                        nmsEntries,
                        PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize(singleOptionInput.getLabel())),
                        singleOptionInput.isLabelVisible()
                );
            } else if (input.getControl() instanceof FS_DialogTextInput textInput) {
                control = new TextInput(
                        textInput.getWidth(),
                        PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize(textInput.getLabel())),
                        textInput.isLabelVisible(),
                        textInput.getInitial(),
                        textInput.getMaxLength(),
                        Optional.empty()
                );
            }

            nmsInputs.add(new Input(key, control));
        }

        return nmsInputs;
    }

    private ActionButton actionButtonToNms(FS_DialogActionButton actionButton) {
        CommonButtonData buttonData = commonButtonDataToNms(actionButton.getButtonData());

        Action action = null;
        if (actionButton.getAction() instanceof FS_DialogCustomAction customAction) {
            Key idKey = Key.key("fancysitula", customAction.getId());
            Identifier idLocation = PaperAdventure.asVanilla(idKey);

            Optional<CompoundTag> additions;
            if (customAction.getAdditions() != null) {
                CompoundTag tag = new CompoundTag();
                customAction.getAdditions().forEach(tag::putString);
                additions = Optional.of(tag);
            } else {
                additions = Optional.empty();
            }

            action = new CustomAll(idLocation, additions);
        }

        Optional<Action> optionalAction = action != null ?
                Optional.of(action) :
                Optional.empty();

        return new ActionButton(buttonData, optionalAction);
    }

    private CommonButtonData commonButtonDataToNms(FS_CommonButtonData commonButtonData) {
        Component label = PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize(commonButtonData.getLabel()));
        Optional<Component> tooltip = commonButtonData.getTooltip() != null ?
                Optional.of(PaperAdventure.asVanilla(MiniMessage.miniMessage().deserialize(commonButtonData.getTooltip()))) :
                Optional.empty();
        int width = commonButtonData.getWidth();

        return new CommonButtonData(label, tooltip, width);
    }
}
