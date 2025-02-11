package studcraft.darkness.world;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SafeZoneManager implements Listener {

    public static final int SAFE_ZONE_RADIUS = 20; // Радиус безопасной зоны (сфера)
    private final Set<Player> outsideSafeZone = new HashSet<>();
    private final Map<Player, Long> lastWarningTime = new HashMap<>();// Игроки за границей безопасной зоны
    private static final long WARNING_COOLDOWN = 5000;

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location spawnLocation = player.getWorld().getSpawnLocation();
        double distance = player.getLocation().distance(spawnLocation);

        // Проверяем, покинул ли игрок безопасную зону
        boolean isOutsideSafeZone = distance > SAFE_ZONE_RADIUS;

        if (isOutsideSafeZone && !outsideSafeZone.contains(player)) {
            // Игрок пересёк границу безопасной зоны (вышел за пределы)
            outsideSafeZone.add(player);
            onExitSafeZone(player);
        } else if (!isOutsideSafeZone && outsideSafeZone.contains(player)) {
            // Игрок вернулся в безопасную зону
            outsideSafeZone.remove(player);
            onEnterSafeZone(player);
        } else if (!isOutsideSafeZone) {
            // Игрок приближается к границе безопасной зоны
            double distanceToBorder = SAFE_ZONE_RADIUS - distance;
            if (distanceToBorder <= distanceToBorder * 0.25) {
                onNearSafeZoneBorder(player);
            }
        }
    }

    /**
     * Эффекты при выходе из безопасной зоны.
     */
    private void onExitSafeZone(Player player) {
        player.sendMessage("§cВы покинули безопасную зону! Мир становится опаснее...");
        World world = player.getWorld();
        Location location = player.getLocation();

        // Звуковые эффекты
        world.playSound(location, Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.5f);

        // Визуальные эффекты
        world.spawnParticle(Particle.LARGE_SMOKE, location, 20, 1, 1, 1, 0.05);
    }

    /**
     * Эффекты при возвращении в безопасную зону.
     */
    private void onEnterSafeZone(Player player) {
        player.sendMessage("§aВы вернулись в безопасную зону. Здесь вы в безопасности!");
        World world = player.getWorld();
        Location location = player.getLocation();

        // Звуковые эффекты
        world.playSound(location, Sound.BLOCK_NOTE_BLOCK_HARP, 1.0f, 1.5f);

        // Визуальные эффекты
        world.spawnParticle(Particle.HEART, location, 10, 0.5, 0.5, 0.5, 0.05);
    }

    /**
     * Эффекты, когда игрок приближается к границе безопасной зоны.
     */
    private void onNearSafeZoneBorder(Player player) {
        long currentTime = System.currentTimeMillis();
        long lastWarning = lastWarningTime.getOrDefault(player, 0L);

        if (currentTime - lastWarning >= WARNING_COOLDOWN) {
            // Выводим предупреждение
            player.sendMessage("§eВы близки к границе безопасной зоны...");

            World world = player.getWorld();
            Location location = player.getLocation();

            // Визуальные эффекты
            world.spawnParticle(Particle.END_ROD, location, 5, 0.2, 0.2, 0.2, 0.02);

            // Звуковые эффекты (тихий сигнал тревоги)
            world.playSound(location, Sound.BLOCK_BELL_USE, 0.5f, 1.2f);

            // Обновляем время последнего предупреждения
            lastWarningTime.put(player, currentTime);
        }
    }
}
