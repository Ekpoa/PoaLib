package poa.poalib.wurstsupport;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import poa.poalib.PoaLib;

import java.util.List;

public class PoaThruManager implements PluginMessageListener {

    public static final String CHANNEL = "poa:poathrough";


    @Override
    public void onPluginMessageReceived(
            @NotNull String channel,
            @NotNull Player player,
            byte @NotNull [] message
    ) {
        if (!CHANNEL.equals(channel))
            return;



        List<String> poaWurstSupport = PoaLib.LIB_INSTANCE.getConfig().getStringList("PoaWurstSupport");
        if(poaWurstSupport.isEmpty())
            return;

        if(!poaWurstSupport.contains(player.getUniqueId().toString()))
            return;

        teleportThroughWall(player, 100);

    }

    public static boolean teleportThroughWall(Player player, double maxDistance) {
        Location start = player.getEyeLocation();
        Vector direction = start.getDirection().normalize();

        World world = player.getWorld();
        boolean hitWall = false;

        for (double distance = 0; distance <= maxDistance; distance += 0.2) {
            Location check = start.clone().add(direction.clone().multiply(distance));
            Block block = check.getBlock();

            if (!block.isPassable()) {
                hitWall = true;
                continue;
            }

            if (!hitWall) {
                continue;
            }

            Location target = check.clone();
            target.setY(Math.floor(target.getY()));

            Block feet = target.getBlock();
            Block head = target.clone().add(0, 1, 0).getBlock();
            Block ground = target.clone().subtract(0, 1, 0).getBlock();

            if (!feet.isPassable() || !head.isPassable() || !ground.getType().isSolid()) {
                continue;
            }

            target.add(0.5, 0, 0.5);
            target.setYaw(player.getLocation().getYaw());
            target.setPitch(player.getLocation().getPitch());

            player.teleport(target);
            return true;
        }

        return false;
    }
}