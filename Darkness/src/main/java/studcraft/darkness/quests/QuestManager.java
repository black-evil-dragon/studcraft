package studcraft.darkness.quests;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import studcraft.darkness.Darkness;

import java.util.*;
import java.util.stream.Collectors;

public class QuestManager {
    private final Map<Player, List<QuestItem>> activeQuests = new HashMap<>();

    public static final String PLAYER_QUEST = "player_quests";

    // Добавление квеста
    public void addQuest(Player player, QuestItem quest) {
        List<QuestItem> quests = activeQuests.computeIfAbsent(player, k -> new ArrayList<>());

        // Проверяем, есть ли уже такой квест у игрока
        if (hasQuest(player, quest.getId())) {
            player.sendMessage("§cУ вас уже есть это задание!");
            return;
        }

        // Добавляем новый квест
        quests.add(quest);

        // Сохраняем квест в PersistentDataContainer
        saveQuestsToPlayerData(player, quests);

        player.sendMessage("§aВы получили задание: " + quest.getName());
    }

    // Удаление квеста
    public void removeQuest(Player player, String questId) {
        List<QuestItem> quests = activeQuests.get(player);
        if (quests == null || quests.isEmpty()) {
            return; // Если квестов нет, ничего не делаем
        }

        // Удаляем квест из списка
        quests.removeIf(quest -> quest.getId().equals(questId));

        // Сохраняем обновленный список квестов
        saveQuestsToPlayerData(player, quests);

        player.sendMessage("§aЗадание завершено и удалено!");
    }

    public void completeQuest(Player player, String questId) {
        List<QuestItem> quests = activeQuests.get(player);
        if (quests == null || quests.isEmpty()) {
            player.sendMessage("§cУ вас нет активных заданий!");
            return;
        }

        QuestItem quest = quests.stream()
                .filter(q -> q.getId().equals(questId))
                .findFirst()
                .orElse(null);

        if (quest == null) {
            player.sendMessage("§cУ вас нет этого задания!");
            return;
        }

        // Выдаем награду
        quest.giveReward(player);

        // Удаляем квест
        removeQuest(player, questId);

        player.sendMessage("§aВы завершили задание и получили награду!");
    }

    // Проверка наличия квеста
    public boolean hasQuest(Player player, String questId) {
        List<QuestItem> quests = activeQuests.get(player);
        if (quests == null || quests.isEmpty()) {
            return false; // Если квестов нет, возвращаем false
        }

        // Проверяем, есть ли нужный квест в списке
        return quests.stream().anyMatch(quest -> quest.getId().equals(questId));
    }

    // Получение списка квестов игрока
    public List<QuestItem> getQuests(Player player) {
        return activeQuests.getOrDefault(player, new ArrayList<>());
    }

    // Сохранение квестов в PersistentDataContainer
    private void saveQuestsToPlayerData(Player player, List<QuestItem> quests) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(Darkness.getInstance(), PLAYER_QUEST);

        // Сохраняем квесты в виде строки: "questId1,questId2,questId3"
        String questsString = quests.stream()
                .map(QuestItem::getId)
                .collect(Collectors.joining(","));

        data.set(key, PersistentDataType.STRING, questsString);
    }

    // Загрузка квестов из PersistentDataContainer
    private List<QuestItem> loadQuestsFromPlayerData(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(Darkness.getInstance(), PLAYER_QUEST);

        String questsString = data.get(key, PersistentDataType.STRING);
        if (questsString == null || questsString.isEmpty()) {
            return new ArrayList<>(); // Если квестов нет, возвращаем пустой список
        }

        // Загружаем квесты из строки
        return Arrays.stream(questsString.split(","))
                .map(this::getQuestById) // Предположим, что у тебя есть метод getQuestById
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // Пример метода для получения квеста по ID
    private QuestItem getQuestById(String questId) {
        // Здесь нужно реализовать логику получения квеста по ID
        // Например, можно хранить все квесты в QuestMerchant или в отдельном классе
        return null; // Заглушка
    }
}