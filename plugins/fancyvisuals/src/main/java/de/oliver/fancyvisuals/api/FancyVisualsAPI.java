package de.oliver.fancyvisuals.api;

import de.oliver.fancyvisuals.api.nametags.NametagRepository;
import org.bukkit.plugin.java.JavaPlugin;

public interface FancyVisualsAPI {

    JavaPlugin getPlugin();

    NametagRepository getNametagRepository();
}
