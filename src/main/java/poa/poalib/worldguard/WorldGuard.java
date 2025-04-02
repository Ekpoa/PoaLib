package poa.poalib.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("ALL")
public class WorldGuard {

    public static List<ProtectedRegion> getRegionsAt(Location location){
        List<ProtectedRegion> regions = new ArrayList<>();

        RegionManager manager = WorldGuardMain.regionContainer.get(BukkitAdapter.adapt(location.getWorld()));
        ApplicableRegionSet regionSet = manager.getApplicableRegions(BukkitAdapter.asBlockVector(location));
        for(ProtectedRegion region : regionSet)
            regions.add(region);

        return regions;
    }

    public static List<String> getRegionsAtAsString(Location location){
        List<String> tr = new ArrayList<>();
        for(ProtectedRegion region : getRegionsAt(location))
            tr.add(region.getId());

        return tr;
    }

    public static boolean canBuild(Player player, Location location){
        if(player.hasPermission("worldguard.region.bypass." + location.getWorld().getName()))
            return true;

        return WorldGuardMain.regionQuery.testBuild(BukkitAdapter.adapt(location), WorldGuardMain.worldGuardPlugin.wrapPlayer(player));
    }




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


    public static Location getRandomLocationInRegion(String regionName, World world) {
        RegionContainer container = WorldGuardMain.regionContainer;
        RegionManager regionManager = container.get(BukkitAdapter.adapt(world));

        if (regionManager == null) return null;

        ProtectedRegion region = regionManager.getRegion(regionName);
        if (region == null) return null;

        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();

        int x = ThreadLocalRandom.current().nextInt(min.getX(), max.getX() + 1);
        int y = ThreadLocalRandom.current().nextInt(min.getY(), max.getY() + 1);
        int z = ThreadLocalRandom.current().nextInt(min.getZ(), max.getZ() + 1);

        final Location location = new Location(world, x + 0.5, y, z + 0.5);

        if(!getRegionsAtAsString(location).contains(regionName))
            return getRandomLocationInRegion(regionName, world);

        return location;
    }






}
