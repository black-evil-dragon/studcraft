package studcraft.darkness.perks.impl.weapon;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import studcraft.darkness.Darkness;
import studcraft.darkness.perks.Perk;
import studcraft.darkness.perks.PerkManager;

import java.util.List;

public class ChainReactionPerk implements Perk, Listener {

    @Override
    public void apply(Player user, LivingEntity target) {

    }

    @Override
    public String getId() {
        return "chain_reaction";
    }

    @Override
    public String getName() {
        return "§bЦепная реакция§r";
    }

    @Override
    public String getDescription() {
        return "§b§oПосле убийства ударяет молнией врагов в радиусе§r";
    }

    @Override
    public double getActivationChance() {
        return 100.0; // Шанс активации (в процентах)
    }

    private static final double RADIUS = 10.0;
    private static final int MAX_ENTITIES = 5;
    private static final double DAMAGE = 10.0; // Урон от молнии
    private static final int FIRE_TICKS = 100; // Количество тиков поджога (5 секунд)

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity deadEntity = event.getEntity();
        Player killer = deadEntity.getKiller();

        if (killer == null) return;

        // Проверяем, есть ли у игрока оба перка
        boolean hasThunderPerk = hasPerk(killer, "thunder_effect");
        boolean hasChainReactionPerk = hasPerk(killer, "chain_reaction");

        if (hasThunderPerk && hasChainReactionPerk) {
            killer.sendMessage("§o§bЦепная реакция§r");
            // Ищем ближайших существ в радиусе
            Location deathLocation = deadEntity.getLocation();
            List<Entity> nearbyEntities = deathLocation.getWorld().getNearbyEntities(deathLocation, RADIUS, RADIUS, RADIUS)
                    .stream()
                    .filter(entity -> entity instanceof LivingEntity) // Только живые сущности
                    .filter(entity -> !(entity instanceof Player))    // Исключаем игроков
                    .filter(entity -> entity != deadEntity)          // Исключаем убитое существо
                    .toList();

            // Вызываем молнии для каждого найденного существа
            int currentCount = 0;
            for (Entity entity : nearbyEntities) {
                if (entity instanceof LivingEntity target && currentCount < MAX_ENTITIES) {
                    currentCount++;
                    Bukkit.getScheduler().runTaskLater(Darkness.getInstance(), () -> {
                        // Визуальный эффект молнии
                        Location targetLocation = target.getLocation();
                        target.getWorld().strikeLightningEffect(targetLocation);

                        // Нанесение урона и поджог
                        target.damage(DAMAGE);
                        target.setFireTicks(FIRE_TICKS);

                        target.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, target.getLocation(), 150, RADIUS/2, RADIUS/2, RADIUS/2, 0.1);
                    }, 10L); // Небольшая задержка для визуального эффекта
                } else if (currentCount == MAX_ENTITIES) {
                    break;
                }
            }
        }
    }

    private boolean hasPerk(Player player, String perkID) {
        return PerkManager.hasPerk(player, perkID);
    }
}