package studcraft.darkness.world;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import studcraft.darkness.Darkness;

public class EventScheduler {

    public static void startEventScheduler() {
        // Получаем экземпляр плагина (например, Darkness)
        Bukkit.getScheduler().runTaskTimer(Darkness.getInstance(), () -> {
            // Для каждого игрока в игре проверяем, нужно ли активировать случайный ивент
            for (Player player : Bukkit.getOnlinePlayers()) {
                EventManager.checkForRandomEvents(player);
            }
        }, 0L, 200L); // Задержка перед первым запуском - 0L, интервал между запусками - 200 тиков (10 секунд)
    }
}
