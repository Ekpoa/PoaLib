package poa.poalib.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import poa.poalib.Economy.Economy;
import poa.poalib.economyshopgui.EconomyShop;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
       if(!(sender instanceof Player player))
           return false;

       //player.sendRichMessage(EconomyShop.getSellPrice(player.getInventory().getItemInMainHand(), player) + "");


        return false;
    }
}
