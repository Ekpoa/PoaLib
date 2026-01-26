package poa.poalib;

import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
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

    @Override
    public void onEnable() {
        LIB_INSTANCE = this;
        saveDefaultConfig();
        setupEconomy();
        setupPermissions();
        isLoaded();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerMoveListener(), this);
        pm.registerEvents(new PersistentFakeBlockEvents(), this);

        try {
            RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
            if (provider != null) {
                lpAPI = provider.getProvider();
            }
            WorldGuardMain.worldGuardVarSetup();
        }
        catch (Exception ignored){}


        getCommand("poalibtestcommand").setExecutor(new TestCommand());

        new ActualPlaceholder().register();

    }


    public static void isLoaded(){
        LIB_INSTANCE.getLogger().log(Level.INFO, "PoaLib Loaded");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            LIB_INSTANCE.getLogger().log(Level.SEVERE, "NO VAULT");
            return false;
        }
        RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (rsp == null) {
            LIB_INSTANCE.getLogger().log(Level.SEVERE, "NO RSP FOR VAULT");
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return true;
    }

}
