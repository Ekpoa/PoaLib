package poa.poalib.wurstsupport;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import poa.poalib.PoaLib;

import java.nio.ByteBuffer;
import java.util.List;

public class PoaJumpManager implements PluginMessageListener {
    public static final String CHANNEL = "poa:jump";

    private static final int MESSAGE_LENGTH = 12;
    private static final double MAX_DISTANCE = 1000;
    private static final double VALIDATION_DISTANCE = MAX_DISTANCE + 2;
    private static final double MAX_DISTANCE_SQUARED = VALIDATION_DISTANCE * VALIDATION_DISTANCE;

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        if (!CHANNEL.equals(channel) || message.length != MESSAGE_LENGTH)
            return;

        List<String> poaWurstSupport = PoaLib.LIB_INSTANCE.getConfig().getStringList("PoaWurstSupport");
        if(poaWurstSupport.isEmpty())
            return;

        if(!poaWurstSupport.contains(player.getUniqueId().toString()))
            return;

        ByteBuffer buffer = ByteBuffer.wrap(message);
        int x = buffer.getInt();
        int y = buffer.getInt();
        int z = buffer.getInt();

        World world = player.getWorld();
        if (y < world.getMinHeight() || y >= world.getMaxHeight() - 1)
            return;

        Location targetCenter = new Location(world, x + 0.5, y + 0.5, z + 0.5);
        if (player.getEyeLocation().distanceSquared(targetCenter) > MAX_DISTANCE_SQUARED)
            return;

        Location destination = new Location(world, x + 0.5, y + 1, z + 0.5,
                player.getYaw(), player.getPitch());

        if(!destination.getBlock().isEmpty()){
            Location l = destination.clone();
            for (int i = 0; i < 200; i++) {
                l.add(0, 1, 0);
                Material type = l.getBlock().getType();
                if(!type.isAir() && type != Material.LIGHT && type != Material.WATER && type != Material.LAVA)
                    continue;

                destination = l.clone();
                break;
            }
        }

        player.teleportAsync(destination, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }
}
