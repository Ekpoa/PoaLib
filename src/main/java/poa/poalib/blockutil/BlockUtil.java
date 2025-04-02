package poa.poalib.blockutil;

import de.tr7zw.changeme.nbtapi.NBTChunk;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BlockUtil {


    public static Block nextSolidBlock(Location location) {
        Location loc = location.clone();

        if (location.getBlock().getType() == Material.AIR) {
            for (int i = 0; i < 320; i++) {
                loc.add(0, -1, 0);
                if (loc.getBlock().getType() != Material.AIR)
                    return loc.getBlock();
            }
        }
        loc = location.clone();
        for (int i = 0; i < 320; i++) {
            loc.add(0, 1, 0);
            if (loc.getBlock().getType() == Material.AIR) {
                return loc.getBlock();

            }
        }
        return null;
    }

    public static List<Block> blocksInBoundingBox(BoundingBox box, World world){
        List<Block> tr = new ArrayList<>();

        for(double x = box.getMinX(); x < box.getMaxX(); x++)
            for(double y = box.getMinY(); y < box.getMaxY(); y++)
                for(double z = box.getMinZ(); z < box.getMaxZ(); z++)
                    tr.add(world.getBlockAt(new Location(world,x,y,z)));

        return tr;
    }

    public static void removeBlockNBT(Block block) {
        NBTCompound chunkContainer = new NBTChunk(block.getChunk()).getPersistentDataContainer();
        if (chunkContainer.hasTag("blocks")) {
            NBTCompound blocksContainer = chunkContainer.getOrCreateCompound("blocks");
            removeNBT(blocksContainer, block);

            if (block.getBlockData() instanceof Bisected bisected) {
                BlockFace face = bisected.getHalf() == Bisected.Half.BOTTOM ? BlockFace.UP : BlockFace.DOWN;
                removeNBT(blocksContainer, block.getRelative(face));
            }
            if (blocksContainer.getKeys().isEmpty()) chunkContainer.removeKey("blocks");
        }
    }

    private static void removeNBT(NBTCompound blocksContainer, Block block) {
        String blockKey = String.format("%s_%s_%s", block.getX(), block.getY(), block.getZ());
        if (blocksContainer.hasTag(blockKey)) {
            blocksContainer.removeKey(blockKey);
        }
    }


    public static Location getRandomLocationFromLoadedChunks(Player player) {
        World world = player.getWorld();
        List<Chunk> loadedChunks = new ArrayList<>(Arrays.asList(world.getLoadedChunks()));

        if (loadedChunks.isEmpty()) {
            return null; // No loaded chunks
        }

        // Select a random chunk
        Chunk randomChunk = loadedChunks.get(ThreadLocalRandom.current().nextInt(loadedChunks.size()));

        // Get random X and Z within chunk bounds
        int x = (randomChunk.getX() << 4) + ThreadLocalRandom.current().nextInt(16);
        int z = (randomChunk.getZ() << 4) + ThreadLocalRandom.current().nextInt(16);

        // Find highest solid block at (x, z)
        int y = world.getHighestBlockYAt(x, z);

        return new Location(world, x + 0.5, y, z + 0.5); // Center position
    }


    public static Location getRandomLocationNearPlayer(Player player, int radius) {
        World world = player.getWorld();
        Location playerLoc = player.getLocation();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        // Get random X and Z within radius
        double angle = random.nextDouble(0, Math.PI * 2); // Random direction
        double distance = random.nextDouble(0, radius); // Random distance

        int x = playerLoc.getBlockX() + (int) (Math.cos(angle) * distance);
        int z = playerLoc.getBlockZ() + (int) (Math.sin(angle) * distance);

        // Find the highest solid block at (x, z)
        int y = world.getHighestBlockYAt(x, z);

        return new Location(world, x + 0.5, y, z + 0.5); // Center in block
    }



}
