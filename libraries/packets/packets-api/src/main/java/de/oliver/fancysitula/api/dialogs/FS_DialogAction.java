package de.oliver.fancysitula.api.dialogs;

public enum FS_DialogAction {
    CLOSE(0, "close"),
    NONE(1, "none"),
    WAIT_FOR_RESPONSE(2, "wait_for_response");

    private final int id;
    private final String name;

    FS_DialogAction(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
