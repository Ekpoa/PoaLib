package poa.poalib.wurstsupport;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import poa.poalib.PoaLib;

import java.util.List;

public class PoaDupeManager implements PluginMessageListener {
    public static final String CHANNEL = "poa:dupe";



    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        if (!CHANNEL.equals(channel))
            return;


        List<String> poaWurstSupport = PoaLib.LIB_INSTANCE.getConfig().getStringList("PoaWurstSupport");
        if(poaWurstSupport.isEmpty())
            return;

        if(!poaWurstSupport.contains(player.getUniqueId().toString()))
            return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if(item.isEmpty())
            return;

        player.getInventory().addItem(item);

    }
}
