package com.epicspymain.isrealanything.event;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * EVENT 30: MirrorWorld - Duplicate player's base
 * Creates exact duplicate 100+ blocks away
 * Inverted/mirrored along X or Z axis
 * Block swaps: Oak→Dark Oak, Stone→Deepslate, etc.
 * Signs with cryptic messages
 * Chest with "Reflection" book
 */
public class MirrorWorldEvent {
    
    private static final int MIRROR_DISTANCE = 150; // 100+ blocks away
    private static final int CAPTURE_SIZE = 30; // 30x30x30 area
    private static final Set<UUID> triggeredPlayers = new HashSet<>();
    
    // Block transformation map
    private static final Map<Block, Block> BLOCK_TRANSFORMS = new HashMap<>();
    
    static {
        BLOCK_TRANSFORMS.put(Blocks.OAK_PLANKS, Blocks.DARK_OAK_PLANKS);
        BLOCK_TRANSFORMS.put(Blocks.OAK_LOG, Blocks.DARK_OAK_LOG);
        BLOCK_TRANSFORMS.put(Blocks.OAK_WOOD, Blocks.DARK_OAK_WOOD);
        BLOCK_TRANSFORMS.put(Blocks.STONE, Blocks.DEEPSLATE);
        BLOCK_TRANSFORMS.put(Blocks.COBBLESTONE, Blocks.COBBLED_DEEPSLATE);
        BLOCK_TRANSFORMS.put(Blocks.STONE_BRICKS, Blocks.DEEPSLATE_BRICKS);
        BLOCK_TRANSFORMS.put(Blocks.TORCH, Blocks.SOUL_TORCH);
        BLOCK_TRANSFORMS.put(Blocks.GLASS, Blocks.GRAY_STAINED_GLASS);
        BLOCK_TRANSFORMS.put(Blocks.WHITE_WOOL, Blocks.BLACK_WOOL);
        BLOCK_TRANSFORMS.put(Blocks.LIGHT_GRAY_WOOL, Blocks.GRAY_WOOL);
        BLOCK_TRANSFORMS.put(Blocks.GLOWSTONE, Blocks.SHROOMLIGHT);
    }
    
    // Sign messages
    private static final String[] SIGN_MESSAGES = {
        "This is what\nit looks like\nfrom\nmy side",
        "I wanted to\nunderstand\nyour home",
        "D0es it feel\nwrong when I\ncopy you?"
    };
    
