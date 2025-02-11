package studcraft.darkness.items.weapons;

import org.bukkit.Material;
import studcraft.darkness.items.CustomItem;

import java.util.List;
import java.util.Map;

public class SwordWaif extends CustomItem {

    public SwordWaif() {
        super(
                "sword_waif", // Уникальный идентификатор
                "§r§fБродяга§r", // Название предмета
                "UNCOMMON", // Редкость
                "§oВерни на место§r", // Лор
                List.of("thunder_effect", "chain_reaction"), // Перки
                Map.of( // Статы
                        "attack_damage", 8.0,
                        "attack_speed", -3.0
                ),
                Material.IRON_SWORD // Материал
        );
    }
}