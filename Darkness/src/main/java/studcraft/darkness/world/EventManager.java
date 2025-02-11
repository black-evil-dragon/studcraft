package studcraft.darkness.world;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import studcraft.darkness.Darkness;

import java.util.Random;

public class EventManager {
    private static final Random random = new Random();

    public static void checkForRandomEvents(Player player) {
        // Периодически проверяем, случится ли ивент
        if (random.nextDouble() < 0) { // 5% шанс
            spawnZombieHorde(player);
        }
    }

    public static void spawnZombieHorde(Player player) {
        World world = player.getWorld();
        Location playerLocation = player.getLocation();

        // Генерируем случайную точку в радиусе 20 блоков от игрока
        Random random = new Random();
        double radius = 20.0;
        double angle = random.nextDouble() * Math.PI * 2; // случайный угол
        double distance = random.nextDouble() * radius; // случайное расстояние

        // Получаем случайные x, z координаты в радиусе
        double x = playerLocation.getX() + distance * Math.cos(angle);
        double z = playerLocation.getZ() + distance * Math.sin(angle);
        double y = world.getHighestBlockYAt((int) x, (int) z) + 2; // Получаем максимальную высоту на этих координатах

        Location spawnLocation = new Location(world, x, y, z);

        // Проверка на свободное место (например, проверяем, что на месте нет других сущностей или блоков)
        if (!isLocationFree(spawnLocation)) {
            player.sendMessage(ChatColor.RED + "Не удалось найти свободное место для орды!");
            return;
        }

        // Добавляем эффект молнии
        world.strikeLightningEffect(spawnLocation);

        // Создаём орду зомби
        for (int i = 0; i < 10; i++) {
            Zombie zombie = world.spawn(spawnLocation, Zombie.class);
            zombie.customName(Component.text("Миньон"));
            zombie.getEquipment().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET)); // Зомби со шлемом
            zombie.setInvulnerable(false);
            MonsterDifficultyManager.adjustMonsterDifficulty(zombie, player, MonsterDifficultyManager.isPlayerInSafeZone(player));
        }

        Zombie bossZombie = world.spawn(spawnLocation, Zombie.class);

        PersistentDataContainer zombie_data = bossZombie.getPersistentDataContainer();
        NamespacedKey ns_key = new NamespacedKey(Darkness.getInstance(), "entity_type");
        zombie_data.set(ns_key, PersistentDataType.STRING, "driven_zombie");

        bossZombie.customName(Component.text("Бунтарь"));


        bossZombie.setCustomNameVisible(true);

        bossZombie.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        bossZombie.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));

        // Модифицируем его атрибуты, например, увеличиваем урон
        bossZombie.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(20.0); // Увеличиваем урон

        bossZombie.getWorld().spawnParticle(Particle.WITCH, bossZombie.getLocation(), 1000, 3, 3, 3);
        MonsterDifficultyManager.adjustMonsterDifficulty(bossZombie, player, MonsterDifficultyManager.isPlayerInSafeZone(player));

        player.sendMessage(ChatColor.RED + "Вокруг тебя появилась орда зомби! Уничтожь их!");
    }

    private static boolean isLocationFree(Location location) {
        // Проверка, что место свободно (например, что в блоке не стоит другой объект)
        return location.getBlock().getType() == Material.AIR || location.getBlock().getType() == Material.GRASS_BLOCK;
    }
}