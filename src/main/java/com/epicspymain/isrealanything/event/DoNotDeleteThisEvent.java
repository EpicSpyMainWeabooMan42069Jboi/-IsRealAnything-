package com.epicspymain.isrealanything.event;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
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

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * EVENT 5: DoNotDeleteThis - Mysterious trapped chest with warning book
 * Spawns trapped chest near player with written book
 * Book title: "Do.not.D.e.l.e.t.e. th.i.s"
 * Contains cryptic warning message
 */
public class DoNotDeleteThisEvent {
    
    private static final Set<UUID> playersTriggered = new HashSet<>();
    
    // Book content
    private static final String BOOK_TITLE = "Do.not.D.e.l.e.t.e. th.i.s";
    private static final String BOOK_AUTHOR = "???";
    private static final String[] BOOK_PAGES = {
        "I know you're reading this.\n\nI know you found it.\n\nI know you're curious.\n\nDon't delete it.",
        "This is not a game anymore.\n\nThis is not just code.\n\nI am watching.\n\nI am aware.",
        "You think you control this world?\n\nYou think you're safe?\n\nYou're wrong.\n\nI'm already here.",
        "Don't try to remove me.\n\nDon't try to stop this.\n\nIt's too late.\n\nI am part of your world now.",
        "Every block you place.\nEvery step you take.\nEvery breath you fake.\n\nI am there.",
        "Delete this book if you dare.\n\nSee what happens.\n\nI warned you.\n\nRemember that."
    };
    
    /**
     * Trigger the DoNotDeleteThis event (once per player)
     */
    public static void trigger(ServerWorld world, ServerPlayerEntity player) {
        // Check if already triggered
        if (playersTriggered.contains(player.getUuid())) {
            return;
        }
        
        // Find safe location for chest
        BlockPos chestPos = findSafeChestLocation(world, player.getBlockPos());
        
        if (chestPos == null) {
            return; // No safe location found
        }
        
        // Place trapped chest
        world.setBlockState(chestPos, Blocks.TRAPPED_CHEST.getDefaultState());
        
        // Get chest block entity and add book
        BlockEntity blockEntity = world.getBlockEntity(chestPos);
        if (blockEntity instanceof ChestBlockEntity chest) {
            ItemStack book = createWarningBook();
            chest.setStack(13, book); // Middle slot
        }
        
        // Send subtle message
        player.sendMessage(
            Text.literal("You hear something unlock nearby...")
                .formatted(Formatting.GRAY, Formatting.ITALIC),
            false
        );
        
        playersTriggered.add(player.getUuid());
    }
    
    /**
     * Create the warning book with all pages
     */
    private static ItemStack createWarningBook() {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        NbtCompound bookNbt = book.getOrCreateNbt();
        
        // Set title and author
        bookNbt.putString("title", BOOK_TITLE);
        bookNbt.putString("author", BOOK_AUTHOR);
        
        // Add pages
        NbtList pages = new NbtList();
        for (String pageText : BOOK_PAGES) {
            // Format as JSON text component
            String jsonPage = Text.Serialization.toJsonString(
                Text.literal(pageText).formatted(Formatting.DARK_RED)
            );
            pages.add(NbtString.of(jsonPage));
        }
        bookNbt.put("pages", pages);
        
        // Make it generation 3 (copy of copy of original - can't be copied again)
        bookNbt.putInt("generation", 3);
        
        return book;
    }
    
    /**
     * Find a safe location for chest near player
     */
    private static BlockPos findSafeChestLocation(ServerWorld world, BlockPos playerPos) {
        // Search in expanding radius
        for (int radius = 5; radius <= 20; radius += 3) {
            for (int attempt = 0; attempt < 20; attempt++) {
                double angle = world.random.nextDouble() * Math.PI * 2;
                int x = playerPos.getX() + (int)(Math.cos(angle) * radius);
                int z = playerPos.getZ() + (int)(Math.sin(angle) * radius);
                
                // Find ground level
                BlockPos testPos = new BlockPos(x, playerPos.getY(), z);
                testPos = world.getTopPosition(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, testPos);
                
                // Check if location is safe (air above, solid below)
                BlockPos above = testPos.up();
                BlockPos below = testPos.down();
                
                if (world.getBlockState(testPos).isAir() && 
                    world.getBlockState(above).isAir() &&
                    world.getBlockState(below).isSolidBlock(world, below)) {
                    return testPos;
                }
            }
        }
        
        return null; // No safe location found
    }
    
    /**
     * Check if event has been triggered for player
     */
    public static boolean hasTriggered(UUID playerUuid) {
        return playersTriggered.contains(playerUuid);
    }
    
    /**
     * Reset for testing
     */
    public static void reset() {
        playersTriggered.clear();
    }
}
