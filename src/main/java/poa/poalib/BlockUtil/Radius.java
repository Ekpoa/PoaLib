package poa.poalib.BlockUtil;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class Radius {

    public static List<Block> getCircle(Location loc, int radius, boolean square) {
        World world = loc.getWorld();
        int
                minX = loc.getBlockX() - radius,
                minZ = loc.getBlockZ() - radius,
                maxX = loc.getBlockX() + radius,
                y = loc.getBlockY(),
                maxZ = loc.getBlockZ() + radius;

        List<Block> tr = new ArrayList<>();

        for (int x = minX; x <= maxX; x++)
            for (int z = minZ; z <= maxZ; z++) {
                final Location location = new Location(world, x, y, z);
                if (!square) {
                    if (loc.distanceSquared(loc) <= (radius * radius)) {
                        tr.add(world.getBlockAt(location));
                    }
                }
                else
                    tr.add(world.getBlockAt(location));
            }

        return tr;
    }
    public static List<Block> getCircle(Location loc, int radius){
        return getCircle(loc, radius, false);
    }

    public static List<Block> getSquare(Location loc, int radius){
        return getCircle(loc, radius, true);
    }


    public static List<Block> getSphere(Location loc, int radius, boolean includeAir) {
        World world = loc.getWorld();
        int
                minX = loc.getBlockX() - radius,
                minY = loc.getBlockY() - radius,
                minZ = loc.getBlockZ() - radius,
                maxX = loc.getBlockX() + radius,
                maxY = loc.getBlockY() + radius,
                maxZ = loc.getBlockZ() + radius;

        List<Block> tr = new ArrayList<>();

        for (int x = minX; x <= maxX; x++)
            for (int y = minY; y <= maxY; y++)
                for (int z = minZ; z <= maxZ; z++) {
                    Location location = new Location(world, x, y, z);
                    if (!location.isChunkLoaded())
                        continue;

                    if (location.distanceSquared(loc) <= radius * radius) {
                        Block blockAt = world.getBlockAt(location);
                        if (!blockAt.getType().isAir())
                            tr.add(blockAt);
                        else if (includeAir)
                            tr.add(blockAt);

                    }
                }
        return tr;
    }

    public static List<Block> getSphere(Location loc, int radius) {
        return getSphere(loc, radius, true);
    }

    public static List<Block> getSpiralBlocks(Location center, int radius, boolean includeAir) {
        List<Block> blocks = new ArrayList<>();

        for (int y = -radius; y <= radius; y++) {
            int ySquared = y * y;

            for (int x = -radius; x <= radius; x++) {
                int xSquared = x * x;

                for (int z = -radius; z <= radius; z++) {
                    if (xSquared + ySquared + z * z <= radius * radius) {
                        Block block = center.clone().add(x, y, z).getBlock();

                        if (includeAir || !block.getType().isAir()) {
                            blocks.add(block);
                        }
                    }
                }
            }
        }

        // Sorting the list to create an outward spiral effect
        blocks.sort((b1, b2) -> {
            double dist1 = b1.getLocation().distanceSquared(center);
            double dist2 = b2.getLocation().distanceSquared(center);
            return Double.compare(dist1, dist2);
        });

        return blocks;
    }

}
