package com.fancyinnovations.fancydialogs.api.body;

import org.jetbrains.annotations.NotNull;

public record TextBody(
        @NotNull String content,
        int width
) implements DialogBody {

}
