package poa.poalib.placeholders;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Setter
@Getter
public class CustomPlaceholderEvent extends Event {


    private static final HandlerList handlers = new HandlerList();

    String id;
    String output;

    public CustomPlaceholderEvent(String id) {
        this.id = id;
    }


    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
