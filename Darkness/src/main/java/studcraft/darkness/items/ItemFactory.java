package studcraft.darkness.items;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import studcraft.darkness.Darkness;
import studcraft.darkness.items.utils.ItemUtils;
import studcraft.darkness.perks.Perk;
import studcraft.darkness.perks.PerkManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static studcraft.darkness.perks.PerkManager.getItemPerks;
import static studcraft.darkness.rating.ItemRating.BASE_RATING;
import static studcraft.darkness.rating.ItemRating.RATING_KEY;

public class ItemFactory {
    private static final Map<String, String> RARITY_NAMES = Map.of(
        "COMMON", "§7Обычный§r",
        "UNCOMMON", "§2Необычный§r",
        "RARE", "§bРедкий§r",
        "EPIC", "§6Эпический§r",
        "LEGENDARY", "§dЛегендарный§r"
    );


    public static final NamespacedKey CUSTOM_ITEM_KEY = new NamespacedKey(Darkness.getInstance(), "is_custom_item");

    public static final String ITEM_RARE = "item_rare";
    public static final String ITEM_LORE = "item_lore";

    public static ItemStack createItem(CustomItem item) {
        ItemStack stack = new ItemStack(item.getMaterial());
        ItemMeta meta = stack.getItemMeta();

        if (meta != null) {
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(CUSTOM_ITEM_KEY, PersistentDataType.BYTE, (byte) 1);

            meta.displayName(Component.text(item.getName()));
            data.set(new NamespacedKey(Darkness.getInstance(), ITEM_RARE), PersistentDataType.STRING, item.getRarity());
            data.set(new NamespacedKey(Darkness.getInstance(), ITEM_LORE), PersistentDataType.STRING, item.getLore());

            item.getStats().forEach((key, value) -> {
                if (key.equalsIgnoreCase("attack_damage")) {
                    ItemUtils.addAttributeModifier(
                            meta,
                            Attribute.GENERIC_ATTACK_DAMAGE,
                            value,
                            "custom_damage",
                            EquipmentSlotGroup.MAINHAND
                    );
                } else if (key.equalsIgnoreCase("attack_speed")) {
                    ItemUtils.addAttributeModifier(
                            meta,
                            Attribute.GENERIC_ATTACK_SPEED,
                            value,
                            "custom_speed",
                            EquipmentSlotGroup.MAINHAND
                    );
                }
            });

            stack.setItemMeta(meta);

            PerkManager.addPerksToItem(stack, item.getPerks());
        }

        return stack;
    }



    public static void updateLore(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;


        int item_rating = ItemUtils.getMetadata(
                item,
                new NamespacedKey(Darkness.getInstance(), RATING_KEY),
                BASE_RATING
        );
        String item_rare = ItemUtils.getMetadata(
            item,
            new NamespacedKey(Darkness.getInstance(), ITEM_RARE),
            "COMMON"
        );
        String item_lore = ItemUtils.getMetadata(
                item,
                new NamespacedKey(Darkness.getInstance(), ITEM_LORE),
                ""
        );

        item_rare = RARITY_NAMES.getOrDefault(item_rare.toUpperCase(), "Неизвестная редкость");
        List<Perk> perks = getItemPerks(item);


        List<Component> lore = new ArrayList<>();
        if(!Objects.equals(item_lore, "")) lore.add(Component.text(item_lore));
        lore.add(
            Component.text(
            item_rare + "§7 | " + item_rating + " очков силы§r"
            )
        );

        if (!perks.isEmpty()) {
            lore.add(Component.text(""));
            lore.add(Component.text("§7Перки:§r"));
            for (Perk perk : perks) {
                lore.add(Component.text(" " + perk.getName() + "§r"));
            }
        }

        meta.lore(lore);
        item.setItemMeta(meta);
    }
}