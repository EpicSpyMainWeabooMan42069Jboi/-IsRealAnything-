package com.epicspymain.isrealanything.events.helpers;

import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;

import java.io.InputStream;

/**
 * Helper: StructureManager - Structure placement utilities
 * Handles .nbt structure spawning with rotation support
 */
public class StructureManager {
    
    /**
     * Place structure with random rotation
     */
    public static boolean placeStructureRandomRotation(
        ServerWorld world,
        BlockPos pos,
        String structureName
    ) {
        try {
            // Load structure
            InputStream stream = StructureManager.class.getResourceAsStream(
                "/assets/isrealanything/structures/" + structureName + ".nbt"
            );
            
            if (stream == null) {
                return false;
            }
            
            NbtCompound nbt = NbtIo.readCompressed(stream);
            StructureTemplate template = new StructureTemplate();
            template.readNbt(world.getRegistryManager(), nbt);
            
            // Random rotation
            BlockRotation rotation = BlockRotation.values()[
                world.random.nextInt(BlockRotation.values().length)
            ];
            
            StructurePlacementData placementData = new StructurePlacementData()
                .setRotation(rotation)
                .setMirror(BlockMirror.NONE)
                .setIgnoreEntities(false);
            
            // Place
            template.place(world, pos, pos, placementData, world.getRandom(), Block.NOTIFY_ALL);
            
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Place structure with specific rotation
     */
    public static boolean placeStructure(
        ServerWorld world,
        BlockPos pos,
        String structureName,
        BlockRotation rotation
    ) {
        try {
            InputStream stream = StructureManager.class.getResourceAsStream(
                "/assets/isrealanything/structures/" + structureName + ".nbt"
            );
            
            if (stream == null) {
                return false;
            }
            
            NbtCompound nbt = NbtIo.readCompressed(stream);
            StructureTemplate template = new StructureTemplate();
            template.readNbt(world.getRegistryManager(), nbt);
            
            StructurePlacementData placementData = new StructurePlacementData()
                .setRotation(rotation)
                .setMirror(BlockMirror.NONE)
                .setIgnoreEntities(false);
            
            template.place(world, pos, pos, placementData, world.getRandom(), Block.NOTIFY_ALL);
            
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }
}
