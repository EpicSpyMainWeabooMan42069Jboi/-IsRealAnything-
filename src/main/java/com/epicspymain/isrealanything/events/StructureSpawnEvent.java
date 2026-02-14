package com.epicspymain.isrealanything.events;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

import java.io.InputStream;
import java.util.*;

/**
 * EVENT 10: StructureSpawn - Base structure spawning system
 * Spawns .nbt structures from assets/isrealanything/structures/
 * Generates 15-30 blocks from player
 * Safe, flat location
 * Doesn't replace chests or liquids
 * Triggers once per world
 * Silent spawn (no particles/sound)
 */
public class StructureSpawnEvent {
    
    // Track which structures have been spawned per world
    private static final Map<String, Set<String>> spawnedStructures = new HashMap<>();
    
    private static final int MIN_DISTANCE = 15;
    private static final int MAX_DISTANCE = 30;
    
    /**
     * Attempt to spawn a structure
     * @param structureName Name of .nbt file (without extension)
     * @param minDay Minimum game day required
     * @return true if spawned successfully
     */
    public static boolean spawnStructure(
        ServerWorld world, 
        ServerPlayerEntity player, 
        String structureName,
        int minDay
    ) {
        // Check game day
        int gameDay = (int) (world.getTimeOfDay() / 24000);
        if (gameDay < minDay) {
            return false;
        }
        
        // Check if already spawned in this world
        String worldKey = world.getRegistryKey().getValue().toString();
        Set<String> worldStructures = spawnedStructures.computeIfAbsent(worldKey, k -> new HashSet<>());
        
        if (worldStructures.contains(structureName)) {
            return false; // Already spawned
        }
        
        // Find safe location
        BlockPos spawnPos = findSafeStructureLocation(world, player.getBlockPos());
        if (spawnPos == null) {
            return false;
        }
        
        // Load and place structure
        boolean success = placeStructure(world, spawnPos, structureName);
        
        if (success) {
            worldStructures.add(structureName);
        }
        
        return success;
    }
    
    /**
     * Place structure from NBT file
     */
    private static boolean placeStructure(ServerWorld world, BlockPos pos, String structureName) {
        try {
            // Load structure from resources
            Identifier structureId = new Identifier("isrealanything", "structures/" + structureName + ".nbt");
            InputStream stream = StructureSpawnEvent.class.getResourceAsStream(
                "/assets/isrealanything/structures/" + structureName + ".nbt"
            );
            
            if (stream == null) {
                return false; // Structure file not found
            }
            
            NbtCompound nbt = NbtIo.readCompressed(stream);
            StructureTemplate template = new StructureTemplate();
            template.readNbt(world.getRegistryManager(), nbt);
            
            // Place with random rotation
            BlockRotation rotation = BlockRotation.values()[world.random.nextInt(BlockRotation.values().length)];
            StructurePlacementData placementData = new StructurePlacementData()
                .setRotation(rotation)
                .setMirror(BlockMirror.NONE)
                .setIgnoreEntities(false);
            
            // Place structure
            template.place(world, pos, pos, placementData, world.getRandom(), Block.NOTIFY_ALL);
            
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Find safe location for structure spawn
     */
    private static BlockPos findSafeStructureLocation(ServerWorld world, BlockPos playerPos) {
        Random random = world.getRandom();
        
        for (int attempt = 0; attempt < 50; attempt++) {
            // Random angle and distance
            double angle = random.nextDouble() * Math.PI * 2;
            int distance = MIN_DISTANCE + random.nextInt(MAX_DISTANCE - MIN_DISTANCE);
            
            int x = playerPos.getX() + (int)(Math.cos(angle) * distance);
            int z = playerPos.getZ() + (int)(Math.sin(angle) * distance);
            
            // Find ground level
            BlockPos testPos = new BlockPos(x, playerPos.getY(), z);
            testPos = world.getTopPosition(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, testPos);
            
            // Check if location is suitable
            if (isSafeStructureLocation(world, testPos)) {
                return testPos;
            }
        }
        
        return null;
    }
    
    /**
     * Check if location is safe for structure placement
     */
    private static boolean isSafeStructureLocation(ServerWorld world, BlockPos pos) {
        // Check 10x10 area for flatness and safety
        int flatCount = 0;
        
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                BlockPos checkPos = pos.add(x, 0, z);
                BlockPos below = checkPos.down();
                
                // Check solid ground
                if (!world.getBlockState(below).isSolidBlock(world, below)) {
                    continue;
                }
                
                // Check no liquids
                if (world.getBlockState(checkPos).getFluidState().isEmpty()) {
                    flatCount++;
                }
                
                // Check no chests
                Block block = world.getBlockState(checkPos).getBlock();
                if (block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST) {
                    return false;
                }
            }
        }
        
        // Need at least 80% flat ground
        return flatCount >= 80;
    }
    
    /**
     * Reset for testing
     */
    public static void reset() {
        spawnedStructures.clear();
    }
    
    // ===== SPECIFIC STRUCTURE SPAWNERS =====
    
    /**
     * Freedom Home (Americanized version) - Day 3+
     */
    public static boolean spawnFreedomHome(ServerWorld world, ServerPlayerEntity player) {
        return spawnStructure(world, player, "freedom_home_americanized", 3);
    }
    
    /**
     * Meadow - Day 6+
     */
    public static boolean spawnMeadow(ServerWorld world, ServerPlayerEntity player) {
        return spawnStructure(world, player, "meadow", 6);
    }
    
    /**
     * Iron Trap - Day 3+
     */
    public static boolean spawnIronTrap(ServerWorld world, ServerPlayerEntity player) {
        return spawnStructure(world, player, "irontrap", 3);
    }
    
    /**
     * Memory - Day 10+
     */
    public static boolean spawnMemory(ServerWorld world, ServerPlayerEntity player) {
        return spawnStructure(world, player, "memory", 10);
    }
    
    /**
     * Bedrock Pillar - Day 5+
     */
    public static boolean spawnBedrockPillar(ServerWorld world, ServerPlayerEntity player) {
        return spawnStructure(world, player, "bedrockpillar", 5);
    }
    
    /**
     * Strip Mine - Day 4+
     */
    public static boolean spawnStripMine(ServerWorld world, ServerPlayerEntity player) {
        return spawnStructure(world, player, "stripmine", 4);
    }
    
    /**
     * Corrupted Freedom Home - Day 6+
     */
    public static boolean spawnCorruptedFreedomHome(ServerWorld world, ServerPlayerEntity player) {
        return spawnStructure(world, player, "freedom_home_corrupted", 6);
    }
}
