package poa.poalib.worldguard.events;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;
import poa.poalib.worldguard.WorldGuard;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RegionChangeEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final List<String> oldRegions;
    private final List<String> newRegions;
    private final Location from;
    private final Location to;
    private final PlayerMoveEvent moveEvent;
    private final PlayerTeleportEvent teleportEvent;

    private boolean cancelled;

    public RegionChangeEvent(Player player,
                             List<String> oldRegions,
                             List<String> newRegions,
                             Location from,
                             Location to,
                             PlayerMoveEvent moveEvent,
                             PlayerTeleportEvent teleportEvent) {
        this.player = player;
        this.oldRegions = oldRegions == null ? new ArrayList<>() : new ArrayList<>(oldRegions);
        this.newRegions = newRegions == null ? new ArrayList<>() : new ArrayList<>(newRegions);
        this.from = from == null ? null : from.clone();
        this.to = to == null ? null : to.clone();
        this.moveEvent = moveEvent;
        this.teleportEvent = teleportEvent;
    }

    public List<String> getRegions(){
        return WorldGuard.getRegionsAtAsString(player.getLocation());
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void cancelAndPushBack(double blocks) {
        this.cancelled = true;
        pushBack(blocks);
    }

    public void cancelToFrom() {
        this.cancelled = true;

        if (from == null) {
            return;
        }

        if (moveEvent != null) {
            moveEvent.setTo(from.clone());
        }

        if (teleportEvent != null) {
            teleportEvent.setTo(from.clone());
        }
    }

    public void pushBack(double blocks) {
        if (from == null) {
            return;
        }

        Location safe = from.clone();

        if (to != null
                && from.getWorld() != null
                && to.getWorld() != null
                && from.getWorld().equals(to.getWorld())) {
            Vector direction = to.toVector().subtract(from.toVector());

            if (direction.lengthSquared() > 0.0001) {
                direction.normalize().multiply(blocks);
                safe.subtract(direction);
            }
        }

        safe.setYaw(from.getYaw());
        safe.setPitch(from.getPitch());

        if (moveEvent != null) {
            moveEvent.setTo(safe);
        }

        if (teleportEvent != null) {
            teleportEvent.setTo(safe);
        }
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}