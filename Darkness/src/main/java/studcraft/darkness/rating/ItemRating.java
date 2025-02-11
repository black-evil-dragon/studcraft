package studcraft.darkness.rating;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import studcraft.darkness.Darkness;
import studcraft.darkness.items.utils.ItemUtils;

import java.util.HashMap;
import java.util.Map;

import static studcraft.darkness.items.ItemFactory.updateLore;

public class ItemRating {

    private static final FileConfiguration CONFIG = Darkness.getInstance().getConfig();

    public static final int BASE_RATING = CONFIG.getInt("rating.base", 50);
    private static final int MAX_RATING = CONFIG.getInt("rating.max", 600);

    public static final int START_CAP = CONFIG.getInt("rating.caps.start.cap", 100);
    public static final int SOFT_CAP = CONFIG.getInt("rating.caps.soft.cap", 200);
    public static final int POWER_CAP = CONFIG.getInt("rating.caps.power.cap", 400);
    public static final int HARD_CAP = CONFIG.getInt("rating.caps.hard.cap", 500);
    public static final int IMPOSSIBLE_CAP = CONFIG.getInt("rating.caps.hard.cap", 550);

    private static final int START_BONUS = CONFIG.getInt("rating.caps.soft.bonus", 25);
    private static final int SOFT_BONUS = CONFIG.getInt("rating.caps.soft.bonus", 20);
    private static final int POWER_BONUS = CONFIG.getInt("rating.caps.power.bonus", 10);
    private static final int HARD_BONUS = CONFIG.getInt("rating.caps.hard.bonus", 5);
    private static final int IMPOSSIBLE_BONUS = CONFIG.getInt("rating.caps.impossible.bonus", 1);

    public static final String RATING_KEY = CONFIG.getString("rating.key", "item_rating");
    public static final String RATING_MULTIPLIER = "item_rating_bonus_multiplier";


    public void assignRatingToItem(ItemStack item, Player player) {
        if (!isWeaponOrArmor(item)) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer data = meta.getPersistentDataContainer();
        NamespacedKey rating_key = new NamespacedKey(Darkness.getInstance(), RATING_KEY);


        if (!data.has(rating_key, PersistentDataType.INTEGER)) {
            NamespacedKey rating_multiplier_key = new NamespacedKey(Darkness.getInstance(), RATING_MULTIPLIER);

            // Генерация рейтинга с учетом среднего рейтинга игрока
            int playerRating = getPlayerAverageRating(player)[2];
            double ratingMultiplier = data.getOrDefault(rating_multiplier_key, PersistentDataType.DOUBLE, 0.0);

            playerRating += (int) (calculateBonus(playerRating) * ratingMultiplier);

            ItemUtils.setMetadata(
                item,
                new NamespacedKey(Darkness.getInstance(), RATING_KEY),
                generateRating(playerRating)
            );

            updateLore(item);
        }
    }

    /**
     * Генерация рейтинга предмета на основе редкости.
     *
     * @param rarity Уровень редкости предмета: COMMON, UNCOMMON, RARE, EPIC, LEGENDARY.
     * @param playerRating Средний рейтинг игрока.
     * @return Рейтинг предмета.
     */
    public int generateRatingByRarity(String rarity, int playerRating) {
        // Диапазоны значений рейтинга для каждой редкости
        Map<String, int[]> rarityRanges = Map.of(
        "COMMON", new int[]{-10, 10},
        "UNCOMMON", new int[]{0, 20},
        "RARE", new int[]{10, 30},
        "EPIC", new int[]{20, 50},
        "LEGENDARY", new int[]{40, 100}
        );

        // Получаем диапазон для редкости
        int[] range = rarityRanges.getOrDefault(rarity.toUpperCase(), new int[]{0, 20});

        // Перерасчитываем рейтинг учитывая кап игрока
        // и бонус от капа
        playerRating = generateRating(playerRating);

        // Минимальный и максимальный рейтинг для предмета с учетом рейтинга игрока
        int minRating = Math.max(BASE_RATING, playerRating + range[0]);
        int maxRating = Math.min(MAX_RATING, playerRating + range[1]);

        // Генерируем случайное значение внутри диапазона
        return minRating + (int) (Math.random() * (maxRating - minRating + 1));
    }


    /**
     * Генерация рейтинга предмета с учетом среднего рейтинга игрока.
     *
     * @param playerRating Средний рейтинг игрока.
     * @param additionalRating Рейтинг понижения
     * @return Рейтинг предмета.
     */
    private int _generateRating(int playerRating, int additionalRating) {
        int bonus = calculateBonus(playerRating);
        int minRating = Math.max(BASE_RATING, playerRating + additionalRating);
        int maxRating = Math.min(MAX_RATING, playerRating + additionalRating + bonus); // Максимум - выше текущего с учетом бонуса

        return minRating + (int) (Math.random() * (maxRating - minRating + 1));
    }
    public int generateRating(int playerRating, int additionalRating) {  return _generateRating(playerRating, additionalRating); }
    public int generateRating(int playerRating) {                       return _generateRating(playerRating, 0); }



