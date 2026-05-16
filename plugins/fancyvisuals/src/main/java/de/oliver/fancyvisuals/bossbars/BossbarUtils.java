package de.oliver.fancyvisuals.bossbars;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class BossbarUtils {

    private BossbarUtils() {
    }

    public static BarColor parseColor(String value) {
        if (value == null) {
            return BarColor.WHITE;
        }

        try {
            return BarColor.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return BarColor.WHITE;
        }
    }

    public static BarStyle parseStyle(String value) {
        if (value == null) {
            return BarStyle.SOLID;
        }

        try {
            return BarStyle.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return BarStyle.SOLID;
        }
    }

    public static Set<BarFlag> parseFlags(List<String> values) {
        Set<BarFlag> flags = new HashSet<>();
        if (values == null) {
            return flags;
        }

        for (String value : values) {
            if (value == null) {
                continue;
            }
            try {
                flags.add(BarFlag.valueOf(value.toUpperCase()));
            } catch (IllegalArgumentException ignored) {
                // ignore invalid flag
            }
        }

        return flags;
    }
}
