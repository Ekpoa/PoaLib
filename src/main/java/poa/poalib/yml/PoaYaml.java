package poa.poalib.yml;

import lombok.SneakyThrows;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.inventory.CraftInventoryCustom;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import poa.poalib.Items.CreateItem;
import poa.poalib.PoaLib;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PoaYaml extends YamlConfiguration {

    public void saveAsync(File file) {
        Bukkit.getScheduler().runTaskAsynchronously(PoaLib.libINSTANCE, () -> {
            try {
                this.save(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void addStringList(String node, String string) {
        List<String> list = new ArrayList<>();
        if (this.isSet(node))
            list = this.getStringList(node);

        list.add(string);

        this.set(node, list);
    }

    public void setUUID(String node, UUID uuid){
        this.set(node, uuid.toString());
    }
    public UUID getUUID(String node){
        return UUID.fromString(this.getString(node));
    }

    public void setItemStack(String node, ItemStack item) {
        this.set(node, CreateItem.serializeItem(item.clone()));
    }

    public ItemStack getSerializedItemStack(String node) {
        return CreateItem.deserializeItem(this.getString(node));
    }

    public void setEntity(String node, Entity entity) {
        this.set(node, entity.createSnapshot().getAsString());
    }

    public void setEntity(String node, EntitySnapshot snapshot) {
        this.set(node, snapshot.getAsString());
    }

    public EntitySnapshot getEntity(String node) {
        return Bukkit.getEntityFactory().createEntitySnapshot(this.getString(node));
    }

    public void setInventory(String node, Inventory inventory) {
        final CraftInventoryCustom craft = (CraftInventoryCustom) inventory;
        this.set(node, null);

        String title = "null";
        if (craft.title() != null)
            title = MiniMessage.miniMessage().serialize(craft.title());

        this.set(node + ".Type", inventory.getType().toString());
        this.set(node + ".Size", inventory.getSize());
        this.set(node + ".Title", title);

        for (int i = 0; i < inventory.getSize(); i++) {
            final ItemStack item = inventory.getItem(i);
            if (item == null || item.getType().isAir())
                continue;

            this.set(node + ".Items." + i, CreateItem.serializeItem(item));
        }
    }

    public Inventory getInventory(String node, @Nullable InventoryHolder holder) {
        if (!this.isConfigurationSection(node))
            throw new RuntimeException(node + " is not a serialized inventory");

        InventoryType type = InventoryType.valueOf(this.getString(node + ".Type"));
        int size = this.getInt(node + ".Size");
        String title = this.getString(node + ".Title");
        if(title == null || title.equalsIgnoreCase("null"))
            title = type.getDefaultTitle();

        Inventory inventory;
        if(type == InventoryType.CHEST)
            inventory = Bukkit.createInventory(holder, size, MiniMessage.miniMessage().deserialize(title));
        else
            inventory = Bukkit.createInventory(holder, type, MiniMessage.miniMessage().deserialize(title));

        if(!this.isConfigurationSection( node + ".Items"))
            return inventory;

        for (String key : this.getConfigurationSection(node + ".Items").getKeys(false)) {
            int slot = Integer.parseInt(key);

            inventory.setItem(slot, CreateItem.deserializeItem(this.getString(node + ".Items." + key)));
        }


        return inventory;
    }


    @SneakyThrows
    public static PoaYaml loadFromFile(File file) {
        PoaYaml config = new PoaYaml();
        config.load(file);
        return config;
    }


}
