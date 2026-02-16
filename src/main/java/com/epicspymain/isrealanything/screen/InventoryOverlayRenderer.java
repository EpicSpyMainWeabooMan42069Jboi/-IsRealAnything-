package com.epicspymain.isrealanything.screen;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

import java.util.Random;

/**
 * Applies visual glitch effects over inventory screens
 */
public class InventoryOverlayRenderer {
    private static boolean isActive = false;
    private static int effectType = 0; // 0=none, 1=invert, 2=darken, 3=glitch
    private static float intensity = 0.5f;
    private static long startTime = 0;
    private static final Random random = new Random();

    public static void register() {
        HudRenderCallback.EVENT.register(InventoryOverlayRenderer::render);
    }

    private static void render(DrawContext context, RenderTickCounter tickCounter) {
        if (!isActive) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen == null || !(client.currentScreen instanceof HandledScreen)) {
            return;
        }

        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        long elapsed = System.currentTimeMillis() - startTime;

        switch (effectType) {
            case 1: // Color inversion flash
                if (elapsed % 500 < 250) {
                    int color = 0x80FFFFFF; // Semi-transparent white
                    context.fill(0, 0, width, height, color);
                }
                break;

            case 2: // Darkening pulse
                float pulse = (float) Math.sin(elapsed / 200.0) * 0.5f + 0.5f;
                int alpha = (int) (pulse * intensity * 200);
                context.fill(0, 0, width, height, (alpha << 24));
                break;

            case 3: // Random glitch lines
                if (random.nextFloat() < 0.3f) {
                    for (int i = 0; i < 5; i++) {
                        int y = random.nextInt(height);
                        int lineHeight = random.nextInt(3) + 1;
                        int offset = random.nextInt(20) - 10;
                        context.fill(offset, y, width + offset, y + lineHeight, 0xFFFF0000);
                    }
                }
                break;

            case 4: // Scan lines
                for (int y = 0; y < height; y += 4) {
                    context.fill(0, y, width, y + 2, 0x40000000);
                }
                break;
        }
    }

    public static void activate(int type, float effectIntensity) {
        isActive = true;
        effectType = type;
        intensity = effectIntensity;
        startTime = System.currentTimeMillis();
    }

    public static void deactivate() {
        isActive = false;
        effectType = 0;
    }

    public static boolean isActive() {
        return isActive;
    }
}
