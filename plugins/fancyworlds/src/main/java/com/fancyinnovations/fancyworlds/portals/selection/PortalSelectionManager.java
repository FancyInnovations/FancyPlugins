package com.fancyinnovations.fancyworlds.portals.selection;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PortalSelectionManager {

    private final Map<UUID, PortalSelection> selections = new ConcurrentHashMap<>();

    public PortalSelection setFirst(UUID playerID, PortalSelectionPoint point) {
        return selections.compute(playerID, (ignored, current) ->
                new PortalSelection(point, current == null ? null : current.second()));
    }

    public PortalSelection setSecond(UUID playerID, PortalSelectionPoint point) {
        return selections.compute(playerID, (ignored, current) ->
                new PortalSelection(current == null ? null : current.first(), point));
    }

    public PortalSelection getSelection(UUID playerID) {
        return selections.get(playerID);
    }

    public void clearSelection(UUID playerID) {
        selections.remove(playerID);
    }
}
