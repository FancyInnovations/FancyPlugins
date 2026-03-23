package com.fancyinnovations.fancyholograms.commands.lampCommands.conditions;

import com.fancyinnovations.fancyholograms.api.trait.HologramTrait;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HasHologramTrait {
    Class<? extends HologramTrait> value();
}
