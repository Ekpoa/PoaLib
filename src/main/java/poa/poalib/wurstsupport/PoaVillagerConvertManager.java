package poa.poalib.wurstsupport;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import poa.poalib.PoaLib;

import java.util.List;

public class PoaVillagerConvertManager implements PluginMessageListener {

    public static final String CHANNEL = "poa:villagerconvert";


    @Override
    public void onPluginMessageReceived(
            @NotNull String channel,
            @NotNull Player player,
            byte @NotNull [] message
    ) {
        if (!CHANNEL.equals(channel))
            return;



        List<String> poaWurstSupport = PoaLib.LIB_INSTANCE.getConfig().getStringList("PoaWurstSupport");
        if(poaWurstSupport.isEmpty())
            return;

        if(!poaWurstSupport.contains(player.getUniqueId().toString()))
            return;

        Entity targetEntity = player.getTargetEntity(100);

        if(targetEntity == null)
            return;

        if(!(targetEntity instanceof ZombieVillager zombieVillager))
            return;

        zombieVillager.setConversionTime(1);
    }


}