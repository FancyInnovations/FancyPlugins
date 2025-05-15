package com.fancyinnovations.fancydialogs.api.data.body;

import org.jetbrains.annotations.NotNull;

public record TextBody(
        @NotNull String content,
        int width
) implements DialogBody {

}
