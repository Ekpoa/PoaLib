package poa.poalib.worldguard.events;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

@Getter
public class RegionChangeEvent extends Event {


    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private List<String> regions;
    private PlayerMoveEvent moveEvent;
    private PlayerTeleportEvent teleportEvent;

    public RegionChangeEvent(Player player, List<String> regions, PlayerMoveEvent moveEvent, PlayerTeleportEvent teleportEvent) {
        this.player = player;
        this.regions = regions;
        this.moveEvent = moveEvent;
        this.teleportEvent = teleportEvent;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
