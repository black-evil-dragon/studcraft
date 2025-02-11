package studcraft.darkness.rating.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import studcraft.darkness.Darkness;
import studcraft.darkness.rating.ItemRating;

public class GetRatingCommand implements CommandExecutor {

    private static final FileConfiguration CONFIG = Darkness.getInstance().getConfig();

    private static final int BASE_RATING = CONFIG.getInt("rating.base", 100); // Базовый рейтинг для пустых слотов
    private static final String RATING_KEY = CONFIG.getString("rating.key", "item_rating");

    private final ItemRating itemRating;


    public GetRatingCommand() {
        this.itemRating = new ItemRating();
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Эту команду может использовать только игрок.");
            return true;
        }

        Player player = (Player) sender;

        // Подсчёт рейтинга для брони и оружия
        int[] playerRating = itemRating.getPlayerAverageRating(player);
        int totalArmorRating = playerRating[0];
        int totalWeaponRating = playerRating[1];

        // Подсчёт среднего рейтинга
        int averageRating = playerRating[2];

        player.sendMessage(ChatColor.GREEN + "Ваш средний рейтинг: " + ChatColor.AQUA + averageRating);
        player.sendMessage(ChatColor.GREEN + "- брони: " + ChatColor.AQUA + totalArmorRating);
        player.sendMessage(ChatColor.GREEN + "- оружия: " + ChatColor.AQUA + totalWeaponRating);
        return true;
    }
}
