package com.fancyinnovations.fancyholograms.api;

public interface HologramConfiguration {

    /**
     * Returns whether version notifications are muted.
     *
     * @return {@code true} if version notifications are muted, {@code false} otherwise.
     */
    boolean areVersionNotificationsMuted();

    /**
     * Returns whether autosave is enabled.
     *
     * @return {@code true} if autosave is enabled, {@code false} otherwise.
     */
    boolean isAutosaveEnabled();

    /**
     * Returns the interval at which autosave is performed.
     *
     * @return The autosave interval in minutes.
     */
    int getAutosaveInterval();

    /**
     * Returns whether the plugin should save holograms when they are changed.
     *
     * @return {@code true} if the plugin should save holograms when they are changed, {@code false} otherwise.
     */
    boolean isSaveOnChangedEnabled();

    /**
     * Returns the default visibility distance for holograms.
     *
     * @return The default hologram visibility distance.
     */
    int getDefaultVisibilityDistance();

    /**
     * Returns whether the plugin should register its commands.
     *
     * @return {@code true} if the plugin should register its commands, {@code false} otherwise.
     */
    boolean isRegisterCommands();

    /**
     * Returns the log level for the plugin.
     *
     * @return The log level for the plugin.
     */
    String getLogLevel();

    /**
     * Returns the language used by the plugin.
     *
     * @return The language used by the plugin.
     */
    String getLanguage();

    /**
     * Returns whether holograms should be shown to bedrock players.
     *
     * @return {@code true} if holograms should be shown to bedrock players, {@code false} otherwise.
     */
    boolean isHologramsForBedrockPlayersEnabled();

    /**
     * Returns whether holograms should be shown to players with an old client version.
     *
     * @return {@code true} if holograms should be shown to players with an old client version, {@code false} otherwise.
     */
    boolean isHologramsForOldClientsEnabled();

    /**
     * Returns whether the plugin uses lamp commands.
     *
     * @return {@code true} if the plugin uses lamp commands, {@code false} otherwise.
     */
    boolean useLampCommands();

    /**
     * Returns whether the Folia visibility fix is enabled.
     *
     * @return {@code true} if the Folia visibility fix is enabled, {@code false} otherwise.
     */
    boolean isFoliaVisibilityFixEnabled();
}
