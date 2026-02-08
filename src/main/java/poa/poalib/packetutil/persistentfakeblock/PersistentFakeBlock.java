package poa.poalib.packetutil.persistentfakeblock;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import poa.poalib.PoaLib;
import poa.poalib.blockutil.BlockUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PersistentFakeBlock {

    @Getter
    private static final Map<Player, PersistentFakeBlock> DATA_MAP = new ConcurrentHashMap<>();

    @Nullable
    public static PersistentFakeBlock getPersistentFakeBlock(Player player) {
        return DATA_MAP.get(player);
    }

    public static PersistentFakeBlock getOrCreatePersistentFakeBlock(Player player) {
        return DATA_MAP.computeIfAbsent(player, PersistentFakeBlock::new);
    }

    private final Player player;

    /**
     * worldId -> chunkKey -> (blockKey -> fake BlockData)
     */
    @Getter
    private final Map<UUID, Map<Long, Map<Long, BlockData>>> worldChunkMap = new ConcurrentHashMap<>();

    private PersistentFakeBlock(Player player) {
        this.player = player;
    }

    public void setFakeBlock(Location location, BlockData blockData) {
        if (location == null || blockData == null) return;

        final World world = location.getWorld();
        if (world == null) return;

        final UUID worldId = world.getUID();
        final long chunkKey = location.getChunk().getChunkKey();
        final long blockKey = BlockUtil.blockKey(location);

        removeFakeBlock(location, false);

        if (location.isChunkLoaded()) {
            player.sendBlockChange(location, blockData);
        }

        Bukkit.getScheduler().runTaskLater(PoaLib.LIB_INSTANCE, () -> {
        worldChunkMap
                .computeIfAbsent(worldId, w -> new ConcurrentHashMap<>())
                .computeIfAbsent(chunkKey, c -> new ConcurrentHashMap<>())
                .put(blockKey, blockData);

    }, 1L);
    }

    public void removeFakeBlock(Location location, boolean showDefaultBlock) {
        if (location == null) return;

        final World world = location.getWorld();
        if (world == null) return;

        final UUID worldId = world.getUID();
        final long chunkKey = location.getChunk().getChunkKey();
        final long blockKey = BlockUtil.blockKey(location);

        final Map<Long, Map<Long, BlockData>> chunkMap = worldChunkMap.get(worldId);
        if (chunkMap == null) return;

        final Map<Long, BlockData> blockMap = chunkMap.get(chunkKey);
        if (blockMap == null) return;

        blockMap.remove(blockKey);

        if (blockMap.isEmpty()) {
            chunkMap.remove(chunkKey);
        }
        if (chunkMap.isEmpty()) {
            worldChunkMap.remove(worldId);
        }

        if (!location.isChunkLoaded()) return;

        if (showDefaultBlock) {
            player.sendBlockChange(location, location.getBlock().getBlockData());
        }
    }

    public void removeFakeBlock(Location location) {
        removeFakeBlock(location, true);
    }

    @SuppressWarnings("UnstableApiUsage")
    public void resetAll() {
        final Map<Location, BlockData> restore = new HashMap<>();

        for (Map.Entry<UUID, Map<Long, Map<Long, BlockData>>> worldEntry : worldChunkMap.entrySet()) {
            final World world = player.getServer().getWorld(worldEntry.getKey());
            if (world == null) continue;

            for (Map<Long, BlockData> blockMap : worldEntry.getValue().values()) {
                for (long blockKey : blockMap.keySet()) {
                    final Location location = BlockUtil.locationFromBlockKey(world, blockKey);
                    if (!location.isChunkLoaded()) continue;

                    restore.put(location, location.getBlock().getBlockData());
                }
            }
        }

        if (!restore.isEmpty()) {
            player.sendMultiBlockChange(restore);
        }

        worldChunkMap.clear();
        DATA_MAP.remove(player);
    }
}
