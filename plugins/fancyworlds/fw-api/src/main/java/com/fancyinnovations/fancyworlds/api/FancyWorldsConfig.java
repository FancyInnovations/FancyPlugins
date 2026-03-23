package com.fancyinnovations.fancyworlds.api;

public interface FancyWorldsConfig {

    static FancyWorldsConfig get() {
        return FancyWorlds.get().getFancyWorldsConfig();
    }

    String getLogLevel();

    boolean areVersionNotificationsMuted();

    String getLanguage();

    boolean automaticallyLinkWorlds();

}
