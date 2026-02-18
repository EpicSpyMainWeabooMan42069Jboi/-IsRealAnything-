package com.epicspymain.isrealanything.ai;
import com.epicspymain.isrealanything.IsRealAnything;
import net.minecraft.world.Heightmap;
import com.epicspymain.isrealanything.entity.client.TheMEEntitySpawner;
import com.epicspymain.isrealanything.entity.client.TheOtherMEEntitySpawner;
import com.epicspymain.isrealanything.sound.ModSounds;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;

public class StalkingController {

    private static int currentIntensity = 0;
    private static boolean overlookTriggered = false;
    private static int ticksSinceLastPeek = 0;
    private static int ticksSinceLastVanish = 0;

    public static final int INTENSITY_NONE = 0;
    public static final int INTENSITY_LOW = 1;
    public static final int INTENSITY_MEDIUM = 2;
    public static final int INTENSITY_HIGH = 3;
    public static final int INTENSITY_EXTREME = 4;
    public static final int INTENSITY_MAXIMUM = 5;

    public static void setIntensity(int level) {
        if (overlookTriggered) {
            IsRealAnything.LOGGER.warn("Cannot set intensity - OVERLOOK_TRIGGERED is active");
            return;
        }

        int oldIntensity = currentIntensity;
        currentIntensity = Math.max(0, Math.min(5, level));

        if (currentIntensity != oldIntensity) {
            IsRealAnything.LOGGER.info("Stalking intensity changed: {} -> {}", oldIntensity, currentIntensity);
        }

        if (currentIntensity == 0) {
            StalkingBehavior.reset();
        }
    }

    public static int getIntensity() {
        return overlookTriggered ? 0 : currentIntensity;
    }

    public static void increaseIntensity() {
        setIntensity(currentIntensity + 1);
    }

    public static void decreaseIntensity() {
        setIntensity(currentIntensity - 1);
    }

    public static void triggerPeek(ServerWorld world, ServerPlayerEntity player, int distance) {
        if (overlookTriggered) return;

        if (ticksSinceLastPeek < 200) {
            IsRealAnything.LOGGER.debug("Peek on cooldown");
            return;
        }

        BlockPos playerPos = player.getBlockPos();
        BlockPos peekPos = findPeekPosition(world, playerPos, distance);

        if (peekPos != null) {
            TheMEEntitySpawner.spawnAt(world, peekPos);
            world.playSound(null, peekPos, ModSounds.SCREAM, SoundCategory.AMBIENT, 0.4f, 0.9f);
            ticksSinceLastPeek = 0;
            IsRealAnything.LOGGER.debug("Peek triggered at distance {}", distance);
        }
    }

    public static void triggerBehindPlayer(ServerWorld world, ServerPlayerEntity player) {
        if (overlookTriggered) return;

        BlockPos playerPos = player.getBlockPos();
        float playerYaw = player.getYaw();

        int distance = switch (currentIntensity) {
            case 1, 2 -> 8;
            case 3 -> 5;
            case 4, 5 -> 3;
            default -> 10;
        };

        if (currentIntensity >= 4) {
            TheOtherMEEntitySpawner.spawnBehindPlayer(world, playerPos, playerYaw, distance);
        }

        IsRealAnything.LOGGER.debug("Behind player trigger (distance: {})", distance);
    }

    public static void triggerVanish() {
        if (overlookTriggered) return;
        ticksSinceLastVanish = 0;
        IsRealAnything.LOGGER.debug("Vanish trigger");
    }

    public static void triggerGroupSpawn(ServerWorld world, ServerPlayerEntity player, int count) {
        if (overlookTriggered) return;

        if (currentIntensity < 3) {
            IsRealAnything.LOGGER.debug("Intensity too low for group spawn");
            return;
        }

        BlockPos playerPos = player.getBlockPos();
        int spawned = TheOtherMEEntitySpawner.spawnGroup(world, playerPos, count, 20);
        IsRealAnything.LOGGER.info("Group spawn triggered: {} entities", spawned);
    }

    public static void triggerTimeBased(ServerWorld world, ServerPlayerEntity player) {
        if (overlookTriggered) return;
        if (currentIntensity == 0) return;

        boolean isNight = world.isNight();

        if (isNight) {
            if (world.getRandom().nextFloat() < 0.1f * currentIntensity) {
                triggerBehindPlayer(world, player);
            }
        } else {
            if (world.getRandom().nextFloat() < 0.05f * currentIntensity) {
                triggerPeek(world, player, 32);
            }
        }
    }

    public static void triggerLocationBased(ServerWorld world, ServerPlayerEntity player) {
        if (overlookTriggered) return;
        if (currentIntensity == 0) return;

        BlockPos pos = player.getBlockPos();
        boolean isUnderground = pos.getY() < 50;
        boolean isDark = world.getLightLevel(pos) < 7;

        if (isUnderground || isDark) {
            if (world.getRandom().nextFloat() < 0.15f * currentIntensity) {
                triggerPeek(world, player, 16);
            }
        }
    }

    public static void tick() {
        if (overlookTriggered) return;
        ticksSinceLastPeek++;
        ticksSinceLastVanish++;
    }

    public static void setOverlookTriggered(boolean triggered) {
        overlookTriggered = triggered;

        if (triggered) {
            currentIntensity = 0;
            StalkingBehavior.stopAllStalking();
            IsRealAnything.LOGGER.info("StalkingController: OVERLOOK_TRIGGERED set to true");
        }
    }

    public static boolean isOverlookTriggered() {
        return overlookTriggered;
    }

    public static void reset() {
        currentIntensity = 0;
        overlookTriggered = false;
        ticksSinceLastPeek = 0;
        ticksSinceLastVanish = 0;
        StalkingBehavior.reset();
        IsRealAnything.LOGGER.info("StalkingController reset");
    }

    private static BlockPos findPeekPosition(ServerWorld world, BlockPos center, int distance) {
        for (int attempt = 0; attempt < 15; attempt++) {
            double angle = world.getRandom().nextDouble() * 2 * Math.PI;
            int x = (int) (center.getX() + distance * Math.cos(angle));
            int z = (int) (center.getZ() + distance * Math.sin(angle));
            int y = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);

            BlockPos pos = new BlockPos(x, y, z);

            if (world.getBlockState(pos).isAir() &&
                    world.getBlockState(pos.up()).isAir() &&
                    world.getLightLevel(pos) < 10) {
                return pos;
            }
        }
        return null;
    }

    public static int getRecommendedIntensity(long gameDay) {
        if (gameDay <= 2) return INTENSITY_NONE;
        else if (gameDay <= 5) return INTENSITY_LOW;
        else if (gameDay <= 10) return INTENSITY_MEDIUM;
        else if (gameDay <= 20) return INTENSITY_HIGH;
        else if (gameDay <= 35) return INTENSITY_EXTREME;
        else return INTENSITY_MAXIMUM;
    }
}