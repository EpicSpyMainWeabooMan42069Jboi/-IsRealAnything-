package com.epicspymain.isrealanything.event;

import com.epicspymain.isrealanything.screen.FrozenOverlayRenderer;
import com.epicspymain.isrealanything.screen.GlitchOverlay;
import com.epicspymain.isrealanything.screen.ScreenOverlayRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * EVENT 39: LastChance - Villager home trap structure
 * If player enters, destroys, or gets in perimeter:
 * - Screen freezes (actual freeze overlay)
 * - Glitch effects
 * - Message: "BEGONE THOT"
 * - Game crashes after delay
 * - Desktop txt file with warning message
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

        // Build villager-style house (use NBT if available, fallback to manual)
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
        // Freeze screen immediately
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> {
            FrozenOverlayRenderer.freeze(100); // 5 seconds
            GlitchOverlay.trigger(100, 1.0f);
        });

        // Sequence the punishment
        world.getServer().execute(() -> {
            new Thread(() -> {
                try {
                    // Wait 2 seconds
                    Thread.sleep(2000);

                    // Show message
                    world.getServer().execute(() -> {
                        player.sendMessage(
                                Text.literal("BEGONE THOT")
                                        .formatted(Formatting.DARK_RED, Formatting.BOLD),
                                false
                        );

                        // Red flash
                        client.execute(() -> {
                            ScreenOverlayRenderer.executeRedFlash(500);
                        });
                    });

                    // Wait 1 second
                    Thread.sleep(1000);

                    // Create desktop file
                    createWarningFile(player);

                    // Wait 2 more seconds
                    Thread.sleep(2000);

                    // Crash the server
                    world.getServer().execute(() -> {
                        world.getServer().stop(false);
                    });

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        });
    }

    /**
     * Build villager house using NBT structure or fallback to manual
     */
    private static void buildVillagerHouse(ServerWorld world, BlockPos pos) {
        // THIS is where it loads your NBT ↓
        StructureTemplateManager manager = world.getStructureTemplateManager();
        Identifier structureId = Identifier.of("isrealanything", "villagertrap");

        Optional<StructureTemplate> template = manager.getTemplate(structureId);

        if (template.isPresent()) {
            // ✅ Loads your villagertrap.nbt
            template.get().place(world, pos, pos,
                    new StructurePlacementData()
                            .setRotation(BlockRotation.NONE)
                            .setMirror(BlockMirror.NONE)
                            .setIgnoreEntities(false),
                    world.getRandom(),
                    Block.NOTIFY_ALL
            );
        } else {
            // ❌ NBT not found, falls back to manual blocks
            buildVillagerHouseManual(world, pos);
        }
    }

    /**
     * Manual house building (fallback if NBT not found)
     */
    private static void buildVillagerHouseManual(ServerWorld world, BlockPos pos) {
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

        // Windows
        world.setBlockState(pos.add(1, 2, 0), Blocks.GLASS_PANE.getDefaultState());
        world.setBlockState(pos.add(5, 2, 0), Blocks.GLASS_PANE.getDefaultState());

        // Roof (spruce planks)
        for (int x = 0; x < 7; x++) {
            for (int z = 0; z < 7; z++) {
                world.setBlockState(pos.add(x, 4, z), Blocks.SPRUCE_PLANKS.getDefaultState());
            }
        }

        // Interior - bed (makes it look lived-in)
        world.setBlockState(pos.add(1, 1, 1), Blocks.RED_BED.getDefaultState());

        // Chest with creepy sign
        world.setBlockState(pos.add(5, 1, 5), Blocks.CHEST.getDefaultState());
    }

    /**
     * Create desktop warning file
     */
    private static void createWarningFile(ServerPlayerEntity player) {
        try {
            String desktop = System.getProperty("user.home") + "/Desktop";

            // Use player name for extra creepiness
            String filename = player.getName().getString() + "_last_chance.txt";
            File file = new File(desktop, filename);

            FileWriter writer = new FileWriter(file);
            writer.write("Dear " + player.getName().getString() + ",\n\n");
            writer.write("You were warned.\n\n");
            writer.write("I told you not to come here.\n");
            writer.write("I told you to stay away.\n\n");
            writer.write("But you didn't listen.\n\n");
            writer.write("Now look what you've made me do.\n\n");
            writer.write("This is your last chance.\n");
            writer.write("Leave. Now.\n\n");
            writer.write("Or I will make you leave.\n\n");
            writer.write("- EpicSpyMain69420\n");
            writer.close();
        } catch (IOException e) {
            // Silent fail
        }
    }

    /**
     * Check if structure is still standing
     */
    public static boolean isStructureDestroyed(ServerWorld world, UUID playerUuid) {
        BlockPos structurePos = structureLocations.get(playerUuid);
        if (structurePos == null) return false;

        // Check if door still exists
        return !world.getBlockState(structurePos.add(3, 1, 0)).isOf(Blocks.OAK_DOOR);
    }
}