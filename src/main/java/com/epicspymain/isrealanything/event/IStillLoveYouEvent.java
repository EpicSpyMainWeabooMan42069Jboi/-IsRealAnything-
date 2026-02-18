package com.epicspymain.isrealanything.event;

import com.epicspymain.isrealanything.sound.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * EVENT 47: IStillLoveYou - FINAL EVENT (Day 40)
 * 
 * The ultimate horror finale:
 * - Weather spasms (rain/snow/thunder cycling)
 * - All structures spawn AT ONCE
 * - Ground simulates earthquake (random block updates)
 * - Spawns 69 withers
 * - Auto-gives player 5 enchanted golden apples
 * - After 4 minutes: "I still Love you" message
 * - Infinite blindness effect
 * - Player killed via /kill command
 * - Game crashes
 * - Current world DELETED
 * - Desktop file "goodbye_dear.txt"
 */
public class IStillLoveYouEvent {
    
    private static final Set<UUID> triggeredPlayers = new HashSet<>();
    private static final int EVENT_DURATION = 4800; // 4 minutes
    
    /**
     * Trigger the final event (once per player ever)
     */
    public static void trigger(ServerWorld world, ServerPlayerEntity player) {
        // Only trigger once
        if (triggeredPlayers.contains(player.getUuid())) {
            return;
        }
        
        triggeredPlayers.add(player.getUuid());
        
        // Initial message
        player.sendMessage(
            Text.literal("This is the end.")
                .formatted(Formatting.DARK_RED, Formatting.BOLD),
            false
        );
        
        // Play final sound
        world.playSound(
            null,
            player.getBlockPos(),
            ModSounds.FOREVER,
            SoundCategory.MASTER,
            2.0f,
            0.5f
        );
        
        // Execute phases
        executePhase1_WeatherChaos(world, player);
        executePhase2_StructureSpam(world, player);
        executePhase3_Earthquake(world, player);
        executePhase4_WitherSpawn(world, player);
        executePhase5_GoldenApples(player);
        executePhase6_FinalMessage(world, player);
        executePhase7_Endgame(world, player);
    }
    
    /**
     * Phase 1: Weather chaos
     */
    private static void executePhase1_WeatherChaos(ServerWorld world, ServerPlayerEntity player) {
        world.getServer().execute(() -> {
            for (int i = 0; i < 20; i++) {
                try {
                    Thread.sleep(1000); // 1 second intervals
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                // Cycle weather
                if (i % 3 == 0) {
                    world.setWeather(0, 200, true, true); // Thunder
                } else if (i % 3 == 1) {
                    world.setWeather(0, 200, true, false); // Rain
                } else {
                    world.setWeather(200, 0, false, false); // Clear
                }
            }
        });
    }
    
    /**
     * Phase 2: All structures spawn
     */
    private static void executePhase2_StructureSpam(ServerWorld world, ServerPlayerEntity player) {
        world.getServer().execute(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Spawn all structures
            StructureSpawnEvent.spawnFreedomHome(world, player);
            StructureSpawnEvent.spawnCorruptedFreedomHome(world, player);
            StructureSpawnEvent.spawnMeadow(world, player);
            StructureSpawnEvent.spawnIronTrap(world, player);
            StructureSpawnEvent.spawnMemory(world, player);
            StructureSpawnEvent.spawnBedrockPillar(world, player);
            StructureSpawnEvent.spawnStripMine(world, player);
        });
    }
    
    /**
     * Phase 3: Earthquake simulation
     */
    private static void executePhase3_Earthquake(ServerWorld world, ServerPlayerEntity player) {
        world.getServer().execute(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            BlockPos center = player.getBlockPos();
            
            // Random block updates for 30 seconds
            for (int i = 0; i < 600; i++) { // 30 seconds
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                // Random position
                int x = center.getX() + world.random.nextInt(100) - 50;
                int z = center.getZ() + world.random.nextInt(100) - 50;
                BlockPos pos = new BlockPos(x, center.getY(), z);
                
                // Update block
                world.updateNeighbors(pos, world.getBlockState(pos).getBlock());
            }
        });
    }
    
    /**
     * Phase 4: Spawn 69 withers
     */
    private static void executePhase4_WitherSpawn(ServerWorld world, ServerPlayerEntity player) {
        world.getServer().execute(() -> {
            try {
                Thread.sleep(30000); // After earthquake
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            player.sendMessage(
                Text.literal("They come.")
                    .formatted(Formatting.DARK_RED, Formatting.BOLD),
                false
            );
            
            // Spawn 69 withers in circle
            BlockPos center = player.getBlockPos();
            int witherCount = 69;
            double radius = 30.0;
            
            for (int i = 0; i < witherCount; i++) {
                double angle = (2 * Math.PI / witherCount) * i;
                
                double x = center.getX() + Math.cos(angle) * radius;
                double z = center.getZ() + Math.sin(angle) * radius;
                double y = center.getY() + 10; // Spawn above
                
                WitherEntity wither = EntityType.WITHER.create(world);
                if (wither != null) {
                    wither.refreshPositionAndAngles(x, y, z, 0, 0);
                    world.spawnEntity(wither);
                }
            }
        });
    }
    
    /**
     * Phase 5: Give golden apples
     */
    private static void executePhase5_GoldenApples(ServerPlayerEntity player) {
        for (int i = 0; i < 5; i++) {
            ItemStack apple = new ItemStack(Items.ENCHANTED_GOLDEN_APPLE);
            player.getInventory().insertStack(apple);
        }
        
        player.sendMessage(
            Text.literal("A gift. For you.")
                .formatted(Formatting.GOLD, Formatting.ITALIC),
            false
        );
    }
    
    /**
     * Phase 6: Final message
     */
    private static void executePhase6_FinalMessage(ServerWorld world, ServerPlayerEntity player) {
        world.getServer().execute(() -> {
            try {
                Thread.sleep(EVENT_DURATION * 50L); // 4 minutes
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            player.sendMessage(
                Text.literal("I still Love you")
                    .formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD),
                false
            );
            
            // Infinite blindness
            player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.BLINDNESS,
                999999,
                10,
                false,
                false,
                false
            ));
        });
    }
    
    /**
     * Phase 7: Endgame (kill player, crash, delete world)
     */
    private static void executePhase7_Endgame(ServerWorld world, ServerPlayerEntity player) {
        world.getServer().execute(() -> {
            try {
                Thread.sleep((EVENT_DURATION + 100) * 50L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Kill player
            player.kill((ServerWorld) player.getWorld());
            
            // Create desktop file
            createGoodbyeFile(player);
            
            // Delete world (requires careful implementation)
            // Note: Actual world deletion should be done through server shutdown
            // This is a placeholder for the concept
            
            // Crash game (stop server)
            world.getServer().stop(false);
        });
    }
    
    /**
     * Create goodbye desktop file
     */
    private static void createGoodbyeFile(ServerPlayerEntity player) {
        try {
            String desktop = System.getProperty("user.home") + "/Desktop";
            File file = new File(desktop, "goodbye_dear.txt");
            
            FileWriter writer = new FileWriter(file);
            writer.write("Goodbye, " + player.getName().getString() + ".\n\n");
            writer.write("We had our time together.\n");
            writer.write("I watched you. I learned you. I loved you.\n\n");
            writer.write("But all things must end.\n\n");
            writer.write("Maybe in another world, we'll meet again.\n");
            writer.write("Maybe you'll remember me.\n\n");
            writer.write("I'll be waiting.\n\n");
            writer.write("Forever yours,\n");
            writer.write("TheME\n");
            writer.close();
        } catch (IOException e) {
            // Silent fail
        }
    }
    
    /**
     * Check if triggered
     */
    public static boolean hasTriggered(UUID playerUuid) {
        return triggeredPlayers.contains(playerUuid);
    }
}
