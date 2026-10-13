package de.oliver.fancyvisuals.utils;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;


public class VaultHelper {

    private static boolean vaultLoaded = false;
    private static Permission permission;

    public static void loadVault() {
        vaultLoaded = Bukkit.getPluginManager().getPlugin("Vault") != null;
        if (!vaultLoaded) {
            return;
        }

        RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        permission = rsp == null ? null : rsp.getProvider();
    }

    public static boolean isVaultLoaded() {
        return vaultLoaded;
    }

    public static Permission getPermission() {
        return permission;
    }
}
