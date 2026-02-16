package com.epicspymain.isrealanything.screen;

import com.epicspymain.isrealanything.sound.ModSounds;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.sound.SoundCategory;

/**
 * White flash jumpscare overlay triggered by TheME/TheOtherME entities
 * Creates sudden white flashes with optional sound effects for jumpscare moments
 */
public class TheMEEntityWhiteOverlay {
    private static boolean isActive = false;
    private static long flashStartTime = 0;
    private static long flashDuration = 500; // Default 500ms
    private static float maxAlpha = 1.0f;
    private static boolean playSound = true;
    private static boolean hasPlayedSound = false;

    public static void register() {
        HudRenderCallback.EVENT.register(TheMEEntityWhiteOverlay::render);
    }

    private static void render(DrawContext context, RenderTickCounter tickCounter) {
        if (!isActive) return;

        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - flashStartTime;

        if (elapsed > flashDuration) {
            deactivate();
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        // Play sound on first frame if enabled
        if (playSound && !hasPlayedSound && client.player != null) {
            client.player.playSoundToPlayer(
                ModSounds.GLITCH
                SoundCategory.AMBIENT,
                1.0f,
                1.0f
            );
            hasPlayedSound = true;
        }

        // Calculate alpha based on elapsed time (fade in and fade out)
        float alpha;
        long halfDuration = flashDuration / 2;
        
        if (elapsed < halfDuration) {
            // Fade in (very fast)
            alpha = (float) elapsed / halfDuration;
        } else {
            // Fade out
            long fadeElapsed = elapsed - halfDuration;
            alpha = 1.0f - ((float) fadeElapsed / halfDuration);
        }

        alpha *= maxAlpha;
        alpha = Math.max(0.0f, Math.min(1.0f, alpha));

        // Render white flash
        int color = ((int) (alpha * 255) << 24) | 0xFFFFFF;
        context.fill(0, 0, width, height, color);

        // Add slight screen shake effect at peak
        if (elapsed > halfDuration - 50 && elapsed < halfDuration + 50) {
            renderScreenShake(context, width, height, elapsed);
        }
    }

    private static void renderScreenShake(DrawContext context, int width, int height, long time) {
        // Visual representation of shake - add red/blue chromatic aberration
        java.util.Random random = new java.util.Random(time);
        int offsetX = random.nextInt(5) - 2;
        int offsetY = random.nextInt(5) - 2;

        // Draw colored borders to simulate chromatic aberration
        context.fill(offsetX, offsetY, width + offsetX, offsetY + 2, 0x80FF0000);
        context.fill(-offsetX, -offsetY, width - offsetX, -offsetY + 2, 0x800000FF);
    }

    // === Public API Methods ===

    public static void trigger() {
        trigger(500, 1.0f, true);
    }

    public static void trigger(long duration) {
        trigger(duration, 1.0f, true);
    }

    public static void trigger(long duration, float alpha, boolean sound) {
        isActive = true;
        flashStartTime = System.currentTimeMillis();
        flashDuration = duration;
        maxAlpha = alpha;
        playSound = sound;
        hasPlayedSound = false;
    }

    public static void triggerInstant() {
        trigger(200, 1.0f, true);
    }

    public static void triggerSilent() {
        trigger(500, 1.0f, false);
    }

    public static void triggerLong() {
        trigger(2000, 1.0f, true);
    }

    public static void deactivate() {
        isActive = false;
        hasPlayedSound = false;
    }

    public static boolean isActive() {
        return isActive;
    }

    /**
     * Creates multiple quick flashes for an intense effect
     */
    public static void triggerMultiFlash(int count, long delayBetween) {
        MinecraftClient client = MinecraftClient.getInstance();
        for (int i = 0; i < count; i++) {
            final int index = i;
            client.execute(() -> {
                new Thread(() -> {
                    try {
                        Thread.sleep(delayBetween * index);
                        trigger(300, 0.8f, index == 0); // Only play sound on first flash
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            });
        }
    }

    /**
     * Screen flash when entity appears suddenly
     */
    public static void entityAppearFlash() {
        trigger(800, 0.9f, true);
    }

    /**
     * Screen flash when entity disappears
     */
    public static void entityDisappearFlash() {
        trigger(400, 0.6f, false);
    }
}
