package poa.poalib.wurstsupport.holders;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class EntityInvHolder implements InventoryHolder {

    private LivingEntity entity;

    public EntityInvHolder(LivingEntity li) {
        this.entity = li;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}
