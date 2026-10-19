package poa.poalib.wurstsupport;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import poa.poalib.PoaLib;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PoaGamemodeManager implements PluginMessageListener {

    public static final String CHANNEL = "poa:gamemode";

    private static final Map<UUID, GameMode> MAP = new HashMap<>();

    @Override
    public void onPluginMessageReceived(
            @NotNull String channel,
            @NotNull Player player,
            byte @NotNull [] message
    ) {
        if (!CHANNEL.equals(channel))
            return;


        List<String> poaWurstSupport = PoaLib.LIB_INSTANCE.getConfig().getStringList("PoaWurstSupport");
        if (poaWurstSupport.isEmpty())
            return;

        if (!poaWurstSupport.contains(player.getUniqueId().toString()))
            return;

        UUID uuid = player.getUniqueId();
        GameMode gameMode = MAP.get(uuid);
        if(gameMode == null){
            GameMode is = player.getGameMode();
            MAP.put(uuid, is);
            player.setGameMode(GameMode.CREATIVE);
        }
        else{
            player.setGameMode(MAP.get(uuid));
            MAP.remove(uuid);
        }

    }
}