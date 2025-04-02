package poa.poalib.worldguard.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import poa.poalib.worldguard.RegionAt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerMoveListener implements Listener {



    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Assume getRegionsAtPlayer() is the method that returns the list of regions for a player
        List<String> currentRegions = getRegionsAtPlayer(event.getPlayer());

        // You need to store the previous regions of the player somewhere, for comparison
        List<String> previousRegions = getPreviousRegions(event.getPlayer());

        if (!currentRegions.equals(previousRegions)) {
            // Player has entered a new region, fire the custom event
            RegionChangeEvent regionEnterEvent = new RegionChangeEvent(event.getPlayer(), currentRegions);
            Bukkit.getServer().getPluginManager().callEvent(regionEnterEvent);

            // Update the previous regions
            setPreviousRegions(event.getPlayer(), currentRegions);
        }
    }


    Map<Player, List<String>> previousRegions = new HashMap<>();



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
