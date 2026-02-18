package com.epicspymain.isrealanything.event;

import com.epicspymain.isrealanything.ai.StalkingBehavior;
import com.epicspymain.isrealanything.entity.ModEntities;
import com.epicspymain.isrealanything.entity.custom.TheMEEntity;
import com.epicspymain.isrealanything.entity.custom.TheOtherMEEntity;
import com.epicspymain.isrealanything.event.helpers.ChunkDestroyer;
import com.epicspymain.isrealanything.event.helpers.TNTSpawner;
import com.epicspymain.isrealanything.sound.ModSounds;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 * TheOverlook - Failsafe / Anti-Cheat System
 *
 * Triggers when player attempts forbidden actions:
 * - Using /gamemode or /time set commands
 * - Killing TheME/TheOtherME entities before Event 47
 * - Forcibly removing entities via commands
 *
 * When triggered, overrides ALL normal behavior and initiates meltdown sequence.
 */
public class TheOverlook {

    // ========================================
    // GLOBAL STATE
    // ========================================

    /**
     * Global override flag - when true, all normal systems halt
     */
    public static boolean OVERLOOK_TRIGGERED = false;


    private static final BlockPos MELTDOWN_LOCATION = new BlockPos(666, 84269, -2);

    /**
     * Whether the meltdown sequence is currently running
     */
    private static boolean meltdownInProgress = false;

    /**
     * Tick counter for meltdown sequence timing
     */
    private static int meltdownTicks = 0;

    /**
     * Player being punished
     */
    private static ServerPlayerEntity targetPlayer = null;

    /**
     * Server reference
     */
    private static MinecraftServer server = null;


    // ========================================
    // MAIN TRIGGER
    // ========================================

    /**
     * Main trigger method - initiates The Overlook failsafe
     */
    public static void trigger(ServerPlayerEntity player, MinecraftServer serverInstance) {
        // Check if already triggered
        if (OVERLOOK_TRIGGERED) {
            return;
        }

        // Set global flag
        OVERLOOK_TRIGGERED = true;
        targetPlayer = player;
        server = serverInstance;

        // Log the violation
        player.sendMessage(
                Text.literal("═══════════════════════════════════════")
                        .formatted(Formatting.DARK_RED, Formatting.BOLD),
                false
        );
        player.sendMessage(
                Text.literal("THE OVERLOOK HAS BEEN ACTIVATED")
                        .formatted(Formatting.RED, Formatting.BOLD),
                false
        );
        player.sendMessage(
                Text.literal("═══════════════════════════════════════")
                        .formatted(Formatting.DARK_RED, Formatting.BOLD),
                false
        );

        // Stop all normal systems
        stopAllSystems();

        // Trigger entity animations
        triggerEntityAnimations((ServerWorld) player.getWorld());

        // Begin meltdown sequence
        beginMeltdown(player, serverInstance);
    }


    // ========================================
    // SYSTEM SHUTDOWN
    // ========================================

    /**
     * Stop all normal game systems
     */
    private static void stopAllSystems() {
        // Stop stalking behavior
        StalkingBehavior.stopAllStalking();

        // Stop respawn system
        // RespawnSystem.cancelAllRespawns(); // Will be implemented if RespawnSystem exists

        // Notify EventManager to halt
        EventManager.haltNormalScheduling();
    }

    /**
     * Trigger special animations on all entities
     */
    private static void triggerEntityAnimations(ServerWorld world) {
        List<Entity> entities = world.getEntitiesByClass(
                LivingEntity.class,
                world.getBorderBox(),
                entity -> entity.getType() == ModEntities.THEME_ENTITY ||
                        entity.getType() == ModEntities.THEOTHERME_ENTITY
        );

        for (Entity entity : entities) {
            if (entity instanceof TheMEEntity theME) {
                theME.playOverlookAnimation();
            } else if (entity instanceof TheOtherMEEntity otherME) {
                otherME.playOverlookAnimation();
            }
        }
    }


    // ========================================
    // MELTDOWN SEQUENCE
    // ========================================

