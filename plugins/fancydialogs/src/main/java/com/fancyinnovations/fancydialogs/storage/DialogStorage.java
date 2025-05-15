package com.fancyinnovations.fancydialogs.storage;

import com.fancyinnovations.fancydialogs.api.Dialog;
import com.fancyinnovations.fancydialogs.api.data.types.DialogType;

import java.util.Collection;

public interface DialogStorage {

    void save(Dialog dialog);

    void saveBatch(Collection<Dialog> dialogs);

    void delete(Dialog dialog);

    Collection<Dialog> loadAll(Dialog.Type type);

    Collection<Dialog> loadAll();

}
