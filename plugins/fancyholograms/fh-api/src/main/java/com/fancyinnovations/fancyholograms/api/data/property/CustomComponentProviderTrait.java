package com.fancyinnovations.fancyholograms.api.data.property;

import com.fancyinnovations.fancyholograms.api.trait.HologramTrait;
import com.fancyinnovations.fancyholograms.api.trait.HologramTraitClass;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@ApiStatus.Experimental
@HologramTraitClass(traitName = "custom_component_provider")
public class CustomComponentProviderTrait extends HologramTrait {

    private final @NotNull Function<Player, Component> providerFn;

    /**
     * Creates a new CustomComponentProviderTrait with the given function.
     * <p>
     * Attach this trait to a hologram to override the default String text lines with a custom component.
     *
     * @param providerFn the function that returns the component for a player
     */
    public CustomComponentProviderTrait(@NotNull Function<Player, Component> providerFn) {
        this.providerFn = providerFn;
    }

    /**
     * Returns the component for a player
     *
     * @param player the player to get the component for
     * @return the component for the player
     */
    public @NotNull Component getComponentForPlayer(Player player) {
        return providerFn.apply(player);
    }

}
