package poa.poalib.WorldGuard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;

import java.util.ArrayList;
import java.util.List;

public class BlocksInRegion {

    public static List<Block> getBlocksInRegion(String regionId, World world, boolean includeAir) {
        List<Block> blocksInRegion = new ArrayList<>();
        RegionManager regionManager = WorldGuardMain.regionContainer.get(BukkitAdapter.adapt(world));
        ProtectedRegion region = regionManager.getRegion(regionId);

        if (region == null)
            return blocksInRegion;



        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();
        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    Location location = new Location(world, x, y, z);
                    Block block = location.getBlock();

                    if (RegionAt.getRegionsAtAsString(block.getLocation()).contains(regionId)) {
                        Material type = block.getType();
                        if(List.of(Material.AIR, Material.VOID_AIR, Material.CAVE_AIR).contains(type)) {
                            if (includeAir)
                                blocksInRegion.add(block);
                        }
                        else
                            blocksInRegion.add(block);
                    }

                }
            }
        }
        return blocksInRegion;
    }


}