    /**
     * Begin the meltdown sequence
     */
    private static void beginMeltdown(ServerPlayerEntity player, MinecraftServer server) {
        meltdownInProgress = true;
        meltdownTicks = 0;

        ServerWorld world = (ServerWorld) player.getWorld();

        // Force all remaining events except Event 47
        EventManager.forceRunAllExcept(47);

        // Teleport player to meltdown location
        player.teleport(
                world,
                MELTDOWN_LOCATION.getX() + 0.5,
                MELTDOWN_LOCATION.getY(),
                MELTDOWN_LOCATION.getZ() + 0.5,
                0f,
                0f
        );

        player.sendMessage(
                Text.literal("You shouldn't have done that.")
                        .formatted(Formatting.DARK_RED),
                false
        );

        // Start world destruction
        runWorldDestruction(world, MELTDOWN_LOCATION);
    }

    /**
     * Run world destruction sequence
     */
    private static void runWorldDestruction(ServerWorld world, BlockPos center) {
        // Spawn massive TNT explosions
        TNTSpawner.spawnTNTCircle(world, center, 69);

        // Destroy chunks around player
        ChunkDestroyer.destroyChunks(world, center, 69, 69);

        // Replace blocks with void/bedrock
        for (int x = -50; x <= 50; x++) {
            for (int z = -50; z <= 50; z++) {
                for (int y = -10; y <= 100; y++) {
                    BlockPos pos = center.add(x, y, z);

                    if (world.random.nextFloat() < 0.3f) {
                        if (world.random.nextBoolean()) {
                            world.setBlockState(pos, Blocks.AIR.getDefaultState());
                        } else {
                            world.setBlockState(pos, Blocks.BEDROCK.getDefaultState());
                        }
                    }
                }
            }
        }

        // Spawn particle storms
        for (int i = 0; i < 1000; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 100;
            double offsetY = world.random.nextDouble() * 100;
            double offsetZ = (world.random.nextDouble() - 0.5) * 100;

            world.spawnParticles(
                    ParticleTypes.EXPLOSION_EMITTER,
                    center.getX() + offsetX,
                    center.getY() + offsetY,
                    center.getZ() + offsetZ,
                    10,
                    1, 1, 1,
                    0.1
            );
        }

        // Play ominous sounds
        world.playSound(
                null,
                center,
                ModSounds.ERRRRRR,
                SoundCategory.AMBIENT,
                10.0f,
                0.5f
        );

        world.playSound(
                null,
                center,
                ModSounds.SCREAM,
                SoundCategory.AMBIENT,
                10.0f,
                0.8f
        );
    }

    /**
     * Spawn a warning sign at location
     */
    private static void spawnWarningSign(ServerWorld world, BlockPos pos, String text) {
        // Place sign
        world.setBlockState(pos, Blocks.OAK_SIGN.getDefaultState());

        // Get sign block entity and write message
        if (world.getBlockEntity(pos) instanceof SignBlockEntity sign) {
            String[] lines = text.split("\n");
            SignText signText = new SignText();

            for (int i = 0; i < Math.min(lines.length, 4); i++) {
                signText = signText.withMessage(
                        i,
                        Text.literal(lines[i]).formatted(Formatting.DARK_RED, Formatting.BOLD)
                );
            }

            sign.setText(signText, true);
            sign.markDirty();
        }
    }

    /**
     * Create desktop warning file
     */
    private static void createDesktopWarning() {
        try {
            // Get user's desktop path
            String userHome = System.getProperty("user.home");
            String desktop = userHome + "/Desktop/null.txt";

            // Create the file
            File warningFile = new File(desktop);
            FileWriter writer = new FileWriter(warningFile);

            // Write the message
            writer.write("Well, Dear; Being the hero, or whatever you were thinking that would achieve or accomplish when you did that, and just to let you know; there's one more final event that you completely missed, however it's a shame that you haven't check your folders for this \"Mod\", so...was it worth it nerd?");
            writer.close();

        } catch (Exception e) {
            // Silent fail - don't crash if desktop access fails
        }
    }