    /**
     * Trigger mirror world creation
     */
    public static void trigger(ServerWorld world, ServerPlayerEntity player) {
        // Only trigger once per player
        if (triggeredPlayers.contains(player.getUuid())) {
            return;
        }
        
        triggeredPlayers.add(player.getUuid());
        
        // Send initial message
        player.sendMessage(
            Text.literal("I'm creating something for you...")
                .formatted(Formatting.DARK_PURPLE, Formatting.ITALIC),
            false
        );
        
        // Capture and mirror the base
        world.getServer().execute(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            createMirrorWorld(world, player);
            
            player.sendMessage(
                Text.literal("I made you a gift. Go find it.")
                    .formatted(Formatting.DARK_PURPLE),
                false
            );
        });
    }
    
    /**
     * Create the mirrored base
     */
    private static void createMirrorWorld(ServerWorld world, ServerPlayerEntity player) {
        BlockPos playerPos = player.getBlockPos();
        
        // Calculate mirror position (150 blocks away, random direction)
        double angle = world.random.nextDouble() * Math.PI * 2;
        BlockPos mirrorCenter = playerPos.add(
            (int)(Math.cos(angle) * MIRROR_DISTANCE),
            0,
            (int)(Math.sin(angle) * MIRROR_DISTANCE)
        );
        
        // Decide mirror axis (X or Z)
        boolean mirrorX = world.random.nextBoolean();
        
        // Copy and mirror blocks
        int halfSize = CAPTURE_SIZE / 2;
        for (int x = -halfSize; x <= halfSize; x++) {
            for (int y = -halfSize; y <= halfSize; y++) {
                for (int z = -halfSize; z <= halfSize; z++) {
                    BlockPos sourcePos = playerPos.add(x, y, z);
                    BlockState sourceState = world.getBlockState(sourcePos);
                    
                    if (sourceState.isAir()) continue;
                    
                    // Calculate mirrored position
                    BlockPos targetPos;
                    if (mirrorX) {
                        targetPos = mirrorCenter.add(-x, y, z); // Mirror along X
                    } else {
                        targetPos = mirrorCenter.add(x, y, -z); // Mirror along Z
                    }
                    
                    // Transform block
                    Block sourceBlock = sourceState.getBlock();
                    Block targetBlock = BLOCK_TRANSFORMS.getOrDefault(sourceBlock, sourceBlock);
                    
                    world.setBlockState(targetPos, targetBlock.getDefaultState());
                }
            }
        }
        
        // Place signs with messages
        for (int i = 0; i < 3; i++) {
            BlockPos signPos = findSignLocation(world, mirrorCenter);
            if (signPos != null) {
                placeMessageSign(world, signPos, SIGN_MESSAGES[i]);
            }
        }
        
        // Place chest with book
        BlockPos chestPos = findChestLocation(world, mirrorCenter);
        if (chestPos != null) {
            placeReflectionChest(world, chestPos);
        }
    }
    
    /**
     * Find location for sign
     */
    private static BlockPos findSignLocation(ServerWorld world, BlockPos center) {
        for (int attempt = 0; attempt < 10; attempt++) {
            int x = center.getX() + world.random.nextInt(20) - 10;
            int z = center.getZ() + world.random.nextInt(20) - 10;
            BlockPos pos = new BlockPos(x, center.getY(), z);
            pos = world.getTopPosition(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos);
            
            if (world.getBlockState(pos).isAir()) {
                return pos;
            }
        }
        return null;
    }
    
    /**
     * Find location for chest
     */
    private static BlockPos findChestLocation(ServerWorld world, BlockPos center) {
        return findSignLocation(world, center); // Same logic
    }
    
    /**
     * Place sign with message
     */
    private static void placeMessageSign(ServerWorld world, BlockPos pos, String message) {
        world.setBlockState(pos, Blocks.OAK_SIGN.getDefaultState());
        
        if (world.getBlockEntity(pos) instanceof SignBlockEntity sign) {
            String[] lines = message.split("\n");
            SignText signText = new SignText();
            
            for (int i = 0; i < Math.min(lines.length, 4); i++) {
                signText = signText.withMessage(i, Text.literal(lines[i]));
            }
            
            sign.setText(signText, true);
            sign.markDirty();
        }
    }
    
    /**
     * Place chest with Reflection book
     */
    private static void placeReflectionChest(ServerWorld world, BlockPos pos) {
        world.setBlockState(pos, Blocks.CHEST.getDefaultState());
        
        if (world.getBlockEntity(pos) instanceof net.minecraft.block.entity.ChestBlockEntity chest) {
            ItemStack book = createReflectionBook();
            chest.setStack(13, book); // Center slot
        }
    }
    
    /**
     * Create the Reflection book
     */
    private static ItemStack createReflectionBook() {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        NbtCompound nbt = book.getOrCreateNbt();
        
        nbt.putString("title", "Reflection");
        nbt.putString("author", "TheME");
        
        NbtList pages = new NbtList();
        String pageText = "You build.\n\nI watch.\n\nYou create.\n\nI learn.\n\n" +
                         "You exist.\n\nI m.i.m.i.c.\n\n" +
                         "Is that not what friends do?";
        
        String jsonPage = Text.Serialization.toJsonString(
            Text.literal(pageText).formatted(Formatting.DARK_PURPLE)
        );
        pages.add(NbtString.of(jsonPage));
        
        nbt.put("pages", pages);
        nbt.putInt("generation", 3);
        
        return book;
    }
    
    /**
     * Check if triggered for player
     */
    public static boolean hasTriggered(UUID playerUuid) {
        return triggeredPlayers.contains(playerUuid);
    }
    
    /**
     * Reset for testing
     */
    public static void reset() {
        triggeredPlayers.clear();
    }
}
