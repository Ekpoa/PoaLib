package poa.poalib.wurstsupport;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import poa.poalib.PoaLib;

import java.util.List;

public class PoaSummonManager implements PluginMessageListener {

    public static final String CHANNEL = "poa:summon";


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
            EntityType entityType = EntityType.valueOf(args[0]);

            Bukkit.getScheduler().runTask(PoaLib.LIB_INSTANCE, () -> {
                Block block = player.getTargetBlockExact(100);
                if(block == null)
                    return;
                if(args.length == 2)
                    player.getWorld().spawnEntity(block.getLocation().clone().toCenterLocation().add(0, 0.5, 0), entityType, CreatureSpawnEvent.SpawnReason.valueOf(args[1]));
                else
                    player.getWorld().spawnEntity(block.getLocation(), entityType);
            });

        }
        catch (Exception ignored){}


    }
}