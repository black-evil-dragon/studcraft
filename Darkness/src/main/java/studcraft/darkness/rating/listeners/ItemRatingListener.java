package studcraft.darkness.rating.listeners;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;


import studcraft.darkness.Darkness;
import studcraft.darkness.rating.ItemRating;

import static studcraft.darkness.items.CustomItem.isCustomItem;
import static studcraft.darkness.items.ItemFactory.updateLore;


public class ItemRatingListener implements Listener {

    private static final FileConfiguration CONFIG = Darkness.getInstance().getConfig();
    private static final String RATING_KEY = CONFIG.getString("rating.key", "item_rating");
    private final ItemRating itemRating;

    public ItemRatingListener() {
        this.itemRating = new ItemRating();
    }

    // При входе игрока в игру
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        assignRatingsToPlayerInventory(player);
    }

    // При создании предмета
    @EventHandler
    public void onItemCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();

        if (item != null && item.getType() != Material.AIR) {
            // Выдаем рейтинг только для завершенного предмета
            itemRating.assignRatingToItem(item, player);
        }
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();
        Player player = event.getPlayer();

        if (!item.hasItemMeta() || isCustomItem(item)) return;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();

        // Пример ключей для тегов
        NamespacedKey key = new NamespacedKey(Darkness.getInstance(), RATING_KEY);
        if (!data.has(key, PersistentDataType.INTEGER)) {
            itemRating.assignRatingToItem(item, player);
        }
    }

    private void assignRatingsToPlayerInventory(Player player) {
        // Присваиваем рейтинг всем предметам в инвентаре игрока
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                itemRating.assignRatingToItem(item, player);
            }
        }

        // Присваиваем рейтинг предметам в слотах брони
        for (ItemStack armorItem : player.getEquipment().getArmorContents()) {
            if (armorItem != null && armorItem.getType() != Material.AIR) {
                itemRating.assignRatingToItem(armorItem, player);
            }
        }
    }
}
