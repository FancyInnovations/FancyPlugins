package com.fancyinnovations.fancydialogs.storage;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import com.fancyinnovations.fancydialogs.api.Dialog;
import de.oliver.jdb.JDB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JsonDialogStorage implements DialogStorage{

    private final JDB jdb;

    public JsonDialogStorage() {
        this.jdb = new JDB("plugins/FancyDialogs/data/dialogs");
    }

    @Override
    public void save(Dialog dialog) {
        try {
            jdb.set(dialog.getType().name().toLowerCase() + "/" + dialog.getId(), dialog);
        } catch (IOException e) {
            FancyDialogsPlugin.get().getFancyLogger().error("Failed to save dialog " + dialog.getId());
            FancyDialogsPlugin.get().getFancyLogger().error(e);
        }
    }

    @Override
    public void saveBatch(Collection<Dialog> dialogs) {
        for (Dialog dialog : dialogs) {
            save(dialog);
        }
    }

    @Override
    public void delete(Dialog dialog) {
        jdb.delete(dialog.getType().name().toLowerCase() + "/" + dialog.getId());
    }

    @Override
    public Collection<Dialog> loadAll(Dialog.Type type) {
        List<Dialog> dialogs = new ArrayList<>();

        try {
            dialogs = jdb.getAll(type.name().toLowerCase(), Dialog.class);
        } catch (IOException e) {
            FancyDialogsPlugin.get().getFancyLogger().error("Failed to load all dialogs for type: " + type);
            FancyDialogsPlugin.get().getFancyLogger().error(e);
        }

        return dialogs;
    }

    @Override
    public Collection<Dialog> loadAll() {
        List<Dialog> dialogs = new ArrayList<>();

        for (Dialog.Type t : Dialog.Type.values()) {
            dialogs.addAll(loadAll(t));
        }

        return dialogs;
    }
}
