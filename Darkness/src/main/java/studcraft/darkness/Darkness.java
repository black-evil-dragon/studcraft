package studcraft.darkness;

import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import studcraft.darkness.commands.DarkSpearCommand;
import studcraft.darkness.items.CraftingRestriction;
import studcraft.darkness.items.ItemListener;
import studcraft.darkness.loot.listener.LootEventHandler;
import studcraft.darkness.perks.impl.weapon.ChainReactionPerk;
import studcraft.darkness.perks.impl.weapon.FireChargePerk;
import studcraft.darkness.quests.QuestManager;
import studcraft.darkness.quests.QuestTrading;
import studcraft.darkness.rating.commands.GetRatingCommand;

import studcraft.darkness.listeners.DarkCurseListener;
import studcraft.darkness.listeners.DarkSpearListener;
import studcraft.darkness.rating.listeners.ItemRatingListener;
import studcraft.darkness.world.EventScheduler;
import studcraft.darkness.world.MonsterDifficultyManager;
import studcraft.darkness.world.SafeZoneManager;

import static studcraft.darkness.commands.DarkSpear.createDarkSpear;


public final class Darkness extends JavaPlugin {
    private static Darkness instance;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        getLogger().info("Плагин Darkness запущен!");


        PluginCommand GiveSpearCommand = this.getCommand("givedarkspear");
        if (GiveSpearCommand != null) {
            GiveSpearCommand.setExecutor(new DarkSpearCommand(createDarkSpear()));

            getLogger().info("Команды успешно зарегистрированы!");
        } else {
            getLogger().severe("");
        }

        PluginCommand getRatingCommand = this.getCommand("getrating");
        if (getRatingCommand != null) {
            getRatingCommand.setExecutor(new GetRatingCommand());
            getLogger().info("Команда getrating успешно зарегистрирована!");
        } else {
            getLogger().severe("Команда 'getrating' не зарегистрирована!");
        }



        getServer().getPluginManager().registerEvents(new DarkSpearListener(), this);
        getServer().getPluginManager().registerEvents(new DarkCurseListener(), this);

        getServer().getPluginManager().registerEvents(new MonsterDifficultyManager(), this);
        getServer().getPluginManager().registerEvents(new SafeZoneManager(), this);

        getServer().getPluginManager().registerEvents(new ItemRatingListener(), this);
        getServer().getPluginManager().registerEvents(new ItemListener(), this);

        getServer().getPluginManager().registerEvents(new LootEventHandler(), this);

        getServer().getPluginManager().registerEvents(new ChainReactionPerk(), this);
        getServer().getPluginManager().registerEvents(new FireChargePerk(), this);


        NamespacedKey restricted_key = new NamespacedKey(this, "restricted_item");
        getServer().getPluginManager().registerEvents(new CraftingRestriction(restricted_key), this);

        QuestManager questManager = new QuestManager();
        getServer().getPluginManager().registerEvents(new QuestTrading(questManager), this);


        EventScheduler.startEventScheduler();
        QuestTrading.spawnVillagers();

    }

    public static Darkness getInstance() {
        return instance;
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic

        getLogger().info("Darkness плагин деактивирован.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("reloadDarkness")) {
            this.reloadConfig(); // Перезагрузка конфигурации
            sender.sendMessage("Конфигурация Darkness перезагружена!");
            getLogger().info("Конфигурация плагина Darkness успешно перезагружена.");
            return true;
        }
        return false;
    }

    public int getBaseRating() {
        return getConfig().getInt("rating.base", 50); // 10 - значение по умолчанию
    }
}
