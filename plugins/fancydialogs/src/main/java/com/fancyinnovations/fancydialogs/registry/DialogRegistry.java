package com.fancyinnovations.fancydialogs.registry;

import com.fancyinnovations.fancydialogs.api.Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DialogRegistry {

    private final Map<String, Dialog> dialogs;

    public DialogRegistry() {
        this.dialogs = new ConcurrentHashMap<>();
    }

    public void register(@NotNull Dialog dialog) {
        dialogs.put(dialog.getId(), dialog);
    }

    public void unregister(@NotNull String id) {
        dialogs.remove(id);
    }

    public Dialog get(@NotNull String id) {
        return dialogs.get(id);
    }

    public Collection<Dialog> getAll() {
        return dialogs.values();
    }

    public void clear() {
        dialogs.clear();
    }
}
