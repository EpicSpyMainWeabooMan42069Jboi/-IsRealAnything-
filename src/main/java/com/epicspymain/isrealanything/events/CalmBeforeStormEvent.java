package com.epicspymain.isrealanything.events;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

/**
 * EVENT 28: CalmBeforeStorm - Skyblock isolation
 * Teleports player to 10x10 dirt platform high in sky
 * Sign reads: "Just. Look. At. Me."
 * Creates sense of isolation and vulnerability
 */
public class CalmBeforeStormEvent {
    
    private static final int PLATFORM_SIZE = 10;
    private static final int SKY_HEIGHT = 200; // High above world
    
    /**
     * Trigger skyblock teleport event
     */
    public static void trigger(ServerWorld world, ServerPlayerEntity player) {
        // Find safe coordinates far from player
        BlockPos playerPos = player.getBlockPos();
        BlockPos skyPos = new BlockPos(
            playerPos.getX() + 1000,
            SKY_HEIGHT,
            playerPos.getZ() + 1000
        );
        
        // Build platform
        buildSkyPlatform(world, skyPos);
        
        // Teleport player to center
        player.teleport(
            skyPos.getX() + PLATFORM_SIZE / 2.0,
            skyPos.getY() + 1,
            skyPos.getZ() + PLATFORM_SIZE / 2.0
        );
        
        // Send message
        player.sendMessage(
            Text.literal("Where... am I?")
                .formatted(Formatting.GRAY, Formatting.ITALIC),
            false
        );
        
        // Wait then send follow-up
        world.getServer().execute(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            player.sendMessage(
                Text.literal("There's nothing here but... a sign?")
                    .formatted(Formatting.DARK_GRAY),
                false
            );
        });
    }
    
    /**
     * Build the skyblock platform with sign
     */
    private static void buildSkyPlatform(ServerWorld world, BlockPos center) {
        // Build 10x10 dirt platform
        for (int x = 0; x < PLATFORM_SIZE; x++) {
            for (int z = 0; z < PLATFORM_SIZE; z++) {
                BlockPos pos = center.add(x, 0, z);
                world.setBlockState(pos, Blocks.DIRT.getDefaultState());
            }
        }
        
        // Place sign in center
        BlockPos signPos = center.add(PLATFORM_SIZE / 2, 1, PLATFORM_SIZE / 2);
        world.setBlockState(signPos, Blocks.OAK_SIGN.getDefaultState());
        
        // Write message on sign
        if (world.getBlockEntity(signPos) instanceof SignBlockEntity sign) {
            SignText signText = new SignText()
                .withMessage(0, Text.literal("Just."))
                .withMessage(1, Text.literal("Look."))
                .withMessage(2, Text.literal("At."))
                .withMessage(3, Text.literal("Me."));
            
            sign.setText(signText, true);
            sign.markDirty();
        }
        
        // Add a few decorative blocks
        world.setBlockState(center.add(0, 1, 0), Blocks.DEAD_BUSH.getDefaultState());
        world.setBlockState(center.add(PLATFORM_SIZE - 1, 1, 0), Blocks.DEAD_BUSH.getDefaultState());
        world.setBlockState(center.add(0, 1, PLATFORM_SIZE - 1), Blocks.DEAD_BUSH.getDefaultState());
        world.setBlockState(center.add(PLATFORM_SIZE - 1, 1, PLATFORM_SIZE - 1), Blocks.DEAD_BUSH.getDefaultState());
    }
}
