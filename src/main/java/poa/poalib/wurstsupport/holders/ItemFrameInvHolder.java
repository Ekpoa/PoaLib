package poa.poalib.wurstsupport.holders;

import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class ItemFrameInvHolder implements InventoryHolder {

    private ItemFrame frame;

    public ItemFrameInvHolder(ItemFrame frame) {
    this.frame = frame;
    }


    public ItemFrame getFrame() {
        return frame;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}
