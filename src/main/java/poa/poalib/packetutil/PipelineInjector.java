package poa.poalib.packetutil;

import io.netty.channel.ChannelPipeline;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import poa.poalib.PoaLib;

import java.util.logging.Level;

public class PipelineInjector { //Handled in PersistentFakeBlockEvents

    CraftPlayer craftPlayer;
    Player player;
    String id;

    public PipelineInjector(Player player, String id){
        this.craftPlayer = (CraftPlayer) player;
        this.player = player;
        this.id = player.getName() + "-" + id + "-";
    }

    public void inject(Player player) {
        PoaLib.LIB_INSTANCE.getLogger().log(Level.INFO, "Injected " + player.getName());
        ChannelPipeline pipeline = getChannelPipeline((CraftPlayer) player);
        pipeline.addBefore("packet_handler", id, new ChannelHandler(player));
    }

    private static ChannelPipeline getChannelPipeline(CraftPlayer player) {
        PoaLib.LIB_INSTANCE.getLogger().log(Level.INFO, "Got pipeline " + player.getName());
        return player.getHandle().connection.connection.channel.pipeline();
    }

    public void uninjectPlayer() {
        if (this.craftPlayer.getHandle().connection.connection.channel.pipeline().get(id) != null) {
            this.craftPlayer.getHandle().connection.connection.channel.pipeline().remove(id);
            PoaLib.LIB_INSTANCE.getLogger().log(Level.INFO, "Uninjected packet listener into " + this.player.getName());
        }
    }


}
