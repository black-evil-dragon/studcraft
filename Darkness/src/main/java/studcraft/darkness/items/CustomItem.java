package studcraft.darkness.items;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import studcraft.darkness.Darkness;

import java.util.List;
import java.util.Map;


public class CustomItem {
    private final String id;
    private final String name;
    private final String rarity;
    private final String lore;
    private final List<String> perks;
    private final Map<String, Double> stats;
    private final Material material;



    public static final NamespacedKey CUSTOM_ITEM_KEY = new NamespacedKey(Darkness.getInstance(), "is_custom_item");



    public CustomItem(String id, String name, String rarity, String lore, List<String> perks, Map<String, Double> stats, Material material) {
        this.id = id;
        this.name = name;
        this.rarity = rarity;
        this.lore = lore;
        this.perks = perks;
        this.stats = stats;
        this.material = material;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRarity() {
        return rarity;
    }

    public String getLore() {
        return lore;
    }

    public List<String> getPerks() {
        return perks;
    }

    public Map<String, Double> getStats() {
        return stats;
    }

    public Material getMaterial() {
        return material;
    }


    public static boolean isCustomItem(ItemStack item) {
        if (item == null || item.getItemMeta() == null) return false;

        PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
        return data.has(CUSTOM_ITEM_KEY, PersistentDataType.BYTE);
    }
}
//package studcraft.darkness.items;
//
//import net.kyori.adventure.text.Component;
//import org.apache.commons.lang3.ObjectUtils;
//import org.bukkit.Material;
//import org.bukkit.NamespacedKey;
//import org.bukkit.configuration.file.FileConfiguration;
//import org.bukkit.entity.LivingEntity;
//import org.bukkit.entity.Player;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.Listener;
//import org.bukkit.event.entity.EntityDamageByEntityEvent;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.meta.ItemMeta;
//import org.bukkit.persistence.PersistentDataContainer;
//import org.bukkit.persistence.PersistentDataType;
//import studcraft.darkness.Darkness;
//import studcraft.darkness.perks.Perk;
//import studcraft.darkness.perks.PerkManager;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import static studcraft.darkness.perks.PerkManager.getItemPerks;
//import static studcraft.darkness.rating.ItemRating.BASE_RATING;
//import static studcraft.darkness.rating.ItemRating.RATING_KEY;
//
//public abstract class CustomItem implements Listener {
//

//
//    protected String id; // Уникальный ID предмета
//    protected String name; // Отображаемое имя
//    protected String rarity; // Редкость
//    protected List<String> lore; // Описание предмета
//    protected Map<String, Integer> stats; // Характеристики (например, урон, защита)
//    protected String rating_key;
//    protected List<String> perks;
//
//    public CustomItem(
//        String id,
//        String name,
//        String rarity,
//        List<String> lore,
//        List<String> perks
//    ) {
//        this.id = id;
//        this.name = name;
//        this.rarity = rarity;
//        this.lore = lore;
//        this.rating_key = RATING_KEY;
//        this.perks = perks;
//    }
//
//
//
//    public static void updateLoreItem(ItemStack item) {
//        ItemMeta meta = item.getItemMeta();
//        if (meta == null) {
//            return;
//        }
//
//        PersistentDataContainer data = meta.getPersistentDataContainer();
//        NamespacedKey ratingKey = new NamespacedKey(Darkness.getInstance(), RATING_KEY);
//
//        int rating = data.getOrDefault(ratingKey, PersistentDataType.INTEGER, BASE_RATING);
//        List<Perk> perks = getItemPerks(item);
//
//        List<Component> lore = new ArrayList<>();
//
//
//        lore.add(
//            Component.text(
//                getRarityName(item) + "§7 | " + rating + " очков силы§r"
//            )
//        );
//
//        if (!perks.isEmpty()) {
//            lore.add(Component.text(""));
//            lore.add(Component.text("§7Перки:§r"));
//            for (Perk perk : perks) {
//                lore.add(Component.text(" " + perk.getName() + "§r"));
//            }
//        }
//        meta.lore(lore);
//        item.setItemMeta(meta);
//    }
//
//
//    /**
//     * Возвращает название редкости по ключу.
//     *
//     * @param item Предмет.
//     * @return Название редкости или "Неизвестная редкость", если ключ не найден.
//     */
//    public static String getRarityName(ItemStack item) {
//        ItemMeta meta = item.getItemMeta();
//        PersistentDataContainer data = meta.getPersistentDataContainer();
//
//        NamespacedKey key = new NamespacedKey(Darkness.getInstance(), "rare_key");
//        String rare_value = "";
//
//        if (!data.has(key, PersistentDataType.STRING)) {
//            data.set(key, PersistentDataType.STRING, "COMMON");
//            item.setItemMeta(meta);
//        }
//
//        rare_value = data.get(key, PersistentDataType.STRING);
//
//        return RARITY_NAMES.getOrDefault(rare_value.toUpperCase(), "Неизвестная редкость");
//    }
//
//
//
//
//
//
//    public String getId() {
//        return id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public String getRarity() {
//        return rarity;
//    }
//
//    public List<String> getLore() {
//        return lore;
//    }
//
//    public abstract void applyEffect(Player player); // Эффект при использовании
//}
