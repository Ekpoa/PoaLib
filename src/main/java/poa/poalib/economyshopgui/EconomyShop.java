package poa.poalib.economyshopgui;

import me.gypopo.economyshopgui.api.EconomyShopGUIHook;
import me.gypopo.economyshopgui.api.objects.BuyPrice;
import me.gypopo.economyshopgui.api.objects.SellPrice;
import me.gypopo.economyshopgui.util.EconomyType;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.inventory.ItemStack;
import poa.poalib.PoaLib;

import java.util.Optional;

public class EconomyShop {

    private static boolean isAvailable() {
        return PoaLib.hasEconomyShopGUI();
    }

    public static double buyPrice(ItemStack item, OfflinePlayer player) {
        if (player == null)
            throw new RuntimeException("player must not be null");

        if (!isAvailable())
            return -1.0;

        try {
            final Optional<BuyPrice> buyPrice = EconomyShopGUIHook.getBuyPrice(player, item);
            return buyPrice.map(price -> price.getPrice(EconomyType.getFromString("VAULT"))).orElse(-1.0);
        } catch (Exception | LinkageError ignored) {
            return -1.0;
        }
    }

    public static double sellPrice(ItemStack item, OfflinePlayer player) {
        if (!isAvailable())
            return -1.0;

        try {
            final Optional<SellPrice> sellPrice = EconomyShopGUIHook.getSellPrice(player, item);
            if (sellPrice.isEmpty())
                return -1;
            return sellPrice.get().getPrice(EconomyType.getFromString("VAULT"));
        } catch (Exception | LinkageError ignored) {
            return -1.0;
        }
    }

    public static double priceOfAllItemsInChunk(Chunk chunk, boolean removeItem, OfflinePlayer player) {
        double tr = 0;
        for (BlockState tileEntity : chunk.getTileEntities(false)) {
            if (!(tileEntity instanceof Container container))
                continue;

            for (ItemStack item : container.getInventory().getStorageContents()) {
                if (item == null || item.isEmpty())
                    continue;

                final double price = sellPrice(item, player);

                if (price == -1 || price == 0)
                    continue;

                tr = tr + price;
                if (removeItem)
                    item.setAmount(0);
            }
        }

        return tr;
    }
}
