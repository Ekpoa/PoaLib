package poa.poalib.wurstsupport;

import io.papermc.paper.event.player.PlayerFailMoveEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import poa.poalib.PoaLib;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class NoClipManager implements Listener, PluginMessageListener {
    public static final String CHANNEL = "poa:noclip";

    private final Set<UUID> activePlayers = ConcurrentHashMap.newKeySet();

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        if (!CHANNEL.equals(channel) || message.length != 1)
            return;

        List<String> poaWurstSupport = PoaLib.LIB_INSTANCE.getConfig().getStringList("PoaWurstSupport");
        if(poaWurstSupport.isEmpty())
            return;

        if(!poaWurstSupport.contains(player.getUniqueId().toString()))
            return;

        if (message[0] == 1) {
            activePlayers.add(player.getUniqueId());
            return;
        }

        if (message[0] == 0)
            activePlayers.remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerFailMove(PlayerFailMoveEvent event) {
        if (!isNoClipping(event.getPlayer()))
            return;

        event.setAllowed(true);
        event.setLogWarning(false);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        activePlayers.remove(event.getPlayer().getUniqueId());
    }

    public boolean isNoClipping(Player player) {
        return activePlayers.contains(player.getUniqueId());
    }

    public void clear() {
        activePlayers.clear();
    }
}
