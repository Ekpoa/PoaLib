package poa.poalib.BlockUtil;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
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



}
