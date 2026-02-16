package com.epicspymain.isrealanything.events;

import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * EVENT 39: LastChance - Villager home trap structure
 * If player enters, destroys, or gets in perimeter:
 * - Screen freezes (simulated)
 * - Message: "BEGONE THOT"
 * - Game crashes on exit
 * - Desktop txt file (blank title) with warning message
 */
public class LastChanceEvent {
    
    private static final Map<UUID, BlockPos> structureLocations = new HashMap<>();
    private static final int PERIMETER_RADIUS = 15;
    
    /**
     * Spawn last chance structure
     */
    public static void trigger(ServerWorld world, ServerPlayerEntity player) {
        BlockPos playerPos = player.getBlockPos();
        
        // Find location 50-80 blocks away
        double angle = world.random.nextDouble() * Math.PI * 2;
        int distance = 50 + world.random.nextInt(30);
        
        BlockPos structurePos = playerPos.add(
            (int)(Math.cos(angle) * distance),
            0,
            (int)(Math.sin(angle) * distance)
        );
        
        structurePos = world.getTopPosition(
            net.minecraft.world.Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
            structurePos
        );
        
        // Build simple villager-style house
        buildVillagerHouse(world, structurePos);
        
        // Track location
        structureLocations.put(player.getUuid(), structurePos);
        
        player.sendMessage(
            Text.literal("You see a house in the distance...")
                .formatted(Formatting.YELLOW, Formatting.ITALIC),
            false
        );
    }
    
    /**
     * Check if player is near structure
     */
    public static void tick(ServerWorld world, ServerPlayerEntity player) {
        BlockPos structurePos = structureLocations.get(player.getUuid());
        
        if (structurePos == null) {
            return;
        }
        
        // Check if player entered perimeter
        if (player.getBlockPos().isWithinDistance(structurePos, PERIMETER_RADIUS)) {
            triggerPunishment(world, player);
            structureLocations.remove(player.getUuid());
        }
    }
    
    /**
     * Trigger punishment for approaching
     */
    private static void triggerPunishment(ServerWorld world, ServerPlayerEntity player) {
        // Freeze message (simulated)
        for (int i = 0; i < 30; i++) {
            player.sendMessage(
                Text.literal("█").formatted(Formatting.BLACK),
                false
            );
        }
        
        player.sendMessage(
            Text.literal("BEGONE THOT")
                .formatted(Formatting.DARK_RED, Formatting.BOLD),
            false
        );
        
        // Create desktop file
        createWarningFile(player);
        
        // Crash after delay
        world.getServer().execute(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Crash
            world.getServer().stop(false);
        });
    }
    
    /**
     * Build simple villager house
     */
    private static void buildVillagerHouse(ServerWorld world, BlockPos pos) {
        // 7x7 cobblestone base
        for (int x = 0; x < 7; x++) {
            for (int z = 0; z < 7; z++) {
                world.setBlockState(pos.add(x, 0, z), Blocks.COBBLESTONE.getDefaultState());
            }
        }
        
        // Walls (oak planks)
        for (int y = 1; y <= 3; y++) {
            // Front/back
            for (int x = 0; x < 7; x++) {
                world.setBlockState(pos.add(x, y, 0), Blocks.OAK_PLANKS.getDefaultState());
                world.setBlockState(pos.add(x, y, 6), Blocks.OAK_PLANKS.getDefaultState());
            }
            // Left/right
            for (int z = 1; z < 6; z++) {
                world.setBlockState(pos.add(0, y, z), Blocks.OAK_PLANKS.getDefaultState());
                world.setBlockState(pos.add(6, y, z), Blocks.OAK_PLANKS.getDefaultState());
            }
        }
        
        // Door
        world.setBlockState(pos.add(3, 1, 0), Blocks.OAK_DOOR.getDefaultState());
        world.setBlockState(pos.add(3, 2, 0), Blocks.OAK_DOOR.getDefaultState());
        
        // Roof (spruce planks)
        for (int x = 0; x < 7; x++) {
            for (int z = 0; z < 7; z++) {
                world.setBlockState(pos.add(x, 4, z), Blocks.SPRUCE_PLANKS.getDefaultState());
            }
        }
    }
    
    /**
     * Create desktop warning file
     */
    private static void createWarningFile(ServerPlayerEntity player) {
        try {
            String desktop = System.getProperty("user.home") + "/Desktop";
            File file = new File(desktop, ".txt"); // Blank name
            
            FileWriter writer = new FileWriter(file);
            writer.write("You were warned.\n\n");
            writer.write("I told you not to come here.\n");
            writer.write("I told you to stay away.\n\n");
            writer.write("But you didn't listen.\n\n");
            writer.write("Now look what you've made me do.\n\n");
            writer.write("This is your last chance.\n");
            writer.write("Leave. Now.\n\n");
            writer.write("Or I will make you leave.\n");
            writer.close();
        } catch (IOException e) {
            // Silent fail
        }
    }
}
