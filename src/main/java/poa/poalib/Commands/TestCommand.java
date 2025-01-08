package poa.poalib.Commands;

import lombok.SneakyThrows;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import poa.poalib.Economy.Economy;
import poa.poalib.Items.CreateItem;
import poa.poalib.PoaLib;
import poa.poalib.economyshopgui.EconomyShop;
import poa.poalib.yml.PoaYaml;

import java.io.File;
import java.util.Arrays;

public class TestCommand implements CommandExecutor {
    @SneakyThrows
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
       if(!(sender instanceof Player player))
           return false;


        return false;
    }
}
