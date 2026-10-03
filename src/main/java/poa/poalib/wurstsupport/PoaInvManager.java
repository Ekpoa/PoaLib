package poa.poalib.wurstsupport;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import poa.poalib.PoaLib;
import poa.poalib.wurstsupport.holders.EntityInvHolder;
import poa.poalib.wurstsupport.holders.ItemFrameInvHolder;

import java.util.List;


public class PoaInvManager implements PluginMessageListener, Listener {
    public static final String CHANNEL = "poa:inv";



    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        if (!CHANNEL.equals(channel))
            return;

        List<String> poaWurstSupport = PoaLib.LIB_INSTANCE.getConfig().getStringList("PoaWurstSupport");
        if(poaWurstSupport.isEmpty())
            return;

        if(!poaWurstSupport.contains(player.getUniqueId().toString()))
            return;

        Entity targetEntity = player.getTargetEntity(120, true);

        if(targetEntity == null)
            return;

        if(targetEntity instanceof Player p){
            player.openInventory(p.getInventory());
            return;
        }

        if(targetEntity instanceof LivingEntity li){
            Inventory inventory = Bukkit.createInventory(new EntityInvHolder(li), 9);

            EntityEquipment equipment = li.getEquipment();

            if(equipment == null)
                return;


            inventory.setItem(0, equipment.getHelmet());
            inventory.setItem(1, equipment.getChestplate());
            inventory.setItem(2, equipment.getLeggings());
            inventory.setItem(3, equipment.getBoots());
            inventory.setItem(4, equipment.getItemInMainHand());
            inventory.setItem(5, equipment.getItemInOffHand());

            player.openInventory(inventory);
        }

        if(targetEntity instanceof ItemFrame frame){
            Inventory inventory = Bukkit.createInventory(new ItemFrameInvHolder(frame), InventoryType.HOPPER);

            inventory.setItem(2, frame.getItem());

            player.openInventory(inventory);
        }


    }

    @EventHandler
    public void entityInvClick(InventoryClickEvent e){
        Inventory inventory = e.getInventory();
        if(!(inventory.getHolder(false) instanceof EntityInvHolder holder))
            return;

        LivingEntity entity = holder.getEntity();
        if(!entity.isValid())
            return;

        EntityEquipment equipment = entity.getEquipment();

        if(equipment == null)
            return;

        Bukkit.getScheduler().runTaskLater(PoaLib.LIB_INSTANCE, () -> {
            if(!entity.isValid())
                return;

            equipment.setHelmet(inventory.getItem(0));
            equipment.setChestplate(inventory.getItem(1));
            equipment.setLeggings(inventory.getItem(2));
            equipment.setBoots(inventory.getItem(3));
            equipment.setItemInMainHand(inventory.getItem(4));
            equipment.setItemInOffHand(inventory.getItem(5));
        }, 1);


    }

    @EventHandler
    public void frameInvClick(InventoryClickEvent e){
        Inventory inventory = e.getInventory();
        if(!(inventory.getHolder(false) instanceof ItemFrameInvHolder holder))
            return;

        ItemFrame entity = holder.getFrame();
        if(!entity.isValid())
            return;

        Bukkit.getScheduler().runTaskLater(PoaLib.LIB_INSTANCE, () -> {
            if(!entity.isValid())
                return;

            entity.setItem(inventory.getItem(0));
            entity.setItem(inventory.getItem(1));
            entity.setItem(inventory.getItem(2));
            entity.setItem(inventory.getItem(3));
            entity.setItem(inventory.getItem(4));
        }, 1);


    }



}
