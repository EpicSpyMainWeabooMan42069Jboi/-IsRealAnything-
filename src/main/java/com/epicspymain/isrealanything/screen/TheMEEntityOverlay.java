package com.epicspymain.isrealanything.screen;

import com.epicspymain.isrealanything.entity.ModEntities;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

/**
 * Overlay effects when TheME or TheOtherME entities are nearby or watching the player
 * Creates visual distortion and vignette effects based on entity proximity
 */
public class TheMEEntityOverlay {
    private static boolean isActive = false;
    private static float intensity = 0.0f;
    private static boolean isBeingWatched = false;
    private static long lastUpdateTime = 0;
    private static final long UPDATE_INTERVAL = 100; // Update every 100ms

    public static void register() {
        HudRenderCallback.EVENT.register(TheMEEntityOverlay::render);
    }

    private static void render(DrawContext context, RenderTickCounter tickCounter) {
        if (!isActive) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime > UPDATE_INTERVAL) {
            updateIntensity(client);
            lastUpdateTime = currentTime;
        }

        if (intensity <= 0.0f) return;

        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        // Vignette effect - darkens edges when entities are near
        renderVignette(context, width, height, intensity);

        // Screen distortion when being watched
        if (isBeingWatched) {
            renderWatchingEffect(context, width, height, currentTime);
        }

        // Red tint at high intensity
        if (intensity > 0.7f) {
            int alpha = (int) ((intensity - 0.7f) * 3.0f * 100);
            alpha = Math.min(alpha, 150);
            int color = (alpha << 24) | 0xFF0000;
            context.fill(0, 0, width, height, color);
        }
    }

    private static void updateIntensity(MinecraftClient client) {
        if (client.world == null || client.player == null) {
            intensity = 0.0f;
            isBeingWatched = false;
            return;
        }

        Vec3d playerPos = client.player.getPos();
        Box searchBox = Box.of(playerPos, 32, 32, 32);

        // Find nearby TheME or TheOtherME entities
        List<Entity> nearbyEntities = client.world.getOtherEntities(client.player, searchBox, entity -> {
            return entity.getType() == ModEntities.THEME_ENTITY || 
                   entity.getType() == ModEntities.THEOTHERME_ENTITY;
        });

        if (nearbyEntities.isEmpty()) {
            // Fade out
            intensity = Math.max(0.0f, intensity - 0.05f);
            isBeingWatched = false;
            return;
        }

        // Calculate intensity based on closest entity
        float minDistance = Float.MAX_VALUE;
        boolean watching = false;

        for (Entity entity : nearbyEntities) {
            float distance = (float) entity.distanceTo(client.player);
            minDistance = Math.min(minDistance, distance);

            // Check if entity is looking at player
            Vec3d entityLook = entity.getRotationVec(1.0f);
            Vec3d toPlayer = playerPos.subtract(entity.getPos()).normalize();
            double dot = entityLook.dotProduct(toPlayer);
            
            if (dot > 0.9) { // Entity is looking at player
                watching = true;
            }
        }

        isBeingWatched = watching;

        // Calculate intensity (1.0 at distance 0, 0.0 at distance 32)
        float targetIntensity = 1.0f - (minDistance / 32.0f);
        targetIntensity = Math.max(0.0f, Math.min(1.0f, targetIntensity));

        if (isBeingWatched) {
            targetIntensity *= 1.5f; // Amplify when being watched
            targetIntensity = Math.min(1.0f, targetIntensity);
        }

        // Smooth transition
        if (targetIntensity > intensity) {
            intensity = Math.min(targetIntensity, intensity + 0.05f);
        } else {
            intensity = Math.max(targetIntensity, intensity - 0.02f);
        }
    }

    private static void renderVignette(DrawContext context, int width, int height, float strength) {
        int centerX = width / 2;
        int centerY = height / 2;
        float maxDist = (float) Math.sqrt(centerX * centerX + centerY * centerY);

        // Draw vignette effect from edges
        int samples = 20;
        for (int i = 0; i < samples; i++) {
            float ratio = (float) i / samples;
            int dist = (int) (maxDist * ratio);
            int alpha = (int) (strength * (1.0f - ratio) * 200);
            alpha = Math.min(alpha, 255);

            // Top/Bottom
            context.fillGradient(0, 0, width, dist, (alpha << 24), 0x00000000);
            context.fillGradient(0, height - dist, width, height, 0x00000000, (alpha << 24));

            // Left/Right
            context.fillGradient(0, dist, dist, height - dist, (alpha << 24), 0x00000000);
            context.fillGradient(width - dist, dist, width, height - dist, 0x00000000, (alpha << 24));
        }
    }

    private static void renderWatchingEffect(DrawContext context, int width, int height, long time) {
        // Pulsing border effect
        float pulse = (float) Math.sin(time / 200.0) * 0.5f + 0.5f;
        int alpha = (int) (pulse * 150);
        int borderColor = (alpha << 24) | 0xFF0000;

        // Draw pulsing border
        int borderWidth = 4;
        context.fill(0, 0, width, borderWidth, borderColor);
        context.fill(0, height - borderWidth, width, height, borderColor);
        context.fill(0, borderWidth, borderWidth, height - borderWidth, borderColor);
        context.fill(width - borderWidth, borderWidth, width, height - borderWidth, borderColor);

        // Random screen tears when being watched intensely
        if (intensity > 0.8f && Math.random() < 0.1) {
            java.util.Random random = new java.util.Random();
            int y = random.nextInt(height);
            int offset = random.nextInt(10) - 5;
            context.fill(offset, y, width + offset, y + 2, 0xFFFFFFFF);
        }
    }

    public static void activate() {
        isActive = true;
    }

    public static void deactivate() {
        isActive = false;
        intensity = 0.0f;
        isBeingWatched = false;
    }

    public static boolean isActive() {
        return isActive;
    }

    public static float getIntensity() {
        return intensity;
    }

    public static boolean isBeingWatched() {
        return isBeingWatched;
    }
}
