package studcraft.darkness.perks.impl.weapon;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import studcraft.darkness.perks.Perk;
import studcraft.darkness.perks.PerkManager;

import java.util.HashMap;
import java.util.Map;

public class ThunderPerk implements Perk {

    // Карта для хранения времени последнего использования способности
    private final Map<Player, Long> cooldowns = new HashMap<>();

    // Время перезарядки в миллисекундах (например, 5 секунд)
    private static final long COOLDOWN_TIME = 5000;
    private static final double DAMAGE = 10.0; // Урон от молнии
    private static final int FIRE_TICKS = 100;

    @Override
    public void apply(Player player, LivingEntity target) {
        if (
                player.getFallDistance() > 0.0f // Падает
                && !player.isOnGround()     // Не на земле
                && !player.isSneaking()     // Не приседает
                && !player.isSwimming()     // Не плывёт
                && !player.isClimbing()     // Не взбирается
        ) {
            // Текущее время
            long currentTime = System.currentTimeMillis();

            // Проверяем, есть ли перезарядка для игрока
            if (cooldowns.containsKey(player)) {
                long lastUsed = cooldowns.get(player);
                if ((currentTime - lastUsed) < COOLDOWN_TIME) {
                    return;
                }
            }

            // Удар молнии
            if (target != null) {
                Location targetLocation = target.getLocation();
                player.getWorld().strikeLightningEffect(targetLocation);
                target.damage(DAMAGE);
                target.setFireTicks(FIRE_TICKS);
            }

            // Устанавливаем время последнего использования
            cooldowns.put(player, currentTime);

            PerkManager.startCooldownText(player, COOLDOWN_TIME);
        }
    }



    @Override
    public String getId() {
        return "thunder_effect";
    }

    @Override
    public String getName() {
        return "§bУдар молнии§r";
    }

    @Override
    public String getDescription() {
        return "§b§oБьет врагов молнией§r";
    }

    @Override
    public double getActivationChance() {
        return 100.0; // Шанс активации (в процентах)
    }
}