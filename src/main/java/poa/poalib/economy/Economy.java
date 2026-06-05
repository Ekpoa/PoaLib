package poa.poalib.economy;

import org.bukkit.OfflinePlayer;
import poa.poalib.PoaLib;

public class Economy {

    private static net.milkbowl.vault.economy.Economy requireEconomy() {
        if (PoaLib.economy == null) {
            throw new IllegalStateException("Vault economy is not available.");
        }

        return PoaLib.economy;
    }

    public static double getBalance(OfflinePlayer player) {
        return requireEconomy().getBalance(player);
    }

    public static void setBalance(OfflinePlayer player, double balance) {
        net.milkbowl.vault.economy.Economy economy = requireEconomy();
        economy.withdrawPlayer(player, getBalance(player));
        economy.depositPlayer(player, balance);
    }

    public static void depositPlayer(OfflinePlayer player, double balance) {
        requireEconomy().depositPlayer(player, balance);
    }

    public static void withdrawPlayer(OfflinePlayer player, double balance) {
        requireEconomy().withdrawPlayer(player, balance);
    }

    public static boolean hasAccount(OfflinePlayer player) {
        return requireEconomy().hasAccount(player);
    }
}
