package poa.poalib.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemNullCheck {

    public static boolean isNull(ItemStack item){
        return (item == null || item.getType() == Material.AIR);
    }


}
