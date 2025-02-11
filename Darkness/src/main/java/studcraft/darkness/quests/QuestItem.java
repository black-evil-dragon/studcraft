package studcraft.darkness.quests;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;


public class QuestItem {
    private final String name;
    private final String description;
    private final int targetCount;
    private final EntityType targetType;
    private final String id;
    private final ItemStack reward;
    private final int minRewardAmount;
    private final int maxRewardAmount;
    private final int rewardAmount;
    private int progress;

    public QuestItem(String id, String name, String description, int targetCount, EntityType targetType, ItemStack reward, int rewardAmount) {
        this(id, name, description, targetCount, targetType, reward, rewardAmount, rewardAmount);
    }

    public QuestItem(String id, String name, String description, int targetCount, EntityType targetType, ItemStack reward, int minRewardAmount, int maxRewardAmount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.targetCount = targetCount;
        this.targetType = targetType;
        this.reward = reward;
        this.minRewardAmount = minRewardAmount;
        this.maxRewardAmount = maxRewardAmount;
        this.rewardAmount = -1;
        this.progress = 0;
    }
    // what the fuck


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getTargetCount() {
        return targetCount;
    }

    public EntityType getTargetType() {
        return targetType;
    }

    public int getProgress() {
        return progress;
    }

    public void incrementProgress() {
        progress++;
    }

    public boolean isComplete() {
        return progress >= targetCount;
    }

    /**
     * Выдает награду игроку.
     *
     * @param player Игрок, которому выдается награда.
     */
    public void giveReward(Player player) {
        if (reward == null) {
            return; // Если награда не задана, ничего не делаем
        }

        // Определяем количество награды
        int amount = rewardAmount;
        if (amount == -1) {
            // Если задан диапазон, выбираем случайное количество
            Random random = new Random();
            amount = random.nextInt(maxRewardAmount - minRewardAmount + 1) + minRewardAmount;
        }

        // Создаем копию награды с нужным количеством
        ItemStack rewardCopy = reward.clone();
        rewardCopy.setAmount(amount);

        // Выдаем награду игроку
        player.getInventory().addItem(rewardCopy);
        player.sendMessage("§aВы получили награду: " + rewardCopy.getAmount() + "x " + rewardCopy.getType().toString());
    }
}
