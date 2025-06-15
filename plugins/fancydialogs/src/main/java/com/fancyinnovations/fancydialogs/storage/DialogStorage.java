package com.fancyinnovations.fancydialogs.storage;

import com.fancyinnovations.fancydialogs.api.data.DialogData;

import java.util.Collection;

public interface DialogStorage {

    void save(DialogData dialog);

    void saveBatch(Collection<DialogData> dialogs);

    void delete(DialogData dialog);

    Collection<DialogData> loadAll();

}
