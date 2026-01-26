package poa.poalib.packetutil.persistentfakeblock;

import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import poa.poalib.blockutil.BlockUtil;
import poa.poalib.packetutil.PipelineInjector;

import java.util.HashMap;
import java.util.Map;

public class PersistentFakeBlockEvents implements Listener {


    @SuppressWarnings("UnstableApiUsage")
    @EventHandler
    public void onChunkLoad(PlayerChunkLoadEvent e) {
        final Player player = e.getPlayer();

        final PersistentFakeBlock pfb = PersistentFakeBlock.getPersistentFakeBlock(player);
        if (pfb == null) return;

        final World world = e.getWorld();

        final long chunkKey = e.getChunk().getChunkKey();

        final Map<Long, Map<Long, BlockData>> chunkMap = pfb.getWorldChunkMap().get(world.getUID());
        if (chunkMap == null) return;

        final Map<Long, BlockData> blocks = chunkMap.get(chunkKey);
        if (blocks == null || blocks.isEmpty()) return;

        final Map<Location, BlockData> send = new HashMap<>(blocks.size());
        for (Map.Entry<Long, BlockData> entry : blocks.entrySet()) {
            send.put(BlockUtil.locationFromBlockKey(world, entry.getKey()), entry.getValue());
        }

        player.sendMultiBlockChange(send);
    }


    private static final Map<Player, PipelineInjector> INJECTOR_MAP = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        final Player player = e.getPlayer();
        final PipelineInjector injector = new PipelineInjector(player, "PoaLib");
        injector.inject(player);

        INJECTOR_MAP.put(player, injector);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        final Player player = e.getPlayer();

        INJECTOR_MAP.get(player).uninjectPlayer();

        INJECTOR_MAP.remove(player);
    }


}
