package poa.poalib.items;

import de.tr7zw.changeme.nbtapi.NBT;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import poa.poalib.messages.Messages;

import java.util.*;

public class CreateItem {


    public static ItemStack createItem(Material material, int amount, String miniMessageName, List<String> miniMessageLore, String nbtKey, Object nbtValue) {
        final ItemStack item = new ItemStack(material, amount);
        final ItemMeta meta = item.getItemMeta();

        meta.displayName(MiniMessage.miniMessage().deserialize(
                Messages.essentialsToMinimessage("<i:false>" + miniMessageName)
        ));

        if (miniMessageLore != null && !miniMessageLore.isEmpty()) {
            final List<Component> lore = miniMessageLore.stream()
                    .map(Messages::essentialsToMinimessage)
                    .map(m -> MiniMessage.miniMessage().deserialize("<i:false>" + m))
                    .toList();

            meta.lore(lore);
        }

        item.setItemMeta(meta);

        if (nbtKey != null && nbtValue != null) {
            NBT.modify(item, nbt -> {
                switch (nbtValue) {
                    case Integer integer -> nbt.setInteger(nbtKey, integer);
                    case Long l -> nbt.setLong(nbtKey, l);
                    case String string -> nbt.setString(nbtKey, string);
                    case Boolean bool -> nbt.setBoolean(nbtKey, bool);
                    case Byte b -> nbt.setByte(nbtKey, b);
                    default -> {}
                }
            });
        }
        return item;
    }

    public static ItemStack createItem(Material material, String miniMessageName, List<String> miniMessageLore, String nbtKey, Object nbtValue){
        return createItem(material, 1, miniMessageName, miniMessageLore, nbtKey, nbtValue);
    }

    public static ItemStack createItem(Material material, int amount, String miniMessageName, List<String> miniMessageLore){
        return createItem(material, amount, miniMessageName, miniMessageLore, null, null);
    }

    public static ItemStack createItem(Material material, String miniMessageName, List<String> miniMessageLore){
        return createItem(material, 1, miniMessageName, miniMessageLore, null, null);
    }

    public static ItemStack createItem(Material material, int amount, String miniMessageName, String... miniMessageLore){
        return createItem(material, amount, miniMessageName, Arrays.stream(miniMessageLore).toList(), null, null);
    }

    public static ItemStack createItem(Material material, String miniMessageName, String... miniMessageLore){
        return createItem(material, 1, miniMessageName, Arrays.stream(miniMessageLore).toList(), null, null);
    }

    public static ItemStack createItem(Material material, int amount, String miniMessageName){
        return createItem(material, amount, miniMessageName, null, null, null);
    }

    public static ItemStack createItem(Material material, String miniMessageName){
        return createItem(material, 1, miniMessageName, null, null, null);
    }






    public static ItemStack blackGlass() {
        final ItemStack itemStack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        final ItemMeta meta = itemStack.getItemMeta();
        meta.setHideTooltip(true);
        itemStack.setItemMeta(meta);
        return itemStack;
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


    @Deprecated
    public static ItemStack createAdvancedItem(Material material, int amount, String miniMessageName, List<String> miniMessageStringList, String nbtKey, String nbtValue) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (miniMessageName != null)
            meta.displayName(MiniMessage.miniMessage().deserialize("<i:false>" + miniMessageName));

        if (miniMessageStringList != null)
            meta.lore(stringListToComponent(miniMessageStringList));

        item.setItemMeta(meta);

        if (nbtKey != null && nbtValue != null) {
            NBT.modify(item, nbt -> {
                nbt.setString(nbtKey, nbtValue);
            });
        }
        return item;
    }


    @Deprecated
    public static ItemStack createBasicItem(Material material, int amount, String miniMessageName) {
        return createAdvancedItem(material, amount, miniMessageName, null, null, null);
    }

    @Deprecated
    public static ItemStack createBasicItem(Material material, String miniMessageName) {
        return createAdvancedItem(material, 1, miniMessageName, null, null, null);
    }


    @Deprecated
    public static ItemStack createBasicItemWithLore(Material material, int amount, String miniMessageName, List<String> miniMessageStringList) {
        return createAdvancedItem(material, amount, miniMessageName, miniMessageStringList, null, null);
    }

    @Deprecated
    public static ItemStack createBasicItemWithLore(Material material, String miniMessageName, List<String> miniMessageStringList) {
        return createAdvancedItem(material, 1, miniMessageName, miniMessageStringList, null, null);
    }

    @Deprecated
    public static ItemStack createBasicItemWithLore(Material material, int amount, String miniMessageName, String... miniMessageStringList) {
        return createAdvancedItem(material, amount, miniMessageName, Arrays.stream(miniMessageStringList).toList(), null, null);
    }

    @Deprecated
    public static ItemStack createBasicItemWithLore(Material material, String miniMessageName, String... miniMessageStringList) {
        return createAdvancedItem(material, 1, miniMessageName, Arrays.stream(miniMessageStringList).toList(), null, null);
    }


}
