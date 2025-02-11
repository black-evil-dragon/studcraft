package studcraft.darkness.perks;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import studcraft.darkness.Darkness;

import java.util.ArrayList;
import java.util.List;

public class PerkManager {

    private static final String PERKS_KEY = "item_perks";

    /**
     * Применение перков предмета к цели.
     *
     * @param item   Предмет с перками.
     * @param user   Игрок, использующий предмет.
     * @param target Цель для применения перков.
     */
    public static void applyItemPerks(ItemStack item, Player user, LivingEntity target) {
        List<Perk> perks = getItemPerks(item);
        for (Perk perk : perks) {
            if (Math.random() * 100 < perk.getActivationChance()) {
                perk.apply(user, target);
            }
        }
    }


    /**
     * Получение списка перков предмета.
     *
     * @param item Предмет.
     * @return Список перков.
     */
    public static List<Perk> getItemPerks(ItemStack item) {
        List<Perk> perks = new ArrayList<>();
        if (item == null || !item.hasItemMeta()) return perks;

        PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
        if (data.has(new NamespacedKey(Darkness.getInstance(), PERKS_KEY), PersistentDataType.STRING)) {
            String[] perkKeys = data.get(new NamespacedKey(Darkness.getInstance(), PERKS_KEY), PersistentDataType.STRING).split(",");
            for (String key : perkKeys) {
                Perk perk = PerkFactory.getPerk(key);
                if (perk != null) {
                    perks.add(perk);
                }
            }
        }
        return perks;
    }

    /**
     * Добавление перков к предмету.
     *
     * @param item  Предмет.
     * @param perks Перки для добавления.
     * @return item
     */
    public static ItemStack addPerksToItem(ItemStack item, List<String> perks) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            PersistentDataContainer data = meta.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(Darkness.getInstance(), "item_perks");

            // Пример хранения списка перков в строке
            String perksString = String.join(",", perks);
            data.set(key, PersistentDataType.STRING, perksString);

            item.setItemMeta(meta); // Не забудьте сохранить изменения
        }
        return item;
    }

    public static boolean hasPerk(Player player, String perkName) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || !item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(Darkness.getInstance(), "item_perks");

        String perks = container.get(key, PersistentDataType.STRING);
        if (perks == null) return false;

        String[] perkArray = perks.split(",");
        for (String perk : perkArray) {
            if (perk.equals(perkName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Отображение прогрессбара кулдауна над хотбаром.
     *
     * @param player Игрок, которому показывается кулдаун.
     * @param cooldownTime Длительность кулдауна в миллисекундах.
     */
    public static void startCooldownText(Player player, long cooldownTime) {
        long startTime = System.currentTimeMillis(); // Время начала кулдауна

        new BukkitRunnable() {
            @Override
            public void run() {
                long elapsed = System.currentTimeMillis() - startTime; // Прошедшее время
                if (elapsed >= cooldownTime) {
                    // Кулдаун завершён
                    player.sendActionBar("§aГотово!"); // Сообщение после завершения
                    cancel();
                    return;
                }

                // Вычисляем оставшееся время
                long timeLeft = (cooldownTime - elapsed) / 1000; // В секундах

                // Отображаем время на нижнем HUD
                player.sendActionBar("§cКулдаун: §e" + timeLeft + " сек");
            }
        }.runTaskTimer(Darkness.getInstance(), 0, 20); // Обновление каждую секунду
    }
}
