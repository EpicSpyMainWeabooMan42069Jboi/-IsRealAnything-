package com.epicspymain.isrealanything.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.Random;


public class GlitchOverlay {
    
    private static boolean glitchActive = false;
    private static int glitchTicks = 0;
    private static float glitchIntensity = 0.5f;
    private static final Random random = new Random();
    

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (glitchActive) {
                renderGlitch(drawContext);
                glitchTicks--;
                
                if (glitchTicks <= 0) {
                    glitchActive = false;
                }
            }
        });
    }
    

    public static void trigger(int ticks, float intensity) {
        glitchActive = true;
        glitchTicks = ticks;
        glitchIntensity = Math.min(1.0f, Math.max(0.0f, intensity));
    }
    

    private static void renderGlitch(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        
        // Random glitch lines
        if (random.nextFloat() < glitchIntensity) {
            renderGlitchLines(context, width, height);
        }
        
        // Color shift
        if (random.nextFloat() < glitchIntensity * 0.5f) {
            renderColorShift(context, width, height);
        }
        
        // Screen tear effect
        if (random.nextFloat() < glitchIntensity * 0.3f) {
            renderScreenTear(context, width, height);
        }
        
        // Static noise
        if (random.nextFloat() < glitchIntensity * 0.7f) {
            renderStaticNoise(context, width, height);
        }
        
        RenderSystem.disableBlend();
    }
    
    /**
     * Render horizontal glitch lines
     */
    private static void renderGlitchLines(DrawContext context, int width, int height) {
        int lineCount = (int)(5 * glitchIntensity);
        
        for (int i = 0; i < lineCount; i++) {
            int y = random.nextInt(height);
            int lineHeight = 1 + random.nextInt(3);
            int lineWidth = width / 4 + random.nextInt(width / 2);
            int x = random.nextInt(width - lineWidth);
            
            // Random color
            int color = random.nextInt(0xFFFFFF) | 0x80000000; // Semi-transparent
            
            context.fill(x, y, x + lineWidth, y + lineHeight, color);
        }
    }
    
    /**
     * Render color shift effect
     */
    private static void renderColorShift(DrawContext context, int width, int height) {
        int shiftAmount = (int)(10 * glitchIntensity);
        int shiftX = random.nextInt(shiftAmount * 2) - shiftAmount;
        
        // Red shift
        RenderSystem.setShaderColor(1.0f, 0.0f, 0.0f, 0.3f * glitchIntensity);
        context.fill(shiftX, 0, width + shiftX, height, 0x40FF0000);
        
        // Cyan shift
        RenderSystem.setShaderColor(0.0f, 1.0f, 1.0f, 0.3f * glitchIntensity);
        context.fill(-shiftX, 0, width - shiftX, height, 0x4000FFFF);
        
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    /**
     * Render screen tear effect
     */
    private static void renderScreenTear(DrawContext context, int width, int height) {
        int tearY = random.nextInt(height);
        int tearHeight = height / 4 + random.nextInt(height / 4);
        int offset = (int)(20 * glitchIntensity * (random.nextBoolean() ? 1 : -1));
        
        // Simulate tear by drawing offset section
        context.fill(0, tearY, width, tearY + tearHeight, 0x80000000);
    }
    
    /**
     * Render static noise
     */
    private static void renderStaticNoise(DrawContext context, int width, int height) {
        int noiseCount = (int)(50 * glitchIntensity);
        
        for (int i = 0; i < noiseCount; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int size = 1 + random.nextInt(3);
            
            int color = random.nextBoolean() ? 0x80FFFFFF : 0x80000000;
            
            context.fill(x, y, x + size, y + size, color);
        }
    }
    
    /**
     * Stop glitch effect
     */
    public static void stop() {
        glitchActive = false;
        glitchTicks = 0;
    }
}
