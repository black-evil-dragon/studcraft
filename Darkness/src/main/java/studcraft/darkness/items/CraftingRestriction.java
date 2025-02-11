package studcraft.darkness.items;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class CraftingRestriction implements Listener {

    public final NamespacedKey restrictedKey;

    public CraftingRestriction(NamespacedKey key) {
        this.restrictedKey = key;
    }

    // Обработчик для подготовки предметов крафта
    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();
        for (ItemStack item : inventory.getMatrix()) {
            if (isRestrictedItem(item)) {
                inventory.setResult(null); // Убираем результат крафта
                break;
            }
        }
    }

    // Обработчик для предотвращения завершения крафта
    @EventHandler
    public void onCraft(CraftItemEvent event) {
        CraftingInventory inventory = event.getInventory();
        for (ItemStack item : inventory.getMatrix()) {
            if (isRestrictedItem(item)) {
                event.setCancelled(true); // Отменяем крафт
                event.getWhoClicked().sendMessage("§cЭтот предмет нельзя использовать в крафте!");
                break;
            }
        }
    }

    // Проверка, является ли предмет запрещённым для крафта
    private boolean isRestrictedItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;

        var meta = item.getItemMeta();
        if (meta == null) return false;

        PersistentDataContainer data = meta.getPersistentDataContainer();
        return data.has(restrictedKey, PersistentDataType.STRING);
    }
}
