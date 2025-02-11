package studcraft.darkness.perks.impl.weapon;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import studcraft.darkness.Darkness;
import studcraft.darkness.perks.Perk;

import java.util.List;

import static studcraft.darkness.perks.PerkManager.hasPerk;


public class FireChargePerk implements Perk, Listener {

    private static final float RADIUS = 10.0F;
//    private static final int MAX_ENTITIES = 5;
    private static final double DAMAGE = 10.0;
    private static final int MAX_CHARGES = 5;

    // Ключ для хранения количества зарядов для каждого игрока
    NamespacedKey key = new NamespacedKey(Darkness.getInstance(), getId() + "_charges");



    @Override
    public void apply(Player player, LivingEntity target) {

    }
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity deadEntity = event.getEntity();
        Player killer = deadEntity.getKiller();

        if (killer == null) return;

        // Проверяем, есть ли у игрока оба перка
        boolean hasPerk = hasPerk(killer, getId());

        if (hasPerk) {
            // Увеличиваем количество зарядов
            PersistentDataContainer data = killer.getPersistentDataContainer();
            int charges = data.getOrDefault(key, PersistentDataType.INTEGER, 0) + 1;

            if (charges > MAX_CHARGES) return;

            data.set(
                    key,
                    PersistentDataType.INTEGER,
                    charges
            );
            killer.sendMessage("§6Вы получили заряд! Зарядов: " + charges);
        }
    }


    /**
     * Использование огненного заряда.
     * @param player Игрок, использующий заряд.
     */
    public void useFireCharge(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        int charges = data.getOrDefault(key, PersistentDataType.INTEGER, 0);

        if (charges > 0) {
            // Выпускаем огненный шар
            Location location = player.getEyeLocation();
            Fireball fireball = player.getWorld().spawn(location.add(location.getDirection()), Fireball.class);
            fireball.setIsIncendiary(false); // Не поджигает землю
            fireball.setYield(0); // Радиус взрыва равен нулю
            fireball.setShooter(player);

            PersistentDataContainer fireball_data = fireball.getPersistentDataContainer();
            fireball_data.set(
                    key,
                    PersistentDataType.INTEGER,
                    1
            );

            charges--;

            data.set(
                    key,
                    PersistentDataType.INTEGER,
                    Math.min(charges, MAX_CHARGES)
            );
            player.sendMessage("§cВы использовали заряд! Осталось зарядов: " + charges);
        } else {
            player.sendMessage("§cУ вас нет зарядов для использования!");
        }
    }
    @EventHandler
    public void onFireballExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Fireball) {
            PersistentDataContainer data = entity.getPersistentDataContainer();
            if (data.getOrDefault(key, PersistentDataType.INTEGER, 0) == 0) return;

            Fireball fireball = (Fireball) entity;

            // Получение сущностей рядом с фаерболлом
            List<Entity> nearbyEntities = fireball.getNearbyEntities(RADIUS, RADIUS, RADIUS)
                    .stream()
                    .filter(e -> e instanceof LivingEntity) // Только живые сущности
                    .toList();

            for (Entity nearbyEntity : nearbyEntities) {
                if (nearbyEntity instanceof LivingEntity) {
                    double distance = fireball.getLocation().distance(nearbyEntity.getLocation());

                    double adjustedDamage = DAMAGE - (distance / RADIUS) * DAMAGE;
                    adjustedDamage = Math.max(adjustedDamage, 0);

                    ((LivingEntity) nearbyEntity).damage(adjustedDamage);
                }
            }

            fireball.getWorld().spawnParticle(org.bukkit.Particle.LAVA, fireball.getLocation(), 100, RADIUS/2, RADIUS/2, RADIUS/2, 0.1);
            fireball.getWorld().spawnParticle(org.bukkit.Particle.EXPLOSION, fireball.getLocation(), 1); // Большой взрыв
        }
    }

    /**
     * Проверка, был ли нанесён критический урон.
     * @param player Игрок, атакующий цель.
     * @return True, если урон был критическим.
     */
    private boolean isCriticalHit(Player player) {
        return player.getFallDistance() > 0.0f
                && !player.isOnGround()
                && !player.isSneaking()
                && !player.isSwimming()
                && !player.isClimbing();
    }

    /**
     * Отслеживание нажатия правой кнопки мыши для использования огненного заряда.
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        // Проверяем, что в руке игрока есть предмет
        if (itemInHand == null || itemInHand.getType().isAir() || !hasPerk(player, getId())) return;

        // Проверяем, что взаимодействие выполнено правой кнопкой мыши и основной рукой
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!event.getAction().name().contains("RIGHT_CLICK")) return;

        // Проверяем, что у игрока есть заряды
        PersistentDataContainer data = player.getPersistentDataContainer();
        int charges = data.getOrDefault(key, PersistentDataType.INTEGER, 0);

        if (charges > 0) {
            // Используем заряд
            useFireCharge(player);
            event.setCancelled(true); // Отменяем стандартное взаимодействие
        }
    }

    @Override
    public String getId() {
        return "fire_charge";
    }

    @Override
    public String getName() {
        return "§cОгненный заряд§r";
    }

    @Override
    public String getDescription() {
        return "§c§oПолучайте заряды при критических убийствах и используйте их для выстрела огненными шарами.§r";
    }

    @Override
    public double getActivationChance() {
        return 100.0; // Шанс активации (в процентах)
    }
}
