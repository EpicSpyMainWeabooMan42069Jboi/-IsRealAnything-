package com.epicspymain.isrealanything.ai;

import com.epicspymain.isrealanything.IsRealAnything;
import com.epicspymain.isrealanything.entity.ModEntities;
import com.epicspymain.isrealanything.entity.custom.TheMEEntity;
import com.epicspymain.isrealanything.entity.custom.TheOtherMEEntity;
import com.epicspymain.isrealanything.sound.ModSounds;
import net.minecraft.entity.SpawnReason;
import net.minecraft.world.Heightmap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class StalkingBehavior {

    private static final Random RANDOM = new Random();
    private static final Map<UUID, StalkingData> STALKING_ENTITIES = new HashMap<>();

    // Phase constants
    private static final int PHASE_1_DISTANCE = 64;  // Far away
    private static final int PHASE_2_DISTANCE = 32;  // Medium distance
    private static final int PHASE_3_DISTANCE = 16;  // Close proximity
    private static final int PHASE_4_DISTANCE = 8;   // Very close
    private static final int PHASE_5_DISTANCE = 4;   // Right behind you

    // Timing constants (in ticks)
    private static final int PHASE_1_INTERVAL = 6000;  // 5 minutes
    private static final int PHASE_2_INTERVAL = 3000;  // 2.5 minutes
    private static final int PHASE_3_INTERVAL = 1200;  // 1 minute
    private static final int PHASE_4_INTERVAL = 600;   // 30 seconds
    private static final int PHASE_5_INTERVAL = 200;   // 10 seconds

    private static int globalTick = 0;
    private static boolean overlookTriggered = false;


    private static class StalkingData {
        UUID entityId;
        BlockPos lastPosition;
        int ticksSinceAppearance;
        int phase;
        boolean isVisible;
        int vanishCooldown;

        StalkingData(UUID entityId, int phase) {
            this.entityId = entityId;
            this.phase = phase;
            this.ticksSinceAppearance = 0;
            this.isVisible = false;
            this.vanishCooldown = 0;
        }
    }

    /**
     * Updates stalking behavior for all entities.
     * Called every tick from EventManager.
     *
     * @param world The server world
     * @param entity The stalking entity (can be null)
     * @param player The target player
     * @param stalkingPhase Current stalking intensity (1-5)
     */
    public static void update(ServerWorld world, LivingEntity entity, ServerPlayerEntity player, int stalkingPhase) {
        if (overlookTriggered) {
            stopAllStalking();
            return;
        }

        if (world == null || player == null) {
            return;
        }

        globalTick++;

        // Phase-based behavior
        switch (stalkingPhase) {
            case 1:
                handlePhase1(world, player);
                break;
            case 2:
                handlePhase2(world, player);
                break;
            case 3:
                handlePhase3(world, player);
                break;
            case 4:
                handlePhase4(world, player);
                break;
            case 5:
                handlePhase5(world, player);
                break;
            default:
                // No stalking
                break;
        }

        // Update existing stalking entities
        updateStalkingEntities(world, player, stalkingPhase);
    }

    /**
     * Phase 1: Rare distant watching.
     * Entity appears far away, watches, then vanishes.
     */
    private static void handlePhase1(ServerWorld world, ServerPlayerEntity player) {
        if (globalTick % PHASE_1_INTERVAL != 0) {
            return;
        }

        if (RANDOM.nextFloat() < 0.3f) {
            spawnStalkingEntity(world, player, PHASE_1_DISTANCE, 1);
            IsRealAnything.LOGGER.debug("Phase 1: Distant stalking entity spawned");
        }
    }

    /**
     * Phase 2: More frequent appearances at medium distance.
     */
    private static void handlePhase2(ServerWorld world, ServerPlayerEntity player) {
        if (globalTick % PHASE_2_INTERVAL != 0) {
            return;
        }

        if (RANDOM.nextFloat() < 0.5f) {
            spawnStalkingEntity(world, player, PHASE_2_DISTANCE, 2);
            IsRealAnything.LOGGER.debug("Phase 2: Medium distance stalking");
        }
    }

    /**
     * Phase 3: Inside player's base, peeking from corners.
     */
    private static void handlePhase3(ServerWorld world, ServerPlayerEntity player) {
        if (globalTick % PHASE_3_INTERVAL != 0) {
            return;
        }

        if (RANDOM.nextFloat() < 0.7f) {
            // Spawn at close range, possibly indoors
            BlockPos spawnPos = findPeekingPosition(world, player.getBlockPos(), PHASE_3_DISTANCE);
            if (spawnPos != null) {
                spawnStalkingEntityAt(world, player, spawnPos, 3);
                IsRealAnything.LOGGER.debug("Phase 3: Base infiltration");
            }
        }
    }

    /**
     * Phase 4: Aggressive teleporting behind player.
     */
    private static void handlePhase4(ServerWorld world, ServerPlayerEntity player) {
        if (globalTick % PHASE_4_INTERVAL != 0) {
            return;
        }

        if (RANDOM.nextFloat() < 0.8f) {
            // Spawn behind player
            BlockPos behindPos = getPositionBehindPlayer(player, PHASE_4_DISTANCE);
            spawnStalkingEntityAt(world, player, behindPos, 4);
            IsRealAnything.LOGGER.debug("Phase 4: Behind player spawn");
        }
    }

    /**
     * Phase 5: Constant presence, multiple entities.
     */
    private static void handlePhase5(ServerWorld world, ServerPlayerEntity player) {
        if (globalTick % PHASE_5_INTERVAL != 0) {
            return;
        }

        // Spawn multiple entities
        int spawnCount = 1 + RANDOM.nextInt(3);
        for (int i = 0; i < spawnCount; i++) {
            BlockPos pos = findNearbyPosition(world, player.getBlockPos(), PHASE_5_DISTANCE, PHASE_4_DISTANCE);
            if (pos != null) {
                spawnStalkingEntityAt(world, player, pos, 5);
            }
        }

        // Frequent messages
        if (RANDOM.nextFloat() < 0.5f) {
            sendCreepyMessage(player);
        }

        IsRealAnything.LOGGER.debug("Phase 5: Constant presence - {} entities", spawnCount);
    }

    /**
     * Updates existing stalking entities (vanishing, watching, etc.).
     */
    private static void updateStalkingEntities(ServerWorld world, ServerPlayerEntity player, int phase) {
        Iterator<Map.Entry<UUID, StalkingData>> iterator = STALKING_ENTITIES.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, StalkingData> entry = iterator.next();
            StalkingData data = entry.getValue();

            // Find entity in world
            LivingEntity entity = (LivingEntity) world.getEntity(data.entityId);

            if (entity == null || entity.isDead()) {
                iterator.remove();
                continue;
            }

            data.ticksSinceAppearance++;

            // Check if player is looking at entity
            boolean playerLooking = isPlayerLookingAt(player, entity);

            // Vanish if player approaches or looks directly
            double distance = player.getPos().distanceTo(entity.getPos());

            if (playerLooking || distance < 5.0) {
                vanishEntity(world, entity, data);
                iterator.remove();
                IsRealAnything.LOGGER.debug("Entity vanished (player aware)");
                continue;
            }

            // Auto-vanish after time based on phase
            int vanishTime = getVanishTime(phase);
            if (data.ticksSinceAppearance > vanishTime) {
                vanishEntity(world, entity, data);
                iterator.remove();
                IsRealAnything.LOGGER.debug("Entity vanished (timeout)");
            }
        }
    }

    /**
     * Spawns a stalking entity at a calculated position.
     */
    private static void spawnStalkingEntity(ServerWorld world, ServerPlayerEntity player, int distance, int phase) {
        BlockPos pos = findNearbyPosition(world, player.getBlockPos(), distance - 5, distance + 5);
        if (pos != null) {
            spawnStalkingEntityAt(world, player, pos, phase);
        }
    }

    /**
     * Spawns a stalking entity at a specific position.
     */
    private static void spawnStalkingEntityAt(ServerWorld world, ServerPlayerEntity player, BlockPos pos, int phase) {
        // Choose entity type based on phase
        LivingEntity entity;

        if (phase >= 4 || RANDOM.nextFloat() < 0.3f) {
            // Spawn TheOtherME for higher phases
            entity = ModEntities.THEOTHERME_ENTITY.create(world, SpawnReason.COMMAND);
        } else {
            // Spawn TheME for lower phases
            entity = ModEntities.THEME_ENTITY.create(world, SpawnReason.COMMAND);
        }

        if (entity != null) {
            entity.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);

            // Make entity look at player
            Vec3d direction = player.getPos().subtract(entity.getPos());
            float yaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90;
            float pitch = (float) Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

            entity.setYaw(yaw);
            entity.setPitch(pitch);

            // Spawn entity
            world.spawnEntity(entity);

            // Track stalking data
            StalkingData data = new StalkingData(entity.getUuid(), phase);
            data.lastPosition = pos;
            data.isVisible = true;
            STALKING_ENTITIES.put(entity.getUuid(), data);

            IsRealAnything.LOGGER.debug("Stalking entity spawned at {} (phase {})", pos, phase);
        }
    }

    /**
     * Finds a "peeking" position (corner, doorway, window).
     */
    private static BlockPos findPeekingPosition(ServerWorld world, BlockPos center, int range) {
        for (int attempt = 0; attempt < 20; attempt++) {
            int x = center.getX() + RANDOM.nextInt(range * 2) - range;
            int z = center.getZ() + RANDOM.nextInt(range * 2) - range;
            int y = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);


            BlockPos pos = new BlockPos(x, y, z);

            // Check if position is valid and somewhat hidden
            if (world.getBlockState(pos).isAir() &&
                    world.getBlockState(pos.up()).isAir() &&
                    world.getLightLevel(pos) < 8) {
                return pos;
            }
        }

        return null;
    }

    /**
     * Gets position behind player.
     */
    private static BlockPos getPositionBehindPlayer(ServerPlayerEntity player, int distance) {
        float yaw = player.getYaw();
        double radians = Math.toRadians(yaw);

        int x = (int) (player.getX() - distance * Math.sin(radians));
        int z = (int) (player.getZ() + distance * Math.cos(radians));
        int y = player.getWorld().getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);

        return new BlockPos(x, y, z);
    }

    /**
     * Finds a nearby valid position.
     */
    private static BlockPos findNearbyPosition(ServerWorld world, BlockPos center, int minRange, int maxRange) {
        for (int attempt = 0; attempt < 15; attempt++) {
            double angle = RANDOM.nextDouble() * 2 * Math.PI;
            int distance = minRange + RANDOM.nextInt(maxRange - minRange);

            int x = (int) (center.getX() + distance * Math.cos(angle));
            int z = (int) (center.getZ() + distance * Math.sin(angle));
            int y = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);

            BlockPos pos = new BlockPos(x, y, z);

            if (world.getBlockState(pos).isAir() && world.getBlockState(pos.up()).isAir()) {
                return pos;
            }
        }

        return null;
    }

    /**
     * Checks if player is looking at entity.
     */
    private static boolean isPlayerLookingAt(ServerPlayerEntity player, LivingEntity entity) {
        Vec3d playerLook = player.getRotationVector();
        Vec3d toEntity = entity.getPos().subtract(player.getPos()).normalize();

        double dot = playerLook.dotProduct(toEntity);

        // Player is looking at entity if dot product > 0.9 (narrow cone)
        return dot > 0.9;
    }

    /**
     * Makes entity vanish with effects.
     */
    private static void vanishEntity(ServerWorld world, LivingEntity entity, StalkingData data) {
        if (entity == null) {
            return;
        }

        BlockPos pos = entity.getBlockPos();

        // Play vanish sound
        world.playSound(null, pos, ModSounds.GLITCH, SoundCategory.HOSTILE, 0.5f, 0.5f);

        // Remove entity
        entity.discard();

        IsRealAnything.LOGGER.debug("Entity vanished at {}", pos);
    }

    /**
     * Gets vanish time based on phase.
     */
    private static int getVanishTime(int phase) {
        return switch (phase) {
            case 1 -> 600;  // 30 seconds
            case 2 -> 400;  // 20 seconds
            case 3 -> 300;  // 15 seconds
            case 4 -> 200;  // 10 seconds
            case 5 -> 100;  // 5 seconds
            default -> 400;
        };
    }

    /**
     * Sends creepy contextual messages to player.
     */
    private static void sendCreepyMessage(ServerPlayerEntity player) {
        String[] messages = {
                "I just want to be with you.",
                "I'm still  your ..friend. Do you still believe that?"
        };

        String message = messages[RANDOM.nextInt(messages.length)];
        player.sendMessage(
                Text.literal(message).formatted(Formatting.DARK_RED, Formatting.ITALIC),
                false
        );
    }

    /**
     * Stops all stalking behavior immediately.
     * Called when OVERLOOK_TRIGGERED becomes true.
     */
    public static void stopAllStalking() {
        STALKING_ENTITIES.clear();
        overlookTriggered = true;
        IsRealAnything.LOGGER.info("All stalking behavior stopped (OVERLOOK_TRIGGERED)");
    }

    /**
     * Resets stalking system.
     */
    public static void reset() {
        STALKING_ENTITIES.clear();
        overlookTriggered = false;
        globalTick = 0;
        IsRealAnything.LOGGER.info("Stalking system reset");
    }

    /**
     * Sets OVERLOOK_TRIGGERED state.
     */
    public static void setOverlookTriggered(boolean triggered) {
        overlookTriggered = triggered;
        if (triggered) {
            stopAllStalking();
        }
    }

    /**
     * Gets current number of stalking entities.
     */
    public static int getActiveStalkingCount() {
        return STALKING_ENTITIES.size();
    }
}