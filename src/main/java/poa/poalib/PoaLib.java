package poa.poalib;

import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import poa.poalib.commands.TestCommand;
import poa.poalib.packetutil.persistentfakeblock.PersistentFakeBlockEvents;
import poa.poalib.placeholders.ActualPlaceholder;
import poa.poalib.worldguard.WorldGuardMain;
import poa.poalib.worldguard.events.PlayerMoveListener;

import java.util.logging.Level;

public final class PoaLib extends JavaPlugin {

    public static PoaLib LIB_INSTANCE;

    public static LuckPerms lpAPI;
    public static Economy economy;
    public static Permission perms;
    private static boolean worldGuardReady;

    @Override
    public void onEnable() {
        LIB_INSTANCE = this;
        saveDefaultConfig();

        setupEconomy();
        setupPermissions();
        setupLuckPerms();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PersistentFakeBlockEvents(), this);

        setupWorldGuard(pm);
        registerPlaceholderExpansion();
        registerCommands();

        isLoaded();
    }

    public static void isLoaded() {
        LIB_INSTANCE.getLogger().log(Level.INFO, "PoaLib Loaded");
    }

    public static boolean hasLuckPerms() {
        return lpAPI != null;
    }

    public static boolean hasVaultEconomy() {
        return economy != null;
    }

    public static boolean hasVaultPermissions() {
        return perms != null;
    }

    public static boolean hasWorldGuard() {
        return worldGuardReady && isPluginEnabled("WorldGuard");
    }

    public static boolean hasPlaceholderAPI() {
        return isPluginEnabled("PlaceholderAPI");
    }

    public static boolean hasEconomyShopGUI() {
        return isPluginEnabled("EconomyShopGUI") || isPluginEnabled("EconomyShopGUI-Premium");
    }

    public static boolean isPluginEnabled(String pluginName) {
        if (LIB_INSTANCE == null || pluginName == null) {
            return false;
        }

        Plugin plugin = LIB_INSTANCE.getServer().getPluginManager().getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
    }

    private boolean setupEconomy() {
        if (!isPluginEnabled("Vault")) {
            getLogger().log(Level.INFO, "Vault not found. Economy support disabled.");
            return false;
        }

        try {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                getLogger().log(Level.INFO, "Vault found, but no economy provider was registered. Economy support disabled.");
                return false;
            }

            economy = rsp.getProvider();
            return economy != null;
        } catch (Exception | LinkageError e) {
            economy = null;
            getLogger().log(Level.WARNING, "Vault economy hook failed. Economy support disabled.", e);
            return false;
        }
    }

    private boolean setupPermissions() {
        if (!isPluginEnabled("Vault")) {
            getLogger().log(Level.INFO, "Vault not found. Vault permission support disabled.");
            return false;
        }

        try {
            RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
            if (rsp == null) {
                getLogger().log(Level.INFO, "Vault found, but no permission provider was registered. Vault permission support disabled.");
                return false;
            }

            perms = rsp.getProvider();
            return perms != null;
        } catch (Exception | LinkageError e) {
            perms = null;
            getLogger().log(Level.WARNING, "Vault permission hook failed. Vault permission support disabled.", e);
            return false;
        }
    }

    private boolean setupLuckPerms() {
        if (!isPluginEnabled("LuckPerms")) {
            getLogger().log(Level.INFO, "LuckPerms not found. LuckPerms support disabled.");
            return false;
        }

        try {
            RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
            if (provider == null) {
                getLogger().log(Level.INFO, "LuckPerms found, but no service provider was registered. LuckPerms support disabled.");
                return false;
            }

            lpAPI = provider.getProvider();
            return lpAPI != null;
        } catch (Exception | LinkageError e) {
            lpAPI = null;
            getLogger().log(Level.WARNING, "LuckPerms hook failed. LuckPerms support disabled.", e);
            return false;
        }
    }

    private void setupWorldGuard(PluginManager pm) {
        if (!isPluginEnabled("WorldGuard")) {
            getLogger().log(Level.INFO, "WorldGuard not found. WorldGuard support disabled.");
            return;
        }

        try {
            WorldGuardMain.worldGuardVarSetup();
            worldGuardReady = true;
            pm.registerEvents(new PlayerMoveListener(), this);
        } catch (Exception | LinkageError e) {
            worldGuardReady = false;
            getLogger().log(Level.WARNING, "WorldGuard hook failed. WorldGuard support disabled.", e);
        }
    }

    private void registerPlaceholderExpansion() {
        if (!isPluginEnabled("PlaceholderAPI")) {
            getLogger().log(Level.INFO, "PlaceholderAPI not found. Placeholder expansion support disabled.");
            return;
        }

        try {
            new ActualPlaceholder().register();
        } catch (Exception | LinkageError e) {
            getLogger().log(Level.WARNING, "PlaceholderAPI expansion registration failed.", e);
        }
    }

    private void registerCommands() {
        PluginCommand command = getCommand("poalibtestcommand");
        if (command != null) {
            command.setExecutor(new TestCommand());
        }
    }
}
