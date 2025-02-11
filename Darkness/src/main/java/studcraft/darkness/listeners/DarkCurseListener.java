package studcraft.darkness.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public class DarkCurseListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Проверяем, что игрок - Terminator
        if (player.getName().equalsIgnoreCase("_Term1nat0r_")) {
            // Отправляем сообщение игроку
            player.sendMessage("§c§8Ты чувствуешь движение тьмы вокруг... Ты проклят.");
        }
    }
}