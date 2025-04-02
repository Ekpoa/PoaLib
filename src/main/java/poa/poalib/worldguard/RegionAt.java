package poa.poalib.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
@Deprecated
public class RegionAt {

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



}
