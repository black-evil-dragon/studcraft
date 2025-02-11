package studcraft.darkness.world;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import studcraft.darkness.Darkness;
import studcraft.darkness.items.utils.ItemUtils;
import studcraft.darkness.rating.ItemRating;

import java.util.Comparator;
import java.util.Random;


public class MonsterDifficultyManager implements Listener {

    private static final int SAFE_ZONE_RADIUS = SafeZoneManager.SAFE_ZONE_RADIUS; // Радиус безопасной зоны
    private static final double AVOIDANCE_RADIUS = SAFE_ZONE_RADIUS * 1.25;
    private static final double MAX_DISTANCE_FROM_SAFE_ZONE = SAFE_ZONE_RADIUS * 1.5;

    private final Random random = new Random();

    @EventHandler
    public void onMonsterSpawn(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();

        // Проверяем, является ли существо монстром
        if (entity instanceof Monster) {
            Monster monster = (Monster) entity;

            // Определяем ближайшего игрока
            Player nearestPlayer = getNearestPlayer(monster);
            if (nearestPlayer == null) return;

            // Проверяем, находится ли игрок в безопасной зоне
            boolean isInSafeZone = isPlayerInSafeZone(nearestPlayer);

            // Изменяем сложность на основе местоположения игрока
            adjustMonsterDifficulty(monster, nearestPlayer, isInSafeZone);
            startAvoidingSafeZone(monster);
        }
    }

    /**
     * Проверяет, находится ли игрок в безопасной зоне.
     */
    public static boolean isPlayerInSafeZone(Player player) {
        Location spawnLocation = player.getWorld().getSpawnLocation();
        double distance = player.getLocation().distance(spawnLocation);
        return distance <= SAFE_ZONE_RADIUS;
    }

    /**
     * Изменяет сложность монстра в зависимости от местоположения игрока.
     */
    public static void adjustMonsterDifficulty(Monster monster, Player player, boolean isInSafeZone) {
        double baseHealth = monster.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        ItemRating itemRating = new ItemRating();

        // Получаем рейтинг игрока
        int playerRating = itemRating.getPlayerAverageRating(player)[2];

        PersistentDataContainer data = monster.getPersistentDataContainer();
        NamespacedKey mob_difficult = new NamespacedKey(Darkness.getInstance(), "mob_difficult");

        // Если игрок в безопасной зоне, сложность минимальная
        if (isInSafeZone) {
            monster.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(baseHealth);
            monster.setHealth(baseHealth);

            data.remove(mob_difficult);

        } else {
            // Вычисляем множитель здоровья и сложность на основе расстояния
            double distance = player.getLocation().distance(player.getWorld().getSpawnLocation());
            double difficultyMultiplier = calculateDistanceMultiplier(distance);
            double diminutiveMultiplier = diminutiveMultiplierByRating(playerRating);

            difficultyMultiplier = Math.max(difficultyMultiplier - diminutiveMultiplier, 0.9);

            double newHealth = baseHealth * difficultyMultiplier;

            // Применяем изменения
            monster.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(newHealth);
            monster.setHealth(newHealth);
            monster.customName(Component.text((float) difficultyMultiplier));

            data.set(mob_difficult, PersistentDataType.DOUBLE, difficultyMultiplier);
        }
    }

    /**
     * Вычисляет множитель сложности на основе расстояния от точки спавна.
     */
    private static double calculateDistanceMultiplier(double distance) {
        if (distance <= SAFE_ZONE_RADIUS*2.25) {
            return 1.2;
        } else if (distance <= SAFE_ZONE_RADIUS*2.5) {
            return 1.5;
        } else if (distance <= SAFE_ZONE_RADIUS*5) {
            return 2.0;
        } else {
            return 3.0;
        }
    }
    /**
     * Вычисляет множитель здоровья на основе рейтинга игрока.
     */
    private static double diminutiveMultiplierByRating(int playerRating) {
        if (playerRating < ItemRating.START_CAP) {
            return 0;

        } else if (playerRating < ItemRating.SOFT_CAP) {
            return 0.1;

        } else if (playerRating < ItemRating.POWER_CAP) {
            return 0.15;

        } else if (playerRating <= ItemRating.HARD_CAP) {
            return 0.2;

        } else if (playerRating <= ItemRating.IMPOSSIBLE_CAP) {
            return 0.55;

        } else {
            return 0.75;
        }


    }


    /**
     * Ищет ближайшего игрока к монстру.
     */
    private Player getNearestPlayer(Monster monster) {
        return monster.getWorld().getPlayers().stream()
                .min(Comparator.comparingDouble(p -> p.getLocation().distance(monster.getLocation())))
                .orElse(null);
    }


    /**
     * Заставляет монстра избегать приближения к безопасной зоне, устанавливая новую цель.
     */
    private void startAvoidingSafeZone(Monster monster) {
        PersistentDataContainer data = monster.getPersistentDataContainer();
        NamespacedKey mob_difficult = new NamespacedKey(Darkness.getInstance(), "mob_difficult");
        new BukkitRunnable() {
            @Override
            public void run() {
            if (!monster.isValid() || monster.isDead() || !data.has(mob_difficult, PersistentDataType.DOUBLE)) {
                cancel();
                return;
            }

            Location spawnLocation = monster.getWorld().getSpawnLocation();
            double distanceToSafeZone = monster.getLocation().distance(spawnLocation);


            if (distanceToSafeZone <= AVOIDANCE_RADIUS) {
                // Находим новую точку для перемещения монстра
                Location newTarget = findNewTargetAwayFromSafeZone(spawnLocation, monster.getLocation());

                // Задаем цель для монстра
                monster.getPathfinder().moveTo(newTarget, 1.5);
                monster.setAggressive(false);
                monster.setTarget(null);
            }
            }
        }.runTaskTimer(Darkness.getInstance(), 0, 40); // Проверка каждые 2 секунды
    }

    private Location findNewTargetAwayFromSafeZone(Location safeZone, Location currentLocation) {
        // Вычисляем направление от центра безопасной зоны к текущему местоположению монстра
        double deltaX = currentLocation.getX() - safeZone.getX();
        double deltaZ = currentLocation.getZ() - safeZone.getZ();
        double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        // Нормализуем вектор направления
        double normalizedX = deltaX / distance;
        double normalizedZ = deltaZ / distance;

        // Увеличиваем расстояние от безопасной зоны
        double newDistance = MAX_DISTANCE_FROM_SAFE_ZONE; //SAFE_ZONE_RADIUS + random.nextDouble() * (MAX_DISTANCE_FROM_SAFE_ZONE - SAFE_ZONE_RADIUS);
        double newX = safeZone.getX() + normalizedX * newDistance;
        double newZ = safeZone.getZ() + normalizedZ * newDistance;

        // Определяем безопасную высоту на новой позиции
        double newY = currentLocation.getWorld().getHighestBlockYAt((int) newX, (int) newZ) + 1;

        return new Location(currentLocation.getWorld(), newX, newY, newZ);
    }
}
