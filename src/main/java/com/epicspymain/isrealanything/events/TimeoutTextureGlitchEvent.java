package com.epicspymain.isrealanything.events;

import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

/**
 * EVENT 26: TimeoutTextureGlitch - Reality corruption
 * Texture becomes corrupted (visual glitch - requires client-side)
 * Deletes 5 blocks down from where player is standing
 * Creates sense of world instability
 */
public class TimeoutTextureGlitchEvent {
    
    private static final int DELETION_DEPTH = 5;
    
    /**
     * Trigger texture glitch and block deletion
     */
    public static void trigger(ServerWorld world, ServerPlayerEntity player) {
        BlockPos playerPos = player.getBlockPos();
        
        // Delete blocks below player
        for (int y = 1; y <= DELETION_DEPTH; y++) {
            BlockPos deletePos = playerPos.down(y);
            
            // Don't delete bedrock or important blocks
            if (world.getBlockState(deletePos).getBlock() != Blocks.BEDROCK &&
                world.getBlockState(deletePos).getBlock() != Blocks.END_PORTAL &&
                world.getBlockState(deletePos).getBlock() != Blocks.END_PORTAL_FRAME) {
                
                world.setBlockState(deletePos, Blocks.AIR.getDefaultState());
            }
        }
        
        // Send cryptic message
        player.sendMessage(
            Text.literal("The ground beneath you feels... unstable.")
                .formatted(Formatting.DARK_GRAY, Formatting.ITALIC),
            true
        );
        
        // Follow-up message
        world.getServer().execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            player.sendMessage(
                Text.literal("Reality is glitching.")
                    .formatted(Formatting.DARK_PURPLE),
                false
            );
        });
    }
}
