package com.fancyinnovations.fancynpcs.api.skins;

import org.jetbrains.annotations.NotNull;

/**
 * Exception that is thrown when a skin cannot be fetched or loaded.
 */
public final class SkinLoadException extends RuntimeException {

    private final @NotNull SkinLoadException.Reason reason;

    public SkinLoadException(final @NotNull SkinLoadException.Reason reason, final @NotNull String message) {
        super(message);
        this.reason = reason;
    }

    /**
     * Returns the reason why the skin could not be loaded.
     *
     * @return the reason
     */
    public @NotNull SkinLoadException.Reason getReason() {
        return reason;
    }

    public enum Reason {
        INVALID_URL, INVALID_FILE, INVALID_PLACEHOLDER, INVALID_USERNAME
    }

}
