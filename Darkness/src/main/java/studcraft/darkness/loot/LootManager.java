package studcraft.darkness.loot;

import org.bukkit.entity.EntityType;
import studcraft.darkness.items.ItemFactory;
import studcraft.darkness.items.weapons.SwordRusty;
import studcraft.darkness.items.weapons.SwordWaif;

import java.util.*;

public class LootManager {

    private static final Map<String, List<LootItem>> lootPools = new HashMap<>();

    static {
        // Регистрация пула лута для зомби
        registerLoot(
                "driven_zombie",
                List.of(
                    new LootItem(ItemFactory.createItem(new SwordWaif()), 1.0, 1.5)
//                    new LootItem(ItemFactory.createItem(new SwordRusty()),1.0, 1.5)
                )
        );
    }

    public static void registerLoot(String entityType, List<LootItem> lootItems) {
        lootPools.put(entityType, lootItems);
    }

    public static List<LootItem> getLootPoolForEntity(String entityType) {
        return lootPools.getOrDefault(entityType, Collections.emptyList());
    }

    public static LootItem getRandomLoot(List<LootItem> lootPool) {
        if (lootPool == null || lootPool.isEmpty()) {
            return null;
        }

        double totalWeight = lootPool.stream().mapToDouble(LootItem::getDropChance).sum();
        double randomValue = Math.random() * totalWeight;

        for (LootItem loot : lootPool) {
            randomValue -= loot.getDropChance();
            if (randomValue <= 0) {
                return loot;
            }
        }

        return null;
    }
}
