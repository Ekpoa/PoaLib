package poa.poalib.Items;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.world.MenuProvider;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftInventoryCustom;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import poa.poalib.Messages.Messages;

import javax.annotation.Nullable;
import java.util.*;

public class CreateItem {


    public static ItemStack createBasicItem(Material material, int amount, String miniMessageName) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MiniMessage.miniMessage().deserialize("<i:false>" + miniMessageName));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createBasicItemWithLore(Material material, int amount, String miniMessageName, List<String> miniMessageStringList) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MiniMessage.miniMessage().deserialize("<i:false>" + miniMessageName));

        meta.lore(stringListToComponent(miniMessageStringList));

        item.setItemMeta(meta);
        return item;
    }


    public static ItemStack createAdvancedItem(Material material, int amount, String miniMessageName, List<String> miniMessageStringList, String nbtKey, String nbtValue) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (miniMessageName != null)
            meta.displayName(MiniMessage.miniMessage().deserialize("<i:false>" + miniMessageName));

        if (miniMessageStringList != null)
            meta.lore(stringListToComponent(miniMessageStringList));

        item.setItemMeta(meta);

        NBT.modify(item, nbt -> {
            nbt.setString(nbtKey, nbtValue);
        });
        return item;
    }


    public static List<Component> stringListToComponent(List<String> list) {
        List<Component> tr = new ArrayList<>();
        for (String s : list)
            tr.add(MiniMessage.miniMessage().deserialize("<i:false>" + Messages.essentialsToMinimessage(s)));

        return tr;
    }

    public static void fillInventory(Inventory inventory, ItemStack itemStack) {
        for (int i = 0; i < inventory.getSize(); i++) inventory.setItem(i, itemStack);
    }

    public static void fillInventory(Inventory inventory, Material material) {
        fillInventory(inventory, new ItemStack(material, 1));
    }

    public static void fillInventory(Inventory inventory, Material material, String mmName) {
        ItemStack item = createBasicItem(material, 1, mmName);
        fillInventory(inventory, item);
    }

    public static Component getItemName(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta.displayName() == null)
            return Component.text(item.getType().toString());
        return meta.displayName();
    }

    public static String getItemNameMM(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta.displayName() == null)
            return item.getType().toString();
        return MiniMessage.miniMessage().serialize(meta.displayName());
    }


    public static ItemStack deserializeItem(String base64) {
        return ItemStack.deserializeBytes(Base64.getDecoder().decode(base64));
    }

    public static String serializeItem(ItemStack itemStack) {
        ItemStack item = itemStack.clone();
        return Base64.getEncoder().encodeToString(item.serializeAsBytes());
    }





}
