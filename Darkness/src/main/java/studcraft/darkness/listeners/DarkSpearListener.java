package studcraft.darkness.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import studcraft.darkness.commands.DarkSpear;


public class DarkSpearListener implements Listener {

    @EventHandler
    public void onPlayerUseDarkSpear(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null) return; // Если игрок не держит предмет, выходим

        // Проверяем, является ли предмет Тёмным копьём
        DarkSpear darkSpear = new DarkSpear();
        if (darkSpear.isDarkSpear(item)) {
            player.sendMessage("§c§8Ты использовал Тёмное Копьё!");
            // Логика для копья, например, эффект или действие
        }
    }
}
