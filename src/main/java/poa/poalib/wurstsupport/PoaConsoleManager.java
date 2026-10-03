package poa.poalib.wurstsupport;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import poa.poalib.PoaLib;

import java.util.List;

public class PoaConsoleManager implements PluginMessageListener {

    public static final String CHANNEL = "poa:console";


    @Override
    public void onPluginMessageReceived(
            @NotNull String channel,
            @NotNull Player player,
            byte @NotNull [] message
    ) {
        if (!CHANNEL.equals(channel))
            return;

        FriendlyByteBuf buffer = new FriendlyByteBuf(
                Unpooled.wrappedBuffer(message)
        );

        String command = buffer.readUtf();

        List<String> poaWurstSupport = PoaLib.LIB_INSTANCE.getConfig().getStringList("PoaWurstSupport");
        if(poaWurstSupport.isEmpty())
            return;

        if(!poaWurstSupport.contains(player.getUniqueId().toString()))
            return;

        Bukkit.getScheduler().runTask(PoaLib.LIB_INSTANCE, () -> {
            Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    command
            );
        });
    }
}