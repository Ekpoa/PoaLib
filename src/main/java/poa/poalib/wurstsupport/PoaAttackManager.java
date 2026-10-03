package poa.poalib.wurstsupport;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import poa.poalib.PoaLib;

import java.util.List;

public class PoaAttackManager implements PluginMessageListener {
    public static final String CHANNEL = "poa:attack";



    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        if (!CHANNEL.equals(channel))
            return;


        List<String> poaWurstSupport = PoaLib.LIB_INSTANCE.getConfig().getStringList("PoaWurstSupport");
        if(poaWurstSupport.isEmpty())
            return;

        if(!poaWurstSupport.contains(player.getUniqueId().toString()))
            return;

        Entity targetEntity = player.getTargetEntity(120, true);

        switch (targetEntity) {
            case null -> {
                return;
            }
            case Player victim -> player.attack(victim);
            case LivingEntity li -> li.damage(Integer.MAX_VALUE, player);
            default -> targetEntity.remove();
        }

    }
}
