package poa.poalib.wurstsupport;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import poa.poalib.PoaLib;

import java.util.List;

public class PoaGiveManager implements PluginMessageListener {

    public static final String CHANNEL = "poa:give";


    @Override
    public void onPluginMessageReceived(
            @NotNull String channel,
            @NotNull Player player,
            byte @NotNull [] message
    ) {
        if (!CHANNEL.equals(channel))
            return;

        FriendlyByteBuf buffer = new FriendlyByteBuf(
                Unpooled.wrappedBuffer(message)
        );

        String command = buffer.readUtf();

        List<String> poaWurstSupport = PoaLib.LIB_INSTANCE.getConfig().getStringList("PoaWurstSupport");
        if(poaWurstSupport.isEmpty())
            return;

        if(!poaWurstSupport.contains(player.getUniqueId().toString()))
            return;

        String[] args = command.split(" ");

        try {
            Material material = Material.getMaterial(args[0].toUpperCase());
            if(material == null)
                return;

            int amount;
            if(args.length == 2)
                amount = Integer.parseInt(args[1]);
            else {
                amount = 1;
            }

            Bukkit.getScheduler().runTask(PoaLib.LIB_INSTANCE, () -> {
                player.getInventory().addItem(new ItemStack(material, amount));
            });

        }
        catch (Exception ignored){}


    }
}