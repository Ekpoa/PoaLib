package poa.poalib.WorldGuard;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import poa.poalib.PoaLib;

public class WorldGuardMain {
    public static RegionContainer regionContainer;
    public static WorldGuard worldGuardINSTANCE;
    public static WorldGuardPlugin worldGuardPlugin;
    public static RegionQuery regionQuery;
    public static WorldGuardPlatform worldGuardPlatform;

    public static void worldGuardVarSetup(){
        worldGuardINSTANCE = WorldGuard.getInstance();
        worldGuardPlugin = WorldGuardPlugin.inst();
        worldGuardPlatform = worldGuardINSTANCE.getPlatform();
        regionContainer = worldGuardPlatform.getRegionContainer();
        regionQuery = regionContainer.createQuery();
    }


}
