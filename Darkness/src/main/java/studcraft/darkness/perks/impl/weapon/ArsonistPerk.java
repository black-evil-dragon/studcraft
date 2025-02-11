package studcraft.darkness.perks.impl.weapon;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import studcraft.darkness.perks.Perk;

public class ArsonistPerk implements Perk {

    @Override
    public void apply(Player user, LivingEntity target) {
        target.setFireTicks(60);
    }

    @Override
    public String getId() {
        return "arsonist_blade";
    }

    @Override
    public String getName() {
        return "§cГорячий клинок§r";
    }

    @Override
    public String getDescription() {
        return "§c§oПоджигает врагов§r";
    }

    @Override
    public double getActivationChance() {
        return 100.0; // Шанс активации (в процентах)
    }
}