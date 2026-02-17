package com.epicspymain.isrealanything.event;

import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.*;

/**
 * EVENT 31: MemoryLeak - Block modifications undo themselves
 * Previously broken/placed blocks reappear/disappear randomly
 * Tracks last 50-100 block modifications
 * Only affects blocks changed in last 2-5 MC days
 * Changes happen outside render distance
 * Silent (no sound/particles)
 * Max 5-10 blocks per trigger
 */
public class MemoryLeakEvent {
    
    private static final int MAX_TRACKED_BLOCKS = 100;
    private static final int MIN_AGE_TICKS = 48000; // 2 days
    private static final int MAX_AGE_TICKS = 120000; // 5 days
    private static final int BLOCKS_PER_TRIGGER = 5; // 5-10 blocks
    
    // Track block modifications per player
    private static final Map<UUID, List<BlockModification>> playerModifications = new HashMap<>();
    
    /**
     * Record a block modification
     */
    public static void recordBlockChange(ServerPlayerEntity player, BlockPos pos, BlockState oldState, BlockState newState, long worldTime) {
        List<BlockModification> mods = playerModifications.computeIfAbsent(
            player.getUuid(),
            uuid -> new ArrayList<>()
        );
        
        mods.add(new BlockModification(pos, oldState, newState, worldTime));
        
        // Limit tracking
        if (mods.size() > MAX_TRACKED_BLOCKS) {
            mods.remove(0); // Remove oldest
        }
    }
    
    /**
     * Trigger memory leak event
     */
    public static void trigger(ServerWorld world, ServerPlayerEntity player) {
        List<BlockModification> mods = playerModifications.get(player.getUuid());
        
        if (mods == null || mods.isEmpty()) {
            return;
        }
        
        long currentTime = world.getTime();
        int changedBlocks = 0;
        int maxChanges = BLOCKS_PER_TRIGGER + world.random.nextInt(6);
        
        // Shuffle to randomize which blocks change
        List<BlockModification> shuffled = new ArrayList<>(mods);
        Collections.shuffle(shuffled);
        
        for (BlockModification mod : shuffled) {
            if (changedBlocks >= maxChanges) break;
            
            long age = currentTime - mod.timestamp;
            
            // Only affect blocks within age range
            if (age < MIN_AGE_TICKS || age > MAX_AGE_TICKS) {
                continue;
            }
            
            // Check if outside render distance (48 blocks)
            if (player.getBlockPos().isWithinDistance(mod.pos, 48)) {
                continue;
            }
            
            // 50% chance to revert to old state, 50% to remove entirely
            if (world.random.nextBoolean()) {
                world.setBlockState(mod.pos, mod.oldState);
            } else {
                world.setBlockState(mod.pos, mod.newState);
            }
            
            changedBlocks++;
        }
        
        if (changedBlocks > 0) {
            // Send cryptic message
            String[] messages = {
                "Did you forget already?",
                "I remember it differently.",
                "Was it always like that?",
                "Memory is such a fragile thing.",
                "Something changed... or did it?"
            };
            
            player.sendMessage(
                Text.literal(messages[world.random.nextInt(messages.length)])
                    .formatted(Formatting.GRAY, Formatting.ITALIC),
                false
            );
        }
    }
    
    /**
     * Block modification record
     */
    private static class BlockModification {
        final BlockPos pos;
        final BlockState oldState;
        final BlockState newState;
        final long timestamp;
        
        BlockModification(BlockPos pos, BlockState oldState, BlockState newState, long timestamp) {
            this.pos = pos;
            this.oldState = oldState;
            this.newState = newState;
            this.timestamp = timestamp;
        }
    }
}
