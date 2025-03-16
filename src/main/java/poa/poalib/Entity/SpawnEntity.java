package poa.poalib.Entity;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class SpawnEntity {

    public static List<LivingEntity> spawnEntity(Location location, Class<LivingEntity> entityClass, int amount, String miniMessageName, int maxHealth, int spawnHealth, boolean hasAI, boolean hasGravity){
        List<LivingEntity> list = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            list.add(location.getWorld().spawn(location, entityClass, (entity) -> {
                if(miniMessageName != null){
                    entity.setCustomNameVisible(true);
                    entity.customName(MiniMessage.miniMessage().deserialize(miniMessageName));
                }
                entity.getAttribute(Attribute.MAX_HEALTH).setBaseValue(maxHealth);
                entity.setHealth(spawnHealth);
                entity.setAI(hasAI);
                entity.setGravity(hasGravity);
            }));
        }
        return list;
    }

}
