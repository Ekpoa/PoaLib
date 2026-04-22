package poa.poalib.worldguard.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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
import java.util.UUID;

public class PlayerMoveListener implements Listener {

    private final Map<UUID, List<String>> previousRegions = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e instanceof PlayerTeleportEvent) return;
        if (e.getTo() == null) return;

        Location from = e.getFrom();
        Location to = e.getTo();

        if (from.getWorld() != to.getWorld()) {
            handleRegionChange(e.getPlayer(), from, to, e, null);
            return;
        }

        if (from.getBlockX() == to.getBlockX()
                && from.getBlockY() == to.getBlockY()
                && from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        handleRegionChange(e.getPlayer(), from, to, e, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent e) {
        if (e.getTo() == null) return;
        handleRegionChange(e.getPlayer(), e.getFrom(), e.getTo(), null, e);
    }

    private void handleRegionChange(Player player, Location from, Location to, PlayerMoveEvent moveEvent, PlayerTeleportEvent teleportEvent) {
        List<String> oldRegions = previousRegions.computeIfAbsent(
                player.getUniqueId(),
                uuid -> new ArrayList<>(RegionAt.getRegionsAtAsString(from))
        );

        List<String> newRegions = new ArrayList<>(RegionAt.getRegionsAtAsString(to));

        if (oldRegions.equals(newRegions)) {
            return;
        }

        RegionChangeEvent event = new RegionChangeEvent(
                player,
                oldRegions,
                newRegions,
                from,
                to,
                moveEvent,
                teleportEvent
        );

        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            if (moveEvent != null) {
                moveEvent.setTo(from);
            }

            if (teleportEvent != null) {
                teleportEvent.setTo(from);
            }

            return;
        }

        previousRegions.put(player.getUniqueId(), newRegions);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        previousRegions.remove(e.getPlayer().getUniqueId());
    }
}