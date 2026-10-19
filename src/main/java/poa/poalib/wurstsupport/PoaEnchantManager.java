package poa.poalib.wurstsupport;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import poa.poalib.PoaLib;

import java.util.List;
import java.util.Locale;

public class PoaEnchantManager implements PluginMessageListener {

    public static final String CHANNEL = "poa:enchant";


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

        String[] args = command.split(" ");

        try {
            Enchantment enchantment = getEnchantment(args[0]);
            if(enchantment == null)
                return;

            int level;
            if(args.length == 2)
                level = Integer.parseInt(args[1]);
            else {
                level = 1;
            }

            Bukkit.getScheduler().runTask(PoaLib.LIB_INSTANCE, () -> {
                ItemStack item = player.getInventory().getItemInMainHand();
                ItemMeta meta = item.getItemMeta();
                meta.addEnchant(enchantment, level, true);
                item.setItemMeta(meta);
            });

        }
        catch (Exception ignored){}


    }

    public static Enchantment getEnchantment(String name) {
        NamespacedKey key = NamespacedKey.fromString(name.toLowerCase(Locale.ROOT));

        if (key == null) {
            return null;
        }

        return Registry.ENCHANTMENT.get(key);
    }
}