package poa.poalib.entity;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.List;

public class PlayerUtil {
    public static List<Block> getBlocksInHitbox(Player player, float expand) {
        BoundingBox box = player.getBoundingBox().expand(expand);
        World world = player.getWorld();
        List<Block> blocks = new ArrayList<>();

        int minX = (int) Math.floor(box.getMinX());
        int maxX = (int) Math.floor(box.getMaxX());
        int minY = (int) Math.floor(box.getMinY());
        int maxY = (int) Math.floor(box.getMaxY());
        int minZ = (int) Math.floor(box.getMinZ());
        int maxZ = (int) Math.floor(box.getMaxZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);

                    // fluids and non-solid blocks often have null bounding boxes, so include them directly
                    if (block.isLiquid()) {
                        blocks.add(block);
                        continue;
                    }

                    BoundingBox blockBox = block.getBoundingBox();
                    if (box.overlaps(blockBox)) {
                        blocks.add(block);
                    }
                }
            }
        }

        return blocks;
    }
    public static List<Block> getBlocksInHitbox(Player player) {
        return getBlocksInHitbox(player, 0);
    }


}
