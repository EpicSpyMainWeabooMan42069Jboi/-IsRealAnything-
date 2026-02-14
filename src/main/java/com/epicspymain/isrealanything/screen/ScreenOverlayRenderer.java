package com.epicspymain.isrealanything.screen;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

/**
 * Base overlay system for full-screen effects
 * Coordinates black screens, white screens, frozen screens, and other full overlays
 */
public class ScreenOverlayRenderer {
    private static OverlayType currentOverlay = OverlayType.NONE;
    private static float overlayAlpha = 0.0f;
    private static long overlayStartTime = 0;
    private static long overlayDuration = 0;
    private static boolean fadeIn = false;
    private static boolean fadeOut = false;
    private static long fadeDuration = 1000;

    public enum OverlayType {
        NONE,
        BLACK,
        WHITE,
        FROZEN,
        RED_FLASH,
        STATIC
    }

    public static void register() {
        HudRenderCallback.EVENT.register(ScreenOverlayRenderer::render);
    }

    private static void render(DrawContext context, RenderTickCounter tickCounter) {
        if (currentOverlay == OverlayType.NONE) return;

        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - overlayStartTime;

        // Calculate alpha based on fade settings
        float alpha = overlayAlpha;
        if (fadeIn && elapsed < fadeDuration) {
            alpha = (float) elapsed / fadeDuration;
        } else if (fadeOut && overlayDuration > 0 && elapsed > overlayDuration - fadeDuration) {
            long fadeElapsed = elapsed - (overlayDuration - fadeDuration);
            alpha = 1.0f - ((float) fadeElapsed / fadeDuration);
        }

        alpha = Math.max(0.0f, Math.min(1.0f, alpha));

        // Auto-end overlay after duration
        if (overlayDuration > 0 && elapsed > overlayDuration) {
            stop();
            return;
        }

        switch (currentOverlay) {
            case BLACK:
                renderBlackScreen(context, width, height, alpha);
                break;
            case WHITE:
                renderWhiteScreen(context, width, height, alpha);
                break;
            case FROZEN:
                FrozenOverlayRenderer.render(context, tickCounter);
                break;
            case RED_FLASH:
                renderRedFlash(context, width, height, alpha, elapsed);
                break;
            case STATIC:
                renderStatic(context, width, height, alpha);
                break;
        }
    }

    private static void renderBlackScreen(DrawContext context, int width, int height, float alpha) {
        int color = ((int) (alpha * 255) << 24) | 0x000000;
        context.fill(0, 0, width, height, color);
    }

    private static void renderWhiteScreen(DrawContext context, int width, int height, float alpha) {
        int color = ((int) (alpha * 255) << 24) | 0xFFFFFF;
        context.fill(0, 0, width, height, color);
    }

    private static void renderRedFlash(DrawContext context, int width, int height, float alpha, long elapsed) {
        // Pulsing red flash
        float pulse = (float) Math.sin(elapsed / 100.0) * 0.5f + 0.5f;
        int color = ((int) (alpha * pulse * 200) << 24) | 0xFF0000;
        context.fill(0, 0, width, height, color);
    }

    private static void renderStatic(DrawContext context, int width, int height, float alpha) {
        // TV static effect - random noise
        java.util.Random random = new java.util.Random();
        for (int x = 0; x < width; x += 2) {
            for (int y = 0; y < height; y += 2) {
                int brightness = random.nextInt(256);
                int color = ((int) (alpha * 255) << 24) | (brightness << 16) | (brightness << 8) | brightness;
                context.fill(x, y, x + 2, y + 2, color);
            }
        }
    }

    // === Public API Methods ===

    public static void executeBlackScreen(long durationMs, boolean fadeInEffect, boolean fadeOutEffect) {
        currentOverlay = OverlayType.BLACK;
        overlayAlpha = fadeInEffect ? 0.0f : 1.0f;
        overlayStartTime = System.currentTimeMillis();
        overlayDuration = durationMs;
        fadeIn = fadeInEffect;
        fadeOut = fadeOutEffect;
    }

    public static void executeWhiteScreen(long durationMs, boolean fadeInEffect, boolean fadeOutEffect) {
        currentOverlay = OverlayType.WHITE;
        overlayAlpha = fadeInEffect ? 0.0f : 1.0f;
        overlayStartTime = System.currentTimeMillis();
        overlayDuration = durationMs;
        fadeIn = fadeInEffect;
        fadeOut = fadeOutEffect;
    }

    public static void executeFrozenScreen(long durationMs) {
        currentOverlay = OverlayType.FROZEN;
        overlayStartTime = System.currentTimeMillis();
        overlayDuration = durationMs;
        FrozenOverlayRenderer.captureScreen();
        FrozenOverlayRenderer.activate();
    }

    public static void executeRedFlash(long durationMs) {
        currentOverlay = OverlayType.RED_FLASH;
        overlayAlpha = 1.0f;
        overlayStartTime = System.currentTimeMillis();
        overlayDuration = durationMs;
        fadeIn = false;
        fadeOut = true;
    }

    public static void executeStatic(long durationMs) {
        currentOverlay = OverlayType.STATIC;
        overlayAlpha = 1.0f;
        overlayStartTime = System.currentTimeMillis();
        overlayDuration = durationMs;
        fadeIn = false;
        fadeOut = false;
    }

    public static void stop() {
        currentOverlay = OverlayType.NONE;
        overlayAlpha = 0.0f;
        FrozenOverlayRenderer.deactivate();
    }

    public static boolean isActive() {
        return currentOverlay != OverlayType.NONE;
    }

    public static OverlayType getCurrentOverlay() {
        return currentOverlay;
    }

    public static void setFadeDuration(long ms) {
        fadeDuration = ms;
    }
}
