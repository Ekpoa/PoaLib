package poa.poalib.nmsrelated;

import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Set;

public class RefreshChunks {
    public static void refreshChunks(Player player, Set<Chunk> chunks, boolean unloadFirst) {
        if (player == null || chunks == null || chunks.isEmpty()) return;

        // NMS handles
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        ServerLevel nmsLevel  = nmsPlayer.level();
        ServerGamePacketListenerImpl conn = nmsPlayer.connection;
        LevelLightEngine lightEngine = nmsLevel.getLightEngine();

        for (Chunk bukkitChunk : chunks) {
            if (bukkitChunk == null || !bukkitChunk.isLoaded()) continue;

            int cx = bukkitChunk.getX();
            int cz = bukkitChunk.getZ();

            // Fetch the live LevelChunk (null if not fully loaded)
            LevelChunk levelChunk = nmsLevel.getChunkSource().getChunk(cx, cz, false);
            if (levelChunk == null) continue;

            ChunkPos pos = new ChunkPos(cx, cz);


            // 1) Tell the client to forget the chunk (safe way to force a clean re-send)
            if (unloadFirst)
                conn.send(new ClientboundForgetLevelChunkPacket(pos));

            // 2) Send a fresh full chunk + light data
            // In 1.20.5+/1.21.x this ctor carries heightmaps, biomes, light, and block data.
            conn.send(new ClientboundLevelChunkWithLightPacket(
                    levelChunk,
                    lightEngine,
                    null,    // optional custom sky mask
                    null,    // optional custom block mask
                    true     // trust edges (fine for normal worlds)
            ));
        }
    }

    public static void refreshChunksLevel(Player player, Set<LevelChunk> chunks, boolean unloadFirst) {
        if (player == null || chunks == null || chunks.isEmpty()) return;

        // NMS handles
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        ServerLevel nmsLevel  = nmsPlayer.level();
        ServerGamePacketListenerImpl conn = nmsPlayer.connection;
        LevelLightEngine lightEngine = nmsLevel.getLightEngine();

        for (LevelChunk levelChunk : chunks) {
            if(levelChunk == null || !levelChunk.loaded)
                continue;

            int cx = levelChunk.locX;
            int cz = levelChunk.locZ;


            ChunkPos pos = new ChunkPos(cx, cz);


            // 1) Tell the client to forget the chunk (safe way to force a clean re-send)
            if (unloadFirst)
                conn.send(new ClientboundForgetLevelChunkPacket(pos));

            // 2) Send a fresh full chunk + light data
            // In 1.20.5+/1.21.x this ctor carries heightmaps, biomes, light, and block data.
            conn.send(new ClientboundLevelChunkWithLightPacket(
                    levelChunk,
                    lightEngine,
                    null,    // optional custom sky mask
                    null,    // optional custom block mask
                    true     // trust edges (fine for normal worlds)
            ));
        }
    }
}
