package de.oliver.fancysitula.api.packets;

import de.oliver.fancysitula.api.dialogs.FS_Dialog;

public abstract class FS_ClientboundShowDialogPacket extends FS_ClientboundPacket {

    protected FS_Dialog dialog;

    public FS_ClientboundShowDialogPacket(FS_Dialog dialog) {
        this.dialog = dialog;
    }

    public FS_Dialog getDialog() {
        return dialog;
    }

    public void setDialog(FS_Dialog dialog) {
        this.dialog = dialog;
    }
}
