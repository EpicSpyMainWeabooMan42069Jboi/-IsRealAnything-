package com.epicspymain.isrealanything.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

/**
 * Renderer 3: FrozenOverlay - Frozen screenshot overlay
 * Captures current screen and displays as full-screen overlay
 * Creates illusion of game freezing
 */
public class FrozenOverlayRenderer {

    private static boolean frozen = false;
    private static Identifier frozenTexture = null;
    private static int frozenTicks = 0;
    private static int maxFrozenTicks = 0;

    /**
     * Register renderer
     */
    public static void register() {
        HudRenderCallback.EVENT.register(FrozenOverlayRenderer::render);
    }

    private static void render(DrawContext drawContext, RenderTickCounter tickDelta) {
        if (frozen) {
            renderFrozenScreen(drawContext);
            frozenTicks--;

            if (frozenTicks <= 0) {
                unfreeze();
            }
        }
    }

    /**
     * Freeze screen with screenshot
     */
    public static void freeze(int ticks) {
        frozen = true;
        frozenTicks = ticks;
        maxFrozenTicks = ticks;

        // Capture current screen
        captureScreen();
    }

    /**
     * Activate frozen state (called by ScreenOverlayRenderer)
     */
    public static void activate() {
        frozen = true;
    }

    /**
     * Unfreeze screen
     */
    public static void unfreeze() {
        frozen = false;
        frozenTicks = 0;

        // Clear texture
        if (frozenTexture != null) {
            MinecraftClient.getInstance().getTextureManager().destroyTexture(frozenTexture);
            frozenTexture = null;
        }
    }

    /**
     * Deactivate (alias for unfreeze)
     */
    public static void deactivate() {
        unfreeze();
    }

    /**
     * Capture current screen as texture
     * Made public so ScreenOverlayRenderer can call it
     */
    public static void captureScreen() {
        MinecraftClient client = MinecraftClient.getInstance();

        try {
            int width = client.getWindow().getFramebufferWidth();
            int height = client.getWindow().getFramebufferHeight();

            NativeImage screenshot = new NativeImage(width, height, false);

            // Read pixels from framebuffer
            RenderSystem.bindTexture(client.getFramebuffer().getColorAttachment());
            screenshot.loadFromTextureImage(0, false);
            screenshot.mirrorVertically();

            // Create texture
            frozenTexture = Identifier.of("isrealanything", "frozen_screen");
            NativeImageBackedTexture texture = new NativeImageBackedTexture(screenshot);

            client.getTextureManager().registerTexture(frozenTexture, texture);

        } catch (Exception e) {
            // Failed to capture, use black screen
            frozen = false;
        }
    }

    /**
     * Render frozen screen overlay
     */
    private static void renderFrozenScreen(DrawContext context) {
        if (frozenTexture == null) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        // Render full screen
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        context.drawTexture(frozenTexture, 0, 0, 0, 0, width, height, width, height);

        // Add slight vignette effect
        float alpha = 0.3f;
        context.fill(0, 0, width, height, (int)(alpha * 255) << 24);

        RenderSystem.disableBlend();
    }

    /**
     * Check if currently frozen
     */
    public static boolean isFrozen() {
        return frozen;
    }
}