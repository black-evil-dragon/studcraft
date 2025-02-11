package studcraft.darkness.items.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import studcraft.darkness.Darkness;


public class ItemUtils {

    public static void setMetadata(ItemStack item, NamespacedKey key, String value) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
//            System.out.println("ItemMeta is null for item: " + item.getType());
            return;
        }
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(key, PersistentDataType.STRING, value);
        item.setItemMeta(meta);
//        System.out.println("setMetadata - Key: " + key + ", Value: " + value);
    }

    public static void setMetadata(ItemStack item, NamespacedKey key, int value) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(key, PersistentDataType.INTEGER, value);
        item.setItemMeta(meta);
    }


    public static String getMetadata(ItemStack item, NamespacedKey key, String defaultValue) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
//            System.out.println("getMetadata - ItemMeta is null");
            return defaultValue;
        }
        PersistentDataContainer container = meta.getPersistentDataContainer();
        String value = container.get(key, PersistentDataType.STRING);
        if (value == null) {
//            System.out.println("getMetadata - Defaulting to: " + defaultValue);
            return defaultValue;
        }
        return value;
    }
    public static int getMetadata(ItemStack item, NamespacedKey key, int defaultValue) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return defaultValue;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.getOrDefault(key, PersistentDataType.INTEGER, defaultValue);
    }


    public static void addAttributeModifier(ItemMeta meta, Attribute attribute, double value, String name, EquipmentSlotGroup slot) {
        NamespacedKey key = new NamespacedKey(Darkness.getInstance(), name);
        AttributeModifier modifier = new AttributeModifier(key, value, AttributeModifier.Operation.ADD_NUMBER, slot);

        meta.addAttributeModifier(attribute, modifier);
    }
}