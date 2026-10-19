package poa.poalib.wurstsupport;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import poa.poalib.PoaLib;

import java.util.List;
import java.util.Locale;

public class PoaStatisticManager implements PluginMessageListener {

    public static final String CHANNEL = "poa:statistic";


    @Override
    public void onPluginMessageReceived(
            @NotNull String channel,
            @NotNull Player player,
            byte @NotNull [] message
    ) {
        if (!CHANNEL.equals(channel)) {
            return;
        }

        Bukkit.getScheduler().runTask(PoaLib.LIB_INSTANCE, () -> {
            if (!player.isOnline()) {
                return;
            }

            List<String> allowedPlayers = PoaLib.LIB_INSTANCE
                    .getConfig()
                    .getStringList("PoaWurstSupport");

            if (!allowedPlayers.contains(player.getUniqueId().toString())) {
                return;
            }

            handleMessage(player, message);
        });
    }


    private void handleMessage(Player player, byte[] message) {
        String command;

        FriendlyByteBuf buffer = new FriendlyByteBuf(
                Unpooled.wrappedBuffer(message)
        );

        try {
            command = buffer.readUtf();
        } catch (RuntimeException exception) {
            player.sendMessage("Invalid statistic payload.");
            return;
        } finally {
            buffer.release();
        }

        try {
            applyStatistic(player, command);
        } catch (NumberFormatException exception) {
            player.sendMessage(
                    "The statistic value must be a whole number between 0 and "
                            + Integer.MAX_VALUE + "."
            );
        } catch (IllegalArgumentException exception) {
            player.sendMessage(
                    "Unable to set statistic: " + exception.getMessage()
            );
        }
    }


    private void applyStatistic(Player player, String command) {
        String[] args = command.trim().split("\\s+");

        if (args.length < 2 || args.length > 3) {
            throw new IllegalArgumentException(
                    "Usage: <statistic> <value> [<entity/block/item>]"
            );
        }

        Statistic statistic;

        try {
            statistic = Statistic.valueOf(
                    args[0].toUpperCase(Locale.ROOT)
            );
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Unknown statistic: " + args[0]
            );
        }

        Statistic.Type type = statistic.getType();

        if (type == Statistic.Type.UNTYPED && args.length != 2) {
            throw new IllegalArgumentException(
                    statistic.name() + " does not accept an additional argument."
            );
        }

        if (type != Statistic.Type.UNTYPED && args.length != 3) {
            throw new IllegalArgumentException(
                    statistic.name() + " requires a "
                            + type.name().toLowerCase(Locale.ROOT)
                            + " argument."
            );
        }

        int value = Integer.parseInt(args[1]);

        if (value < 0) {
            throw new IllegalArgumentException(
                    "The statistic value cannot be negative."
            );
        }

        switch (type) {
            case UNTYPED -> player.setStatistic(statistic, value);

            case ENTITY -> {
                NamespacedKey key = NamespacedKey.fromString(
                        args[2].toLowerCase(Locale.ROOT)
                );

                EntityType entityType = key == null
                        ? null
                        : Registry.ENTITY_TYPE.get(key);

                if (entityType == null) {
                    throw new IllegalArgumentException(
                            "Unknown entity type: " + args[2]
                    );
                }

                player.setStatistic(statistic, entityType, value);
            }

            case BLOCK, ITEM -> {
                Material material = Material.matchMaterial(args[2]);

                if (material == null) {
                    throw new IllegalArgumentException(
                            "Unknown material: " + args[2]
                    );
                }

                if (type == Statistic.Type.BLOCK && !material.isBlock()) {
                    throw new IllegalArgumentException(
                            material.name() + " is not a block."
                    );
                }

                if (type == Statistic.Type.ITEM && !material.isItem()) {
                    throw new IllegalArgumentException(
                            material.name() + " is not an item."
                    );
                }

                player.setStatistic(statistic, material, value);
            }
        }
    }
}