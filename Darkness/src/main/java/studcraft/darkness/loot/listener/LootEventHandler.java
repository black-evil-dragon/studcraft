package studcraft.darkness.loot.listener;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;
import studcraft.darkness.Darkness;
import studcraft.darkness.items.ItemFactory;
import studcraft.darkness.loot.LootItem;
import studcraft.darkness.loot.LootManager;

import java.util.List;

public class LootEventHandler implements Listener {

    private final NamespacedKey ns_key = new NamespacedKey(Darkness.getInstance(), "entity_type");

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        // Получаем тип сущности
        Entity entity = event.getEntity();

        // Получаем пул лута для сущности
        String entityType = entity.getPersistentDataContainer().getOrDefault(
                ns_key,
                PersistentDataType.STRING,
                ""
        );
        List<LootItem> lootPool = LootManager.getLootPoolForEntity(entityType);

        // Если есть лут, выбираем случайный предмет
        LootItem loot = LootManager.getRandomLoot(lootPool);

        if (loot != null) {
            event.getDrops().add(loot.getItem());
        }
    }
}
