package com.fancyinnovations.fancyholograms.util;

import com.google.common.primitives.Doubles;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class CoordinateUtils {

    private CoordinateUtils() {
    }

    public static @Nullable Double calculateCoordinate(
            @NotNull final String text,
            @Nullable final Location originLocation,
            @NotNull final Location callerLocation,
            @NotNull final Function<Location, Number> extractor
    ) {
        String raw = text.trim().replace(',', '.');
        if (raw.isEmpty()) {
            return null;
        }

        Location target;
        String offsetStr;

        if (raw.startsWith("~~")) {
            target = callerLocation;
            offsetStr = raw.substring(2);
        } else if (raw.startsWith("~")) {
            target = originLocation;
            offsetStr = raw.substring(1);
        } else {
            target = null;
            offsetStr = raw;
        }

        if (target != null) {
            if (offsetStr.isEmpty()) {
                return extractor.apply(target).doubleValue();
            }

            if (offsetStr.startsWith("+")) {
                offsetStr = offsetStr.substring(1);
            }

            Double number = Doubles.tryParse(offsetStr);
            if (number == null || Double.isNaN(number) || Double.isInfinite(number)) {
                return null;
            }

            return extractor.apply(target).doubleValue() + number;
        } else {
            if (offsetStr.startsWith("+")) {
                offsetStr = offsetStr.substring(1);
            }

            Double number = Doubles.tryParse(offsetStr);
            if (number == null || Double.isNaN(number) || Double.isInfinite(number)) {
                return null;
            }

            return number;
        }
    }
}
