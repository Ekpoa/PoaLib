package poa.poalib.BlockUtil;

import de.tr7zw.changeme.nbtapi.NBTChunk;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.List;

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



}
