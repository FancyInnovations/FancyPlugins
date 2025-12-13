package de.oliver.fancysitula.api.utils;

import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.List;

public enum ServerVersion {

    v1_21_11("1.21.11", 774),
    v1_21_10("1.21.10", 773),
    v1_21_9("1.21.9", 773),
    v1_21_8("1.21.8", 772),
    v1_21_7("1.21.7", 772),
    v1_21_6("1.21.6", 771),
    v1_21_5("1.21.5", 770),
    v1_21_4("1.21.4", 769),
    v1_21_3("1.21.3", 768),
    v1_21_1("1.21.1", 767),
    v1_21("1.21", 767),
    ;

    private final String version;
    private final int protocolVersion;

    ServerVersion(String version, int protocolVersion) {
        this.version = version;
        this.protocolVersion = protocolVersion;
    }

    public static ServerVersion getByProtocolVersion(int protocolVersion) {
        for (ServerVersion version : values()) {
            if (version.getProtocolVersion() == protocolVersion) {
                return version;
            }
        }

        return null;
    }

    public static ServerVersion getByVersion(String version) {
        for (ServerVersion serverVersion : values()) {
            if (serverVersion.getVersion().equals(version)) {
                return serverVersion;
            }
        }

        return null;
    }

    public static List<String> getSupportedVersions() {
        return Arrays.stream(values())
                .map(ServerVersion::getVersion)
                .toList();
    }

    public static boolean isVersionSupported(String version) {
        return getByVersion(version) != null;
    }

    /**
     * @return the current server version of the server the plugin is running on
     */
    public static ServerVersion getCurrentVersion() {
        return getByVersion(Bukkit.getMinecraftVersion());
    }

    public String getVersion() {
        return version;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }
}
