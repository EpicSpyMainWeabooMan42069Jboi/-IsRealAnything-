package com.epicspymain.isrealanything.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Utility class for building complex NBT structures using Fabric/Yarn mappings.
 * All methods are static and use safe defaults when parameters are null.
 */
public class NbtBuilder {
    
    /**
     * Creates an ItemStack with custom display name.
     * 
     * @param item The base item
     * @param displayName The custom display name
     * @return ItemStack with custom name
     */
    public static ItemStack createNamedItem(ItemStack item, String displayName) {
        if (item == null) {
            item = new ItemStack(Items.STONE);
        }
        if (displayName == null) {
            displayName = "Custom Item";
        }
        
        NbtCompound nbt = item.getOrCreateNbt();
        NbtCompound display = nbt.getCompound("display");
        display.putString("Name", Text.Serializer.toJson(Text.literal(displayName)));
        nbt.put("display", display);
        item.setNbt(nbt);
        
        return item;
    }
    
    /**
     * Creates an ItemStack with custom display name and lore.
     * 
     * @param item The base item
     * @param displayName The custom display name
     * @param loreLines List of lore lines
     * @return ItemStack with name and lore
     */
    public static ItemStack createItemWithLore(ItemStack item, String displayName, List<String> loreLines) {
        if (item == null) {
            item = new ItemStack(Items.STONE);
        }
        if (displayName == null) {
            displayName = "Custom Item";
        }
        if (loreLines == null || loreLines.isEmpty()) {
            return createNamedItem(item, displayName);
        }
        
        NbtCompound nbt = item.getOrCreateNbt();
        NbtCompound display = nbt.getCompound("display");
        
        // Set name
        display.putString("Name", Text.Serializer.toJson(Text.literal(displayName)));
        
        // Set lore
        NbtList lore = new NbtList();
        for (String line : loreLines) {
            lore.add(NbtString.of(Text.Serializer.toJson(Text.literal(line))));
        }
        display.put("Lore", lore);
        
        nbt.put("display", display);
        item.setNbt(nbt);
        
        return item;
    }
    
    /**
     * Creates an ItemStack with custom enchantments.
     * 
     * @param item The base item
     * @param enchantments Map of enchantment IDs to levels
     * @return ItemStack with enchantments
     */
    public static ItemStack createEnchantedItem(ItemStack item, Map<String, Integer> enchantments) {
        if (item == null) {
            item = new ItemStack(Items.DIAMOND_SWORD);
        }
        if (enchantments == null || enchantments.isEmpty()) {
            return item;
        }
        
        NbtCompound nbt = item.getOrCreateNbt();
        NbtList enchantmentsList = new NbtList();
        
        for (Map.Entry<String, Integer> entry : enchantments.entrySet()) {
            NbtCompound enchantNbt = new NbtCompound();
            enchantNbt.putString("id", entry.getKey());
            enchantNbt.putInt("lvl", entry.getValue());
            enchantmentsList.add(enchantNbt);
        }
        
        nbt.put("Enchantments", enchantmentsList);
        item.setNbt(nbt);
        
        return item;
    }
    
    /**
     * Creates an ItemStack with custom model data.
     * 
     * @param item The base item
     * @param customModelData The custom model data value
     * @return ItemStack with custom model data
     */
    public static ItemStack createItemWithCustomModel(ItemStack item, int customModelData) {
        if (item == null) {
            item = new ItemStack(Items.STICK);
        }
        
        NbtCompound nbt = item.getOrCreateNbt();
        nbt.putInt("CustomModelData", customModelData);
        item.setNbt(nbt);
        
        return item;
    }
    
    /**
     * Creates a complete custom item with all features.
     * 
     * @param item The base item
     * @param displayName Custom display name
     * @param loreLines List of lore lines
     * @param enchantments Map of enchantment IDs to levels
     * @param customModelData Custom model data (0 to disable)
     * @return Fully customized ItemStack
     */
    public static ItemStack createCompleteItem(ItemStack item, String displayName, 
                                               List<String> loreLines, 
                                               Map<String, Integer> enchantments,
                                               int customModelData) {
        if (item == null) {
            item = new ItemStack(Items.DIAMOND_SWORD);
        }
        
        // Apply name and lore
        item = createItemWithLore(item, displayName, loreLines);
        
        // Apply enchantments
        if (enchantments != null && !enchantments.isEmpty()) {
            NbtCompound nbt = item.getOrCreateNbt();
            NbtList enchantmentsList = new NbtList();
            
            for (Map.Entry<String, Integer> entry : enchantments.entrySet()) {
                NbtCompound enchantNbt = new NbtCompound();
                enchantNbt.putString("id", entry.getKey());
                enchantNbt.putInt("lvl", entry.getValue());
                enchantmentsList.add(enchantNbt);
            }
            
            nbt.put("Enchantments", enchantmentsList);
            item.setNbt(nbt);
        }
        
        // Apply custom model data
        if (customModelData > 0) {
            NbtCompound nbt = item.getOrCreateNbt();
            nbt.putInt("CustomModelData", customModelData);
            item.setNbt(nbt);
        }
        
        return item;
    }
    
