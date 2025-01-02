package poa.poalib.economyshopgui;

import me.gypopo.economyshopgui.api.EconomyShopGUIHook;
import me.gypopo.economyshopgui.api.objects.BuyPrice;
import me.gypopo.economyshopgui.api.objects.SellPrice;
import me.gypopo.economyshopgui.util.EcoType;
import me.gypopo.economyshopgui.util.EconomyType;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Optional;

public class EconomyShop {

    private static final EcoType ecoType = EconomyType.getFromString("VAULT");

    public static double getBuyPrice(ItemStack item, OfflinePlayer player) {
        if(player == null)
            throw new RuntimeException("player must not be null");

        final Optional<BuyPrice> buyPrice = EconomyShopGUIHook.getBuyPrice(player, item);
        return buyPrice.map(price -> price.getPrice(ecoType)).orElse(-1.0);
    }

    public static double getSellPrice(ItemStack item, OfflinePlayer player) {
        if(player == null)
            throw new RuntimeException("player must not be null");

        final Optional<SellPrice> sellPrice = EconomyShopGUIHook.getSellPrice(player, item);
        return sellPrice.map(price -> price.getPrice(ecoType)).orElse(-1.0);
    }

}
