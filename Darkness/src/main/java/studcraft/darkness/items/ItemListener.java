package studcraft.darkness.items;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import studcraft.darkness.Darkness;
import studcraft.darkness.perks.PerkManager;
import studcraft.darkness.rating.ItemRating;

import static studcraft.darkness.items.CustomItem.isCustomItem;
import static studcraft.darkness.items.ItemFactory.updateLore;
import static studcraft.darkness.rating.ItemRating.RATING_KEY;

public class ItemListener implements Listener {

    private final ItemRating itemRating = new ItemRating();


    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent event) {
        // Проверяем, что атакующий - игрок
        if (!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getDamager();

        // Проверяем, что цель - живое существо
        if (!(event.getEntity() instanceof LivingEntity)) return;
        LivingEntity target = (LivingEntity) event.getEntity();

        // Получаем предмет в руке игрока
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (weapon.getType() == Material.AIR) return;

        // Проверяем наличие перков на предмете и применяем их
        PerkManager.applyItemPerks(weapon, player, target);
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();

        if (!item.hasItemMeta() || !isCustomItem(item)) return;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();

        // Пример ключей для тегов
        NamespacedKey key = new NamespacedKey(Darkness.getInstance(), RATING_KEY);
        if (!data.has(key, PersistentDataType.INTEGER)) {
            itemRating.assignRatingToItem(item, event.getPlayer());

            updateLore(item);
        }
    }
}
