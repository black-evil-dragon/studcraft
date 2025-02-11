package studcraft.darkness.perks;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import studcraft.darkness.Darkness;

public interface Perk {
    /**
     * Применяет перк на цель.
     *
     * @param user   Игрок, использующий перк.
     * @param target Цель, на которую применяется перк.
     */
    void apply(Player user, LivingEntity target);

    /**
     * Возвращает ID перка.
     *
     * @return ID перка.
     */
    String getId();

    /**
     * Возвращает имя перка.
     *
     * @return Имя перка.
     */
    String getName();


    /**
     * Возвращает имя перка.
     *
     * @return Имя перка.
     */
    String getDescription();

    /**
     * Возвращает шанс срабатывания перка.
     *
     * @return Шанс в процентах (0-100).
     */
    double getActivationChance();
}
