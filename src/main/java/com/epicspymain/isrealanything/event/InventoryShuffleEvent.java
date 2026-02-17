package com.epicspymain.isrealanything/events;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;

/**
 * EVENT 15: InventoryShuffle - Randomly swaps inventory positions
 * Triggers 3-8 seconds after inventory closes
 * Swaps between hotbar ↔ main inventory
 * No deletion or duplication
 * 
 * Note: Uses delayed execution, actual mixin integration handled separately
 */
public class InventoryShuffleEvent {
    
    private static final Map<UUID, Long> pendingShuffles = new HashMap<>();
    
    /**
     * Schedule inventory shuffle after player closes inventory
     */
    public static void scheduleInventoryShuffle(ServerPlayerEntity player) {
        // Random delay: 3-8 seconds (60-160 ticks)
        int delayTicks = 60 + player.getWorld().random.nextInt(100);
        long scheduledTime = player.getWorld().getTime() + delayTicks;
        
        pendingShuffles.put(player.getUuid(), scheduledTime);
    }
    
    /**
     * Check and execute pending shuffles (call from server tick)
     */
    public static void tick(ServerPlayerEntity player) {
        Long scheduledTime = pendingShuffles.get(player.getUuid());
        
        if (scheduledTime != null && player.getWorld().getTime() >= scheduledTime) {
            shuffleInventory(player);
            pendingShuffles.remove(player.getUuid());
        }
    }
    
    /**
     * Shuffle player's inventory
     */
    private static void shuffleInventory(ServerPlayerEntity player) {
        // Don't shuffle if inventory is open
        if (player.currentScreenHandler != player.playerScreenHandler) {
            return;
        }
        
        Random random = player.getWorld().random;
        
        // Get all non-empty slots
        List<Integer> nonEmptySlots = new ArrayList<>();
        for (int i = 0; i < 36; i++) { // 0-8 hotbar, 9-35 main inventory
            if (!player.getInventory().getStack(i).isEmpty()) {
                nonEmptySlots.add(i);
            }
        }
        
        if (nonEmptySlots.size() < 2) {
            return; // Need at least 2 items to shuffle
        }
        
        // Perform 3-6 random swaps
        int swapCount = 3 + random.nextInt(4);
        
        for (int i = 0; i < swapCount; i++) {
            // Pick two random slots
            int slot1 = nonEmptySlots.get(random.nextInt(nonEmptySlots.size()));
            int slot2 = nonEmptySlots.get(random.nextInt(nonEmptySlots.size()));
            
            if (slot1 != slot2) {
                // Swap items
                ItemStack stack1 = player.getInventory().getStack(slot1).copy();
                ItemStack stack2 = player.getInventory().getStack(slot2).copy();
                
                player.getInventory().setStack(slot1, stack2);
                player.getInventory().setStack(slot2, stack1);
            }
        }
        
        // Send subtle message
        player.sendMessage(
            Text.literal("Something feels... different")
                .formatted(Formatting.DARK_GRAY, Formatting.ITALIC),
            true // Action bar
        );
    }
    
    /**
     * Cancel pending shuffle (if player logs out, etc.)
     */
    public static void cancelShuffle(UUID playerUuid) {
        pendingShuffles.remove(playerUuid);
    }
}