    /**
     * Расчет бонуса к рейтингу на основе текущего среднего рейтинга игрока.
     *
     * @param playerRating Средний рейтинг игрока.
     * @return Бонус к рейтингу.
     */
    private int calculateBonus(int playerRating) {
        if (playerRating < START_CAP) {
            return START_BONUS;
        } else if (playerRating < SOFT_CAP) {
            return SOFT_BONUS;
        } else if (playerRating < POWER_CAP) {
            return POWER_BONUS;
        } else if (playerRating <= HARD_CAP) {
            return HARD_BONUS;
        } else if (playerRating <= IMPOSSIBLE_CAP) {
            return IMPOSSIBLE_BONUS;
        }
        return 0;
    }


    /**
     * Вычисляет средний рейтинг игрока.
     *
     * @param player Игрок, для которого вычисляется рейтинг.
     * @return 0 - Armor, 1 - Weapon, 2 - Average.
     */
    public int[] getPlayerAverageRating(Player player) {
        int totalArmorRating = calculateArmorRating(player);
        int totalWeaponRating = calculateWeaponRating(player);

        return new int[]{
            totalArmorRating, totalWeaponRating, (totalArmorRating + totalWeaponRating) / 2,
        };
    }


    /*
     *   Armor calculations
     */
    public int calculateArmorRating(Player player)  {
        int totalArmorRating = 0;
        int armorSlots = 4; // У игрока 4 слота брони

        // Проверяем каждый слот брони
        for (ItemStack armorItem : player.getEquipment().getArmorContents()) {
            if (armorItem == null || armorItem.getType() == Material.AIR) {
                totalArmorRating += BASE_RATING; // Если слот пустой, добавляем базовый рейтинг
            } else {
                totalArmorRating += getItemRating(armorItem);
            }
        }

        return totalArmorRating / armorSlots;
    }
    // ========================



    /*
     *   Weapon calculations
     */
    public int calculateWeaponRating(Player player) {
        Map<String, Integer> weaponMaxRatings = new HashMap<>(); // Словарь для максимальных рейтингов по типам оружия

        // Проверяем только хотбар
        for (int i = 0; i < 9; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null || item.getType() == Material.AIR) continue;

            if (isWeapon(item)) {
                String weaponType = getWeaponType(item); // Определяем тип оружия
                int rating = getItemRating(item); // Получаем рейтинг для этого предмета

                // Сохраняем максимальный рейтинг для каждого типа оружия
                weaponMaxRatings.put(weaponType, Math.max(weaponMaxRatings.getOrDefault(weaponType, BASE_RATING), rating));
            }
        }

        // Суммируем максимальные рейтинги каждого типа оружия
        int totalWeaponRating = 0;
        for (int maxRating : weaponMaxRatings.values()) {
            totalWeaponRating += maxRating;
        }

        // Возвращаем средний рейтинг оружия (по количеству типов)
        return weaponMaxRatings.isEmpty() ? BASE_RATING : totalWeaponRating / weaponMaxRatings.size();
    }


    private String getWeaponType(ItemStack item) {
        Material type = item.getType();
        if (type.name().endsWith("_SWORD")) return "SWORD";
        if (type.name().endsWith("_AXE")) return "AXE";
        if (type.name().endsWith("_BOW")) return "BOW";
        if (type == Material.SHIELD) return "SHIELD";
        if (type == Material.TRIDENT) return "TRIDENT";

        return "UNKNOWN";
    }

    private static boolean isWeaponOrArmor(ItemStack item) {
        Material type = item.getType();
        return type.name().endsWith("_SWORD")
                || type.name().endsWith("_AXE")
                || type.name().endsWith("_BOW")
                || type.name().endsWith("_HELMET")
                || type.name().endsWith("_CHESTPLATE")
                || type.name().endsWith("_LEGGINGS")
                || type.name().endsWith("_BOOTS")
                || type == Material.TRIDENT;
    }


    private boolean isWeapon(ItemStack item) {
        Material type = item.getType();
        return type.name().endsWith("_SWORD") || type.name().endsWith("_AXE") || type.name().endsWith("_BOW")
                || type == Material.SHIELD || type == Material.TRIDENT;
    }
    // ========================

    /*
     *  Get rating
     */
    private int getItemRating(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return BASE_RATING;

        PersistentDataContainer data = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(Darkness.getInstance(), RATING_KEY);

        return data.getOrDefault(key, PersistentDataType.INTEGER, BASE_RATING);
    }
}
