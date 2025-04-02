package poa.poalib.worldguard.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class RegionChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private List<String> regions;

    public RegionChangeEvent(Player player, List<String> regions) {
        this.player = player;
        this.regions = regions;
    }

    public Player getPlayer() {
        return player;
    }

    public List<String> getRegions() {
        return regions;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