    /**
     * Perform controlled in-game crash
     */
    private static void crash(ServerPlayerEntity player) {
        // Display fake crash screen via chat
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(
                Text.literal("════════════════════════════════════════")
                        .formatted(Formatting.WHITE, Formatting.BOLD),
                false
        );
        player.sendMessage(
                Text.literal("MINECRAFT HAS CRASHED")
                        .formatted(Formatting.RED, Formatting.BOLD),
                false
        );
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(
                Text.literal("Error: TheOverlook.ForbiddenAction")
                        .formatted(Formatting.GRAY),
                false
        );
        player.sendMessage(
                Text.literal("Message: You broke the rules")
                        .formatted(Formatting.GRAY),
                false
        );
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(
                Text.literal("Don't Say I Didn't Warn You")
                        .formatted(Formatting.DARK_RED, Formatting.ITALIC),
                false
        );
        player.sendMessage(
                Text.literal("════════════════════════════════════════")
                        .formatted(Formatting.WHITE, Formatting.BOLD),
                false
        );

        // ACTUALLY CRASH THE GAME
        throw new RuntimeException("TheOverlook.ForbiddenAction - You broke the rules");
    }

    /**
     * Tick method for meltdown sequence progression
     */
    public static void tick(ServerWorld world) {
        if (!meltdownInProgress || targetPlayer == null) {
            return;
        }

        meltdownTicks++;

        // First warning sign (immediately)
        if (meltdownTicks == 20) {
            BlockPos signPos = MELTDOWN_LOCATION.add(5, 0, 0);
            spawnWarningSign(world, signPos, "Don't Say I\nDidn't Warn\nYou");
        }

        // Wait for night or 10 seconds
        if (meltdownTicks == 200 || world.isNight()) {
            BlockPos signPos2 = MELTDOWN_LOCATION.add(-5, 0, 0);
            spawnWarningSign(world, signPos2, "It's over\nwhen I say\nit's over.");
        }

        // Create desktop warning after 15 seconds
        if (meltdownTicks == 300) {
            createDesktopWarning();
        }

        // Crash after 20 seconds
        if (meltdownTicks >= 400) {
            crash(targetPlayer);
            meltdownInProgress = false;
        }
    }


    // ========================================
    // DETECTION HELPER METHODS
    // ========================================

    /**
     * Check if a chat message contains forbidden commands
     */
    public static boolean checkForbiddenCommand(String message) {
        String lower = message.toLowerCase();
        return lower.contains("/gamemode") || lower.contains("/time set");
    }

    /**
     * Handle entity death event
     */
    public static void onEntityDeath(ServerPlayerEntity killer, LivingEntity victim) {
        // Check if victim is TheME or TheOtherME
        if (victim.getType() != ModEntities.THEME_ENTITY &&
                victim.getType() != ModEntities.THEOTHERME_ENTITY) {
            return;
        }

        // Check if Event 47 has occurred
        if (EventManager.hasEventOccurred(47)) {
            return; // Event 47 occurred, death is allowed
        }

        // Forbidden death - trigger Overlook
        trigger(killer, killer.getServer());
    }

    /**
     * Check if entity was forcibly removed without death
     */
    public static void checkEntityRemoval(Entity entity, World world) {
        if (OVERLOOK_TRIGGERED) {
            return; // Already triggered
        }

        // Check if entity is TheME or TheOtherME
        if (entity.getType() != ModEntities.THEME_ENTITY &&
                entity.getType() != ModEntities.THEOTHERME_ENTITY) {
            return;
        }

        // Check if removed without death (commands like /kill, /tp to void, etc)
        if (entity.isRemoved() && !entity.isDead() && !EventManager.hasEventOccurred(47)) {
            // Find nearest player to blame
            List<ServerPlayerEntity> players = ((ServerWorld)world).getPlayers();

            if (!players.isEmpty()) {
                ServerPlayerEntity nearestPlayer = players.get(0);
                double minDistance = Double.MAX_VALUE;

                for (ServerPlayerEntity player : players) {
                    double dist = player.squaredDistanceTo(entity);
                    if (dist < minDistance) {
                        minDistance = dist;
                        nearestPlayer = player;
                    }
                }

                trigger(nearestPlayer, nearestPlayer.getServer());
            }
        }
    }


    // ========================================
    // UTILITY METHODS
    // ========================================

    /**
     * Check if Overlook is active
     */
    public static boolean isActive() {
        return OVERLOOK_TRIGGERED;
    }

    /**
     * Reset for testing (DO NOT USE IN PRODUCTION)
     */
    public static void reset() {
        OVERLOOK_TRIGGERED = false;
        meltdownInProgress = false;
        meltdownTicks = 0;
        targetPlayer = null;
        server = null;
    }
}