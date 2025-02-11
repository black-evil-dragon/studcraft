package studcraft.darkness.quests;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import studcraft.darkness.Darkness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestMerchant {

    private final String id;
    private final String name;
    private final Villager.Profession profession;
    private final int level;

    public final Map<String, QuestItem> questPool = new HashMap<>();

    /**
     * Конструктор для создания жителя-квестодателя с заданным списком квестов.
     *
     * @param id         Уникальный ID жителя.
     * @param name       Имя жителя.
     * @param profession Профессия жителя.
     * @param level      Уровень жителя.
     * @param quests     Список квестов, которые будет предлагать житель.
     */
    public QuestMerchant(String id, String name, Villager.Profession profession, int level, List<QuestItem> quests) {
        this.id = id;
        this.name = name;
        this.profession = profession;
        this.level = level;

        // Добавляем квесты в пул
        for (QuestItem quest : quests) {
            questPool.put(quest.getId(), quest);
        }
    }


    public void spawn() {
        Bukkit.getWorld("dev").getSpawnLocation().getWorld().spawn(
                Bukkit.getWorld("dev").getSpawnLocation(),
                Villager.class,
                villager -> {
                    villager.setCustomName(name);
                    villager.setCustomNameVisible(true);
                    villager.setAI(false);
                    villager.setProfession(profession);
                    villager.setVillagerLevel(level);
                    villager.setRecipes(getRecipes());

                    // Добавление уникального ID жителя
                    NamespacedKey villagerKey = new NamespacedKey(Darkness.getInstance(), "villager_id");
                    PersistentDataContainer data = villager.getPersistentDataContainer();
                    data.set(villagerKey, PersistentDataType.STRING, id);
                }
        );
    }

    /**
     * Создает торговые рецепты на основе квестов.
     */
    public List<MerchantRecipe> getRecipes() {
        List<MerchantRecipe> recipes = new ArrayList<>();

        for (QuestItem quest : questPool.values()) {
            // Создаем предмет для квеста
            ItemStack questItem = new ItemStack(Material.BOOK);
            ItemMeta questMeta = questItem.getItemMeta();
            if (questMeta != null) {
                questMeta.setDisplayName(quest.getName());
                questMeta.setLore(List.of(quest.getDescription()));
                questItem.setItemMeta(questMeta);
            }

            // Устанавливаем ID квеста в PersistentDataContainer
            NamespacedKey questKey = new NamespacedKey(Darkness.getInstance(), "quest_id");
            questItem.editMeta(meta -> meta.getPersistentDataContainer().set(questKey, PersistentDataType.STRING, quest.getId()));

            // Создаем рецепт для торговли
            MerchantRecipe recipe = new MerchantRecipe(questItem, 99999); // Квест можно купить бесконечно
            recipe.addIngredient(new ItemStack(Material.EMERALD, 5)); // Цена: 5 изумрудов
            recipes.add(recipe);
        }

        return recipes;
    }
}
