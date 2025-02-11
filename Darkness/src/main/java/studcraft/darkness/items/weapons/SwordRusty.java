package studcraft.darkness.items.weapons;

import org.bukkit.Material;
import studcraft.darkness.items.CustomItem;

import java.util.List;
import java.util.Map;

public class SwordRusty extends CustomItem {

    public SwordRusty() {
        super(
                "sword_rusty", // Уникальный идентификатор
                "§r§fРжавый клинок§r", // Название предмета
                "UNCOMMON", // Редкость
                "§oВоняет§r", // Лор
                List.of(), // Перки
                Map.of( // Статы
                        "attack_damage", 6.0,
                        "attack_speed", 2.4
                ),
                Material.IRON_SWORD // Материал
        );
    }
}