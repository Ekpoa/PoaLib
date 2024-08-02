package poa.poalib;

import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import poa.poalib.Commands.TestCommand;
import poa.poalib.WorldGuard.Events.PlayerMoveListener;
import poa.poalib.WorldGuard.WorldGuardMain;

import javax.print.DocFlavor;
import java.util.logging.Level;

public final class PoaLib extends JavaPlugin {

    public static PoaLib libINSTANCE;


    public static LuckPerms lpAPI;

    public static Economy economy;
    public static Permission perms;

    @Override
    public void onEnable() {
        libINSTANCE = this;
        saveDefaultConfig();
        setupEconomy();
        setupPermissions();
        isLoaded();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerMoveListener(), this);

        try {

            RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
            if (provider != null) {
                lpAPI = provider.getProvider();
            }
            WorldGuardMain.worldGuardVarSetup();
        }
        catch (Exception ignored){}


        getCommand("poalibtestcommand").setExecutor(new TestCommand());
    }


    public static void isLoaded(){
        libINSTANCE.getLogger().log(Level.INFO, "PoaLib Loaded");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            libINSTANCE.getLogger().log(Level.SEVERE, "NO VAULT");
            return false;
        }
        RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (rsp == null) {
            libINSTANCE.getLogger().log(Level.SEVERE, "NO RSP FOR VAULT");
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
