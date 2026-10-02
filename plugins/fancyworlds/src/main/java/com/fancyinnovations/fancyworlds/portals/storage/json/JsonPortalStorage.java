package com.fancyinnovations.fancyworlds.portals.storage.json;

import com.fancyinnovations.fancyworlds.api.portals.FPortal;
import com.fancyinnovations.fancyworlds.api.portals.PortalStorage;
import com.fancyinnovations.fancyworlds.main.FancyWorldsPlugin;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import de.oliver.jdb.JDB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JsonPortalStorage implements PortalStorage {

    private final JDB jdb;

    public JsonPortalStorage() {
        this.jdb = new JDB("plugins/FancyWorlds/data/portals");
    }

    @Override
    public void storePortal(FPortal portal) {
        try {
            jdb.set(portal.getID().toString(), JsonFPortal.fromFPortal(portal));
        } catch (IOException e) {
            FancyWorldsPlugin.get().getFancyLogger().error("Failed to save portal " + portal.getID(), ThrowableProperty.of(e));
        }
    }

    @Override
    public Collection<FPortal> getAllPortals() {
        List<FPortal> portals = new ArrayList<>();
        try {
            List<JsonFPortal> storedPortals = jdb.getAll("", JsonFPortal.class);
            for (JsonFPortal portal : storedPortals) {
                if (portal != null) {
                    portals.add(portal.toFPortal());
                }
            }
        } catch (IOException e) {
            FancyWorldsPlugin.get().getFancyLogger().error("Failed to load all portals", ThrowableProperty.of(e));
        }
        return portals;
    }

    @Override
    public void deletePortal(String id) {
        jdb.delete(id);
    }
}
