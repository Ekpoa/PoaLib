package poa.poalib.BlockUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Warning;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import poa.poalib.PoaLib;

import java.awt.*;
import java.nio.charset.StandardCharsets;

public class BlockMarker {

    public static void createMarker(Location location, Player player, int green, int alpha, String text) {
        Bukkit.getMessenger().registerOutgoingPluginChannel(PoaLib.libINSTANCE, "minecraft:debug/game_test_add_marker");
        if (player == null) return;
        Color color = new Color(0, green, 0, alpha);

        ByteBuf buf = Unpooled.buffer();
        writeLocation(buf, location);
        buf.writeInt(color.getRGB());
        writeString(buf, text);

        int number = Integer.MAX_VALUE;
        buf.writeInt(number);

        ((CraftPlayer) player).addChannel("minecraft:debug/game_test_add_marker");
        player.sendPluginMessage(PoaLib.libINSTANCE, "minecraft:debug/game_test_add_marker", buf.array());

    }

    @Deprecated
    public static void clearAllMarkers(Player player){
        Bukkit.getMessenger().registerOutgoingPluginChannel(PoaLib.libINSTANCE, "minecraft:debug/game_test_clear");
        ((CraftPlayer) player).addChannel("minecraft:debug/game_test_clear");
        player.sendPluginMessage(PoaLib.libINSTANCE, "minecraft:debug/game_test_clear", null);
    }


    private static void writeLocation(ByteBuf buffer, Location location) {
        buffer.writeLong(
                (((long) Math.floor(location.getX())) & 67108863L) << 38 |
                        ((long) Math.floor(location.getY())) & 4095L |
                        (((long) Math.floor(location.getZ())) & 67108863L) << 12
        );
    }

    private static void writeString(ByteBuf buffer, String text) {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        buffer.writeByte(bytes.length);
        buffer.writeBytes(bytes);
    }
}
