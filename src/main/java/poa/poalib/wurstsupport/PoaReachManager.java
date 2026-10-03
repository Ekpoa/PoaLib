package poa.poalib.wurstsupport;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import poa.poalib.PoaLib;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PoaReachManager implements Listener, PluginMessageListener {
    public static final String CHANNEL = "poa:reach";

    private static final int MESSAGE_LENGTH = 11;
    private static final double MIN_REACH = 1;
    private static final double MAX_REACH = 100;

    private final JavaPlugin plugin;
    private final Set<UUID> activePlayers = ConcurrentHashMap.newKeySet();

    public PoaReachManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        if (!CHANNEL.equals(channel) || message.length != MESSAGE_LENGTH)
            return;

        List<String> poaWurstSupport = PoaLib.LIB_INSTANCE.getConfig().getStringList("PoaWurstSupport");
        if(poaWurstSupport.isEmpty())
            return;

        if(!poaWurstSupport.contains(player.getUniqueId().toString()))
            return;

        ByteBuffer buffer = ByteBuffer.wrap(message);
        boolean active = buffer.get() != 0;
        double reach = buffer.getDouble();
        boolean blocks = buffer.get() != 0;
        boolean entities = buffer.get() != 0;

        if (!active) {
            reset(player);
            activePlayers.remove(player.getUniqueId());
            return;
        }

        if (!Double.isFinite(reach))
            return;

        reach = Math.max(MIN_REACH, Math.min(MAX_REACH, reach));
        setAttribute(player, Attribute.BLOCK_INTERACTION_RANGE, blocks ? reach : null);
        setAttribute(player, Attribute.ENTITY_INTERACTION_RANGE, entities ? reach : null);

        if (blocks || entities)
            activePlayers.add(player.getUniqueId());
        else
            activePlayers.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!activePlayers.remove(event.getPlayer().getUniqueId()))
            return;

        reset(event.getPlayer());
    }

    public void clear() {
        for (UUID uuid : activePlayers) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null)
                reset(player);
        }

        activePlayers.clear();
    }

    private void reset(Player player) {
        setAttribute(player, Attribute.BLOCK_INTERACTION_RANGE, null);
        setAttribute(player, Attribute.ENTITY_INTERACTION_RANGE, null);
    }

    private void setAttribute(Player player, Attribute attribute, Double value) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null)
            return;

        if (value == null) {
            instance.setBaseValue(instance.getDefaultValue());
            return;
        }

        instance.setBaseValue(value);
    }
}
