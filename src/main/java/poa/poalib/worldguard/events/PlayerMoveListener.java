package poa.poalib.worldguard.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import poa.poalib.worldguard.RegionAt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerMoveListener implements Listener {



    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        final Player player = e.getPlayer();
        List<String> currentRegions = getRegionsAtPlayer(player);

        List<String> previousRegions = getPreviousRegions(player);

        if (!currentRegions.equals(previousRegions)) {
            RegionChangeEvent regionEnterEvent = new RegionChangeEvent(player, currentRegions);
            Bukkit.getServer().getPluginManager().callEvent(regionEnterEvent);

            setPreviousRegions(player, currentRegions);
        }
    }


    Map<Player, List<String>> previousRegions = new HashMap<>();

    
    @EventHandler
    public void onTeleport(PlayerTeleportEvent e){
        final Player player = e.getPlayer();
        List<String> currentRegions = getRegionsAtPlayer(player);

        List<String> previousRegions = getPreviousRegions(player);

        if (!currentRegions.equals(previousRegions)) {
            RegionChangeEvent regionEnterEvent = new RegionChangeEvent(player, currentRegions);
            Bukkit.getServer().getPluginManager().callEvent(regionEnterEvent);

            setPreviousRegions(player, currentRegions);
        }
    }


    // You need to implement these methods to track the player's previous regions
    private List<String> getPreviousRegions(Player player) {
        return previousRegions.getOrDefault(player, new ArrayList<>());
    }

    private void setPreviousRegions(Player player, List<String> regions) {
       previousRegions.put(player, regions);
    }

    private List<String> getRegionsAtPlayer(Player player) {
        return RegionAt.getRegionsAtAsString(player.getLocation());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e){
        previousRegions.remove(e.getPlayer());
    }


}
