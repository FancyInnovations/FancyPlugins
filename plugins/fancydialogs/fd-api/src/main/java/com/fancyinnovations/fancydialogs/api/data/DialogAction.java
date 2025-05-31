package com.fancyinnovations.fancydialogs.api.data;

public enum DialogAction {

    CLOSE(0, "close"),
    NONE(1, "none"),
    WAIT_FOR_RESPONSE(2, "wait_for_response");

    private final int id;
    private final String name;

    DialogAction(final int id, final String name) {
        this.id = id;
        this.name = name;
    }

}
