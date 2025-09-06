package poa.poalib.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.C;
import poa.poalib.PoaLib;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("ALL")
public class WorldGuard {

    public static List<ProtectedRegion> getRegionsAt(Location location) {
        List<ProtectedRegion> regions = new ArrayList<>();

        RegionManager manager = WorldGuardMain.regionContainer.get(BukkitAdapter.adapt(location.getWorld()));
        ApplicableRegionSet regionSet = manager.getApplicableRegions(BukkitAdapter.asBlockVector(location));
        for (ProtectedRegion region : regionSet)
            regions.add(region);

        return regions;
    }

    public static List<String> getRegionsAtAsString(Location location) {
        List<String> tr = new ArrayList<>();
        for (ProtectedRegion region : getRegionsAt(location))
            tr.add(region.getId());

        return tr;
    }

    public static boolean canBuild(Player player, Location location) {
        if (player.hasPermission("worldguard.region.bypass." + location.getWorld().getName()))
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
                        if (List.of(Material.AIR, Material.VOID_AIR, Material.CAVE_AIR).contains(type)) {
                            if (includeAir)
                                blocksInRegion.add(block);
                        } else
                            blocksInRegion.add(block);
                    }

                }
            }
        }
        return blocksInRegion;
    }

    public static CompletableFuture<List<Block>> getBlocksInRegion(String regionId, World world, boolean includeAir, long timeToTake) {
        CompletableFuture<List<Block>> future = new CompletableFuture<>();
        List<Block> blocksInRegion = new ArrayList<>();

        RegionManager regionManager = WorldGuardMain.regionContainer.get(BukkitAdapter.adapt(world));
        ProtectedRegion region = regionManager.getRegion(regionId);
        if (region == null) {
            future.complete(null);
            return future;
        }

        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();

        int xMin = min.getBlockX(), xMax = max.getBlockX();
        int yMin = min.getBlockY(), yMax = max.getBlockY();
        int zMin = min.getBlockZ(), zMax = max.getBlockZ();

        int totalX = xMax - xMin + 1;
        int xPerTick = Math.max(1, totalX / (int) timeToTake);
        AtomicInteger currentX = new AtomicInteger(xMin);

        Bukkit.getScheduler().runTaskTimer(PoaLib.libINSTANCE, task -> {
            int startX = currentX.get();
            int endX = Math.min(startX + xPerTick - 1, xMax);

            for (int x = startX; x <= endX; x++) {
                for (int y = yMin; y <= yMax; y++) {
                    for (int z = zMin; z <= zMax; z++) {
                        Block block = new Location(world, x, y, z).getBlock();
                        if (RegionAt.getRegionsAtAsString(block.getLocation()).contains(regionId)) {
                            Material type = block.getType();
                            if (type.isAir()) {
                                if (includeAir) blocksInRegion.add(block);
                            } else {
                                blocksInRegion.add(block);
                            }
                        }
                    }
                }
            }

            if (endX >= xMax) {
                task.cancel();
                future.complete(blocksInRegion);
            } else {
                currentX.set(endX + 1);
            }
        }, 0L, 1L);

        return future;
    }



    public static List<Chunk> getRegionChunksCuboid(World world, ProtectedCuboidRegion cuboid, boolean loadedOnly) {
        BlockVector3 min = cuboid.getMinimumPoint();
        BlockVector3 max = cuboid.getMaximumPoint();

        int cMinX = Math.floorDiv(min.getBlockX(), 16);
        int cMaxX = Math.floorDiv(max.getBlockX(), 16);
        int cMinZ = Math.floorDiv(min.getBlockZ(), 16);
        int cMaxZ = Math.floorDiv(max.getBlockZ(), 16);

        List<Chunk> out = new ArrayList<>((cMaxX - cMinX + 1) * (cMaxZ - cMinZ + 1));
        for (int cx = cMinX; cx <= cMaxX; cx++) {
            for (int cz = cMinZ; cz <= cMaxZ; cz++) {
                if (!loadedOnly || world.isChunkLoaded(cx, cz)) {
                    out.add(world.getChunkAt(cx, cz)); // note: this loads the chunk if not loadedOnly
                }
            }
        }
        return out;
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

        if (!getRegionsAtAsString(location).contains(regionName))
            return getRandomLocationInRegion(regionName, world);

        return location;
    }

    public static Location[] getRegionCorners(World world, String regionId) {
        final RegionContainer container = WorldGuardMain.regionContainer;
        RegionManager manager = container.get(BukkitAdapter.adapt(world));
        if (manager == null) return null;

        ProtectedRegion region = manager.getRegion(regionId);
        if (!(region instanceof ProtectedCuboidRegion cuboid)) return null;

        BlockVector3 min = cuboid.getMinimumPoint();
        BlockVector3 max = cuboid.getMaximumPoint();

        Location loc1 = new Location(world, min.getBlockX(), min.getBlockY(), min.getBlockZ());
        Location loc2 = new Location(world, max.getBlockX(), max.getBlockY(), max.getBlockZ());

        return new Location[]{loc1, loc2};
    }

    public static List<String> getRegions(World world) {
        final RegionContainer container = WorldGuardMain.regionContainer;
        RegionManager manager = container.get(BukkitAdapter.adapt(world));

        if (manager == null) return List.of();

        final Map<String, ProtectedRegion> regions = manager.getRegions();
        if (regions.isEmpty())
            return List.of();

        return new ArrayList<>(regions.keySet());
    }


}
