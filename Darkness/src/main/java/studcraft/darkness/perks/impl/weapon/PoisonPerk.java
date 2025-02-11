package studcraft.darkness.perks.impl.weapon;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import studcraft.darkness.perks.Perk;

public class PoisonPerk implements Perk {

    @Override
    public void apply(Player user, LivingEntity target) {
        target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 5)); // Отравление на 5 секунд
    }

    @Override
    public String getId() {
        return "poison_effect";
    }

    @Override
    public String getName() {
        return "§2Отравленный клинок§r";
    }

    @Override
    public String getDescription() {
        return "§2§oОтправляет врагов§r";
    }

    @Override
    public double getActivationChance() {
        return 100.0; // Шанс активации (в процентах)
    }
}