package studcraft.darkness.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import studcraft.darkness.Darkness;

public class ReloadConfig implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("reloadDarkness")) {
            Darkness.getInstance().reloadConfig();
            sender.sendMessage("Конфигурация Darkness перезагружена!");
            return true;
        }
        return false;
    }
}