    /**
     * Creates a player head with custom skin texture.
     * 
     * @param textureUrl The texture URL (base64 encoded or raw URL)
     * @param playerName Optional player name for the head
     * @return Player head ItemStack with custom texture
     */
    public static ItemStack createPlayerHead(String textureUrl, String playerName) {
        if (textureUrl == null || textureUrl.isEmpty()) {
            textureUrl = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvIn19fQ==";
        }
        if (playerName == null) {
            playerName = "Custom Head";
        }
        
        ItemStack head = new ItemStack(Items.PLAYER_HEAD);
        NbtCompound nbt = head.getOrCreateNbt();
        
        // Create SkullOwner compound
        NbtCompound skullOwner = new NbtCompound();
        skullOwner.putString("Name", playerName);
        skullOwner.putString("Id", UUID.randomUUID().toString());
        
        // Create Properties compound
        NbtCompound properties = new NbtCompound();
        NbtList textures = new NbtList();
        
        NbtCompound textureData = new NbtCompound();
        textureData.putString("Value", textureUrl);
        textures.add(textureData);
        
        properties.put("textures", textures);
        skullOwner.put("Properties", properties);
        
        nbt.put("SkullOwner", skullOwner);
        head.setNbt(nbt);
        
        return head;
    }
    
    /**
     * Creates a shulker box pre-filled with items.
     * 
     * @param color The shulker box color (null for default purple)
     * @param items List of ItemStacks to place inside
     * @param customName Optional custom name for the shulker box
     * @return Shulker box ItemStack with items
     */
    public static ItemStack createFilledShulkerBox(String color, List<ItemStack> items, String customName) {
        ItemStack shulkerBox;
        
        if (color == null || color.isEmpty()) {
            shulkerBox = new ItemStack(Items.SHULKER_BOX);
        } else {
            Identifier shulkerId = Identifier.of("minecraft", color + "_shulker_box");
            shulkerBox = new ItemStack(Registries.ITEM.get(shulkerId));
        }
        
        NbtCompound nbt = shulkerBox.getOrCreateNbt();
        
        // Set custom name if provided
        if (customName != null && !customName.isEmpty()) {
            NbtCompound display = new NbtCompound();
            display.putString("Name", Text.Serializer.toJson(Text.literal(customName)));
            nbt.put("display", display);
        }
        
        // Create BlockEntityTag
        NbtCompound blockEntityTag = new NbtCompound();
        NbtList itemsList = new NbtList();
        
        if (items != null && !items.isEmpty()) {
            for (int i = 0; i < Math.min(items.size(), 27); i++) {
                ItemStack item = items.get(i);
                if (item != null && !item.isEmpty()) {
                    NbtCompound itemNbt = new NbtCompound();
                    itemNbt.putByte("Slot", (byte) i);
                    item.writeNbt(itemNbt);
                    itemsList.add(itemNbt);
                }
            }
        }
        
        blockEntityTag.put("Items", itemsList);
        nbt.put("BlockEntityTag", blockEntityTag);
        shulkerBox.setNbt(nbt);
        
        return shulkerBox;
    }
    
