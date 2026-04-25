package de.oliver.fancylib.duration;

import org.jetbrains.annotations.NotNull;

public record FancyDuration(
        long millis
) {

    public static FancyDuration parse(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }

        if (input.equalsIgnoreCase("never") || input.equalsIgnoreCase("off") || input.equalsIgnoreCase("none")) {
            return new FancyDuration(-1);
        }

        long totalMillis = 0;

        // Regex: number + unit
        java.util.regex.Matcher matcher = java.util.regex.Pattern
                .compile("(\\d+)(ms|s|m|h|d)")
                .matcher(input);

        int pos = 0;

        while (matcher.find()) {
            if (matcher.start() != pos) {
                throw new IllegalArgumentException("Invalid format: " + input);
            }

            long value = Long.parseLong(matcher.group(1));
            if (value < 0) {
                throw new IllegalArgumentException("Value cannot be negative: " + value);
            }

            String unit = matcher.group(2);

            switch (unit) {
                case "ms":
                    totalMillis += value;
                    break;
                case "s":
                    totalMillis += value * 1_000;
                    break;
                case "m":
                    totalMillis += value * 60_000;
                    break;
                case "h":
                    totalMillis += value * 3_600_000;
                    break;
                case "d":
                    totalMillis += value * 86_400_000;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown unit: " + unit);
            }

            pos = matcher.end();
        }

        if (pos != input.length()) {
            throw new IllegalArgumentException("Invalid format: " + input);
        }

        return new FancyDuration(totalMillis);
    }

    public boolean isNever() {
        return millis < -1;
    }

    public long seconds() {
        return millis / 1_000;
    }

    public long minutes() {
        return millis / 60_000;
    }

    public long hours() {
        return millis / 3_600_000;
    }

    public long days() {
        return millis / 86_400_000;
    }

    @Override
    public @NotNull String toString() {
        long remainingMillis = millis;

        long days = remainingMillis / 86_400_000;
        remainingMillis %= 86_400_000;

        long hours = remainingMillis / 3_600_000;
        remainingMillis %= 3_600_000;

        long minutes = remainingMillis / 60_000;
        remainingMillis %= 60_000;

        long seconds = remainingMillis / 1_000;
        remainingMillis %= 1_000;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d");
        if (hours > 0) sb.append(hours).append("h");
        if (minutes > 0) sb.append(minutes).append("m");
        if (seconds > 0) sb.append(seconds).append("s");
        if (remainingMillis > 0) sb.append(remainingMillis).append("ms");

        return sb.toString();
    }
}
