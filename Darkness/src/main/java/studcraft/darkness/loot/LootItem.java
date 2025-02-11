package studcraft.darkness.loot;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import studcraft.darkness.Darkness;

import static studcraft.darkness.items.ItemFactory.ITEM_RARE;
import static studcraft.darkness.rating.ItemRating.RATING_MULTIPLIER;

public class LootItem {
    private final ItemStack item;
    private final double dropChance; // Шанс выпадения от 0 до 1 (например, 0.1 = 10%)

    public LootItem(ItemStack item, double dropChance) {
        this.item = item;
        this.dropChance = dropChance;
    }
    public LootItem(ItemStack item, double dropChance, double ratingMultiplier) {
        this.item = item;
        this.dropChance = dropChance;

        ItemMeta meta = this.item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(
            new NamespacedKey(Darkness.getInstance(), RATING_MULTIPLIER),
            PersistentDataType.DOUBLE,
            ratingMultiplier
        );
        this.item.setItemMeta(meta);
    }

    public ItemStack getItem() {
        return item;
    }

    public double getDropChance() {
        return dropChance;
    }
}
