package poa.poalib.economy;

import org.bukkit.OfflinePlayer;

import static poa.poalib.PoaLib.*;

public class Economy {

    public static double getBalance(OfflinePlayer player){
        return economy.getBalance(player);
    }

    public static void setBalance(OfflinePlayer player, double balance){
        economy.withdrawPlayer(player, getBalance(player));
        economy.depositPlayer(player, balance);
    }

    public static void depositPlayer(OfflinePlayer player, double balance){
        economy.depositPlayer(player, balance);
    }

    public static void withdrawPlayer(OfflinePlayer player, double balance){
        economy.withdrawPlayer(player, balance);
    }

    public static boolean hasAccount(OfflinePlayer player){
        return economy.hasAccount(player);
    }


}