    /**
     * Creates NBT for a custom zombie entity.
     * 
     * @param customName The zombie's display name
     * @param health Health value (default 20.0)
     * @param speed Speed multiplier (default 1.0)
     * @param damage Attack damage (default 3.0)
     * @param noAI Whether the zombie has AI disabled
     * @param silent Whether the zombie is silent
     * @param persistent Whether the zombie won't despawn
     * @param equipment Map of equipment slot to ItemStack
     * @return NbtCompound for the custom zombie
     */
    public static NbtCompound createCustomZombieNbt(String customName, 
                                                     Double health, 
                                                     Double speed, 
                                                     Double damage,
                                                     boolean noAI, 
                                                     boolean silent, 
                                                     boolean persistent,
                                                     Map<String, ItemStack> equipment) {
        // Safe defaults
        if (customName == null) {
            customName = "Custom Zombie";
        }
        if (health == null) {
            health = 20.0;
        }
        if (speed == null) {
            speed = 1.0;
        }
        if (damage == null) {
            damage = 3.0;
        }
        
        NbtCompound zombieNbt = new NbtCompound();
        
        // Basic properties
        zombieNbt.putString("id", "minecraft:zombie");
        zombieNbt.putString("CustomName", Text.Serializer.toJson(Text.literal(customName)));
        zombieNbt.putBoolean("CustomNameVisible", true);
        
        // AI flags
        zombieNbt.putBoolean("NoAI", noAI);
        zombieNbt.putBoolean("Silent", silent);
        zombieNbt.putBoolean("PersistenceRequired", persistent);
        
        // Attributes
        NbtList attributes = new NbtList();
        
        // Health attribute
        NbtCompound healthAttr = new NbtCompound();
        healthAttr.putString("Name", "minecraft:generic.max_health");
        healthAttr.putDouble("Base", health);
        attributes.add(healthAttr);
        
        // Speed attribute
        NbtCompound speedAttr = new NbtCompound();
        speedAttr.putString("Name", "minecraft:generic.movement_speed");
        speedAttr.putDouble("Base", 0.23 * speed);
        attributes.add(speedAttr);
        
        // Attack damage attribute
        NbtCompound damageAttr = new NbtCompound();
        damageAttr.putString("Name", "minecraft:generic.attack_damage");
        damageAttr.putDouble("Base", damage);
        attributes.add(damageAttr);
        
        zombieNbt.put("Attributes", attributes);
        
        // Set health
        zombieNbt.putFloat("Health", health.floatValue());
        
        // Equipment
        if (equipment != null && !equipment.isEmpty()) {
            NbtList armorItems = new NbtList();
            NbtList handItems = new NbtList();
            
            // Armor slots: feet, legs, chest, head
            String[] armorSlots = {"feet", "legs", "chest", "head"};
            for (String slot : armorSlots) {
                ItemStack item = equipment.get(slot);
                if (item != null && !item.isEmpty()) {
                    NbtCompound itemNbt = new NbtCompound();
                    item.writeNbt(itemNbt);
                    armorItems.add(itemNbt);
                } else {
                    armorItems.add(new NbtCompound());
                }
            }
            
            // Hand slots: mainhand, offhand
            String[] handSlots = {"mainhand", "offhand"};
            for (String slot : handSlots) {
                ItemStack item = equipment.get(slot);
                if (item != null && !item.isEmpty()) {
                    NbtCompound itemNbt = new NbtCompound();
                    item.writeNbt(itemNbt);
                    handItems.add(itemNbt);
                } else {
                    handItems.add(new NbtCompound());
                }
            }
            
            zombieNbt.put("ArmorItems", armorItems);
            zombieNbt.put("HandItems", handItems);
            
            // Equipment drop chances (0.0 = never drops, 1.0 = always drops)
            NbtList armorDropChances = new NbtList();
            NbtList handDropChances = new NbtList();
            
            for (int i = 0; i < 4; i++) {
                armorDropChances.add(NbtString.of("0.085f"));
            }
            for (int i = 0; i < 2; i++) {
                handDropChances.add(NbtString.of("0.085f"));
            }
            
            zombieNbt.put("ArmorDropChances", armorDropChances);
            zombieNbt.put("HandDropChances", handDropChances);
        }
        
        return zombieNbt;
    }
    
    /**
     * Creates NBT for a custom entity with flexible parameters.
     * 
     * @param entityType The entity type ID (e.g., "minecraft:zombie")
     * @param customName The entity's display name
     * @param attributes Map of attribute names to values
     * @param flags Map of boolean flags (NoAI, Silent, etc.)
     * @return NbtCompound for the custom entity
     */
    public static NbtCompound createCustomEntityNbt(String entityType,
                                                     String customName,
                                                     Map<String, Double> attributes,
                                                     Map<String, Boolean> flags) {
        if (entityType == null || entityType.isEmpty()) {
            entityType = "minecraft:zombie";
        }
        if (customName == null) {
            customName = "Custom Entity";
        }
        
        NbtCompound entityNbt = new NbtCompound();
        
        // Basic properties
        entityNbt.putString("id", entityType);
        entityNbt.putString("CustomName", Text.Serializer.toJson(Text.literal(customName)));
        entityNbt.putBoolean("CustomNameVisible", true);
        
        // Apply flags
        if (flags != null) {
            for (Map.Entry<String, Boolean> flag : flags.entrySet()) {
                entityNbt.putBoolean(flag.getKey(), flag.getValue());
            }
        }
        
        // Apply attributes
        if (attributes != null && !attributes.isEmpty()) {
            NbtList attributesList = new NbtList();
            
            for (Map.Entry<String, Double> attr : attributes.entrySet()) {
                NbtCompound attrNbt = new NbtCompound();
                attrNbt.putString("Name", attr.getKey());
                attrNbt.putDouble("Base", attr.getValue());
                attributesList.add(attrNbt);
            }
            
            entityNbt.put("Attributes", attributesList);
        }
        
        return entityNbt;
    }
    
    /**
     * Helper method to create a base64 encoded texture value for player heads.
     * 
     * @param textureUrl The texture URL
     * @return Base64 encoded texture string
     */
    public static String encodeTextureUrl(String textureUrl) {
        if (textureUrl == null || textureUrl.isEmpty()) {
            return "";
        }
        
        String json = String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", textureUrl);
        return Base64.getEncoder().encodeToString(json.getBytes());
    }
}
