package poa.poalib.economyshopgui;

import me.gypopo.economyshopgui.api.EconomyShopGUIHook;
import me.gypopo.economyshopgui.api.objects.BuyPrice;
import me.gypopo.economyshopgui.api.objects.SellPrice;
import me.gypopo.economyshopgui.util.EcoType;
import me.gypopo.economyshopgui.util.EconomyType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class EconomyShop {

    // THIS WORKS JUST NOT INSIDE A LIB APPARENTLY

    @Deprecated
    public static double getBuyPrice(ItemStack item, OfflinePlayer player) {
        if(player == null)
            throw new RuntimeException("player must not be null");

        final Optional<BuyPrice> buyPrice = EconomyShopGUIHook.getBuyPrice(player, item);
        return buyPrice.map(price -> price.getPrice(EconomyType.getFromString("VAULT"))).orElse(-1.0);
    }

    @Deprecated
    public static double getSellPrice(ItemStack item, OfflinePlayer player) {
        final Optional<SellPrice> sellPrice = EconomyShopGUIHook.getSellPrice(player, item);
        if(sellPrice.isEmpty())
            return -1;
        return sellPrice.get().getPrice(null);
    }

}
