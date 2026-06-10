package com.fancyinnovations.fancyworlds.worlds.view;

import com.fancyinnovations.fancyworlds.worlds.FWorldImpl;
import org.bukkit.World;

import java.util.concurrent.CompletableFuture;

public interface WorldPlatformView {

    CompletableFuture<World> createWorld(FWorldImpl world);

    CompletableFuture<Boolean> unloadWorld(World world, boolean save);

    boolean isPrimaryWorld(World world);
}
