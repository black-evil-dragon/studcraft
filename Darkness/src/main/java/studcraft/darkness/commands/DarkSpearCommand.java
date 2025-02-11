package studcraft.darkness.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import studcraft.darkness.world.EventManager;

public class DarkSpearCommand implements CommandExecutor {

    private final ItemStack darkSpear;

    public DarkSpearCommand(ItemStack darkSpear) {
        this.darkSpear = darkSpear;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            EventManager.spawnZombieHorde(player);
            return true;
        }

        return false;
    }

}