package poa.poalib.packetutil;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.entity.Player;
import poa.poalib.blockutil.BlockUtil;
import poa.poalib.packetutil.persistentfakeblock.PersistentFakeBlock;

import java.util.Map;

public class ChannelHandler extends ChannelDuplexHandler {

    Player player;


    public ChannelHandler(Player player) {
        this.player = player;
    }


    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(!(msg instanceof Packet<?> packet)) {
            super.write(ctx, msg, promise);
            return;
        }
        if(packet instanceof ClientboundBlockUpdatePacket updatePacket){
            final BlockPos pos = updatePacket.getPos();

            final PersistentFakeBlock pfb = PersistentFakeBlock.getPersistentFakeBlock(player);
            if(pfb == null){
                super.write(ctx, msg, promise);
                return;
            }
            final World world = player.getWorld();

            final long chunkKey = Chunk.getChunkKey(pos.getX() >> 4, pos.getZ() >> 4);
            final long blockKey = BlockUtil.blockKey(pos.getX(), pos.getY(), pos.getZ());

            final Map<Long, Map<Long, BlockData>> chunkMap =
                    pfb.getWorldChunkMap().get(world.getUID());
            if (chunkMap == null){
                super.write(ctx, msg, promise);
                return;
            }

            final Map<Long, BlockData> blockMap = chunkMap.get(chunkKey);
            if (blockMap == null){
                super.write(ctx, msg, promise);
                return;
            }

            final BlockData fake = blockMap.get(blockKey);
            if (fake == null){
                super.write(ctx, msg, promise);
                return;
            }


            final ClientboundBlockUpdatePacket overwritePacket = new ClientboundBlockUpdatePacket(pos, ((CraftBlockData) fake).getState());

            super.write(ctx, overwritePacket, promise);
            return;
        }

        super.write(ctx, msg, promise);
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        super.read(ctx);
    }
}
