package studcraft.darkness.commands;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.kyori.adventure.text.Component;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import studcraft.darkness.Darkness;

import java.util.ArrayList;
import java.util.List;

public class DarkSpear {

    public static ItemStack createDarkSpear() {
        ItemStack spear = new ItemStack(Material.TRIDENT); // Базовый предмет — трезубец
        ItemMeta meta = spear.getItemMeta();

        NamespacedKey key = new NamespacedKey(Darkness.getInstance(), "dark_spear");

        if (meta != null) {
            meta.setCustomModelData(1001);

            // Устанавливаем кастомное имя с текстовыми компонентами
            meta.displayName(Component.text("§6Тёмное Копьё"));

            // Добавляем описание (лор) предмета
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("§8Копьё, созданное в тени"));
            lore.add(Component.text(""));
            lore.add(Component.text("§5§oВы чувствуете пульсацию"));
            lore.add(Component.text("§5§oв вашей руке"));
            meta.lore(lore);

            meta.setCustomModelData(1);
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(key, PersistentDataType.STRING, "dark_spear");

            spear.setItemMeta(meta); // Применяем изменения
        }

        return spear;
    }

    // Проверка предмета по слагу
    public boolean isDarkSpear(ItemStack item) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();

            // Проверяем наличие слага
            NamespacedKey key = new NamespacedKey(Darkness.getInstance(), "dark_spear");
            if (container.has(key, PersistentDataType.STRING)) {
                String slug = container.get(key, PersistentDataType.STRING);
                return "dark_spear".equals(slug);
            }
        }
        return false;
    }
}