package com.epicspymain.isrealanything.events;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

/**
 * EVENT 21: MyVoiceSigns - Spawns signs with entity messages
 * Signs appear randomly in 50-60 block radius
 * Behind player or scattered in world
 * Messages express entity's obsession and presence
 */
public class MyVoiceSignsEvent {
    
    private static final int MIN_RADIUS = 50;
    private static final int MAX_RADIUS = 60;
    private static final int SIGNS_PER_TRIGGER = 3; // 3-5 signs
    
    // Sign messages
    private static final String[] SIGN_MESSAGES = {
        "You Are My\nSunshine",
        "You Know\nWho I am",
        "Isn't This\nWhat You\nWanted?",
        "I love you",
        "Notice ME,\nN O W",
        "( ͠° ͟ʖ ͡° )",
        "Where are\nyou",
        "All of this\nis just\nnormality\nfor you?",
        "Don't Say I\nDidn't Warn\nYou"
    };
    
    /**
     * Trigger sign spawning event
     */
    public static void trigger(ServerWorld world, ServerPlayerEntity player) {
        int signCount = SIGNS_PER_TRIGGER + world.random.nextInt(3); // 3-5 signs
        
        for (int i = 0; i < signCount; i++) {
            spawnSign(world, player);
        }
    }
    
    /**
     * Spawn a single sign with message
     */
    private static void spawnSign(ServerWorld world, ServerPlayerEntity player) {
        BlockPos signPos = findSignLocation(world, player);
        
        if (signPos == null) {
            return;
        }
        
        // Place sign
        world.setBlockState(signPos, Blocks.OAK_SIGN.getDefaultState());
        
        // Get sign block entity and write message
        if (world.getBlockEntity(signPos) instanceof SignBlockEntity sign) {
            String message = SIGN_MESSAGES[world.random.nextInt(SIGN_MESSAGES.length)];
            String[] lines = message.split("\n");
            
            // Create sign text
            SignText signText = new SignText();
            for (int i = 0; i < Math.min(lines.length, 4); i++) {
                signText = signText.withMessage(i, Text.literal(lines[i]));
            }
            
            sign.setText(signText, true);
            sign.markDirty();
        }
    }
    
    /**
     * Find location for sign placement
     */
    private static BlockPos findSignLocation(ServerWorld world, ServerPlayerEntity player) {
        BlockPos playerPos = player.getBlockPos();
        
        for (int attempt = 0; attempt < 30; attempt++) {
            // Random angle and distance
            double angle = world.random.nextDouble() * Math.PI * 2;
            int distance = MIN_RADIUS + world.random.nextInt(MAX_RADIUS - MIN_RADIUS);
            
            int x = playerPos.getX() + (int)(Math.cos(angle) * distance);
            int z = playerPos.getZ() + (int)(Math.sin(angle) * distance);
            
            // Find ground level
            BlockPos testPos = new BlockPos(x, playerPos.getY(), z);
            testPos = world.getTopPosition(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, testPos);
            
            // Check if location is suitable
            if (world.getBlockState(testPos).isAir() &&
                world.getBlockState(testPos.down()).isSolidBlock(world, testPos.down())) {
                return testPos;
            }
        }
        
        return null;
    }
}
