package studcraft.darkness.quests;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import studcraft.darkness.Darkness;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestTrading implements Listener {

    private final QuestManager questManager;
    private static final Map<String, QuestMerchant> villagers = new HashMap<>();

    public QuestTrading(QuestManager questManager) {
        this.questManager = questManager;

        // Создаем список квестов для жителя №7
        List<QuestItem> villager7Quests = Arrays.asList(
                new QuestItem(
                        "zombie_cleanup",
                        "Чистка: Убить 10 зомби",
                        "Очистите окрестности, уничтожив 10 зомби.",
                        10,
                        EntityType.ZOMBIE,
                        new ItemStack(Material.DIAMOND),
                        1
                ),
                new QuestItem(
                        "skeleton_hunt",
                        "Чистка: Убить 5 скелетов",
                        "Очистите окрестности, уничтожив 5 скелетов.",
                        5,
                        EntityType.SKELETON,
                        new ItemStack(Material.EMERALD),
                        3, 5 // Награда: от 3 до 5 изумрудов
                )
        );

        // Создаем жителя с заданным списком квестов
        QuestMerchant villager7 = new QuestMerchant(
                "villager_7",
                "Житель №7",
                Villager.Profession.LIBRARIAN,
                3,
                villager7Quests
        );

        villagers.put("villager_7", villager7);
    }


    public static void spawnVillagers() {
        villagers.values().forEach(QuestMerchant::spawn);
    }



    /**
     * Обрабатывает взаимодействие игрока с жителем-квестодателем.
     */
//    @EventHandler
//    public void onVillagerInteract(PlayerInteractEntityEvent event) {
//        if (!(event.getRightClicked() instanceof Villager villager)) return;
//
//        PersistentDataContainer data = villager.getPersistentDataContainer();
//        NamespacedKey villagerKey = new NamespacedKey(Darkness.getInstance(), "villager_id");
//
//        if (!data.has(villagerKey, PersistentDataType.STRING)) return;
//
//        String villagerId = data.get(villagerKey, PersistentDataType.STRING);
//        if (villagerId == null || !villagers.containsKey(villagerId)) return;
//
//        // Открытие интерфейса торговли для квестодателя
//        var questVillager = villagers.get(villagerId);
//        event.getPlayer().openMerchant(villager, true);
//    }
    @EventHandler
    public void onVillagerInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Villager villager)) return;

        PersistentDataContainer data = villager.getPersistentDataContainer();
        NamespacedKey villagerKey = new NamespacedKey(Darkness.getInstance(), "villager_id");

        if (!data.has(villagerKey, PersistentDataType.STRING)) return;

        String villagerId = data.get(villagerKey, PersistentDataType.STRING);
        if (villagerId == null || !villagers.containsKey(villagerId)) return;

        // Открытие интерфейса торговли для квестодателя
        event.getPlayer().openMerchant(villager, true);
    }


    /**
     * Обрабатывает выбор квеста через интерфейс торговли.
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getInventory().getHolder() instanceof Merchant)) return;

        Villager merchant = (Villager) event.getInventory().getHolder();
        ItemStack clickedItem = event.getCurrentItem();

        System.out.println("1 -" + merchant);

        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        PersistentDataContainer merchantData = merchant.getPersistentDataContainer();
        NamespacedKey villagerKey = new NamespacedKey(Darkness.getInstance(), "villager_id");

        PersistentDataContainer questData = clickedItem.getItemMeta().getPersistentDataContainer();
        NamespacedKey questKey = new NamespacedKey(Darkness.getInstance(), "quest_id");


        System.out.println("2 -" + clickedItem);
        System.out.println("2.01 -" + merchantData.get(villagerKey, PersistentDataType.STRING));
        System.out.println("2.02 -" + questData.get(questKey, PersistentDataType.STRING));



        if (!merchantData.has(villagerKey, PersistentDataType.STRING) || !questData.has(questKey, PersistentDataType.STRING)) return;

        String villagerId = merchantData.get(villagerKey, PersistentDataType.STRING);
        if (villagerId == null || !villagers.containsKey(villagerId)) return;

        QuestMerchant villager = villagers.get(villagerId);
        String questId = questData.get(questKey, PersistentDataType.STRING);

        System.out.println("2.1 -" + questId);
        System.out.println("2.2 -" + villager);
        System.out.println("2.3 -" + villager.questPool);

        if (!villager.questPool.containsKey(questId)) return;

        System.out.println("3 -" + questId);
        // Добавляем квест игроку
        questManager.addQuest(player, villager.questPool.get(questId));
//        event.setCancelled(true); // Отменяем стандартное поведение
    }



    /**
     * Обрабатывает убийство мобов и обновляет прогресс задания.
     */
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player player)) return;

        List<QuestItem> quests = questManager.getQuests(player);
        System.out.println("123 -" + quests);

        if (quests.isEmpty()) return;

        for (QuestItem quest : quests) {
            System.out.println("321 -" + quests);
            if (event.getEntityType() == quest.getTargetType()) {
                quest.incrementProgress();
                player.sendMessage("§aПрогресс задания '" + quest.getName() + "': " + quest.getProgress() + "/" + quest.getTargetCount());

                if (quest.isComplete()) {
                    questManager.completeQuest(player, quest.getId());
                    player.sendMessage("§aЗадание '" + quest.getName() + "' завершено!");
                }
            }
        }
    }
}