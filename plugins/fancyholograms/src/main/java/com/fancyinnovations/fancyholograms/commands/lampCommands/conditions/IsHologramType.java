package com.fancyinnovations.fancyholograms.commands.lampCommands.conditions;

import com.fancyinnovations.fancyholograms.api.hologram.HologramType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IsHologramType {
    HologramType[] types();
}
