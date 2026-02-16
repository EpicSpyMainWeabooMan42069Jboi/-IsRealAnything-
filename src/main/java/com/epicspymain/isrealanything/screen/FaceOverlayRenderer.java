package com.epicspymain.isrealanything.screen;

import com.epicspymain.isrealanything.events.MyBeautifulFaceEvent;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;


public class FaceOverlayRenderer {
    
    private static boolean overlayActive = false;
    private static Identifier faceTexture = null;
    private static int closeness = 0; // 0-1000
    

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (overlayActive) {
                renderFaceOverlay(drawContext, tickDelta);
            }
        });
    }
    

    public static void toggleOverlay(boolean active, int closenessLevel) {
        overlayActive = active;
        closeness = closenessLevel;
        
        // Load texture if not already loaded
        if (active && faceTexture == null) {
            faceTexture = getOrLoadTexture();
        }
    }
    

    private static void renderFaceOverlay(DrawContext context, float tickDelta) {
        if (faceTexture == null) {
            return;
        }
        
        MinecraftClient client = MinecraftClient.getInstance();
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        
        // Calculate size based on closeness (0-1000)
        // At 0: Small in distance
        // At 1000: Full screen
        float scale = 0.1f + (closeness / 1000.0f) * 0.9f; // 10% to 100%
        
        int width = (int)(screenWidth * scale);
        int height = (int)(screenHeight * scale);
        
        // Center on screen, but move down as it gets closer
        int x = (screenWidth - width) / 2;
        int y = (int)((screenHeight - height) / 2 - (closeness / 10.0f)); // Move up as closer
        
        // Render with transparency
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        
        float alpha = Math.min(1.0f, closeness / 500.0f); // Fade in
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        
        context.drawTexture(faceTexture, x, y, 0, 0, width, height, width, height);
        
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }
    

    private static Identifier getOrLoadTexture() {
        Identifier id = new Identifier("isrealanything", "textures/misc/MyBeautifulFace.png");
        
        try {
            InputStream stream = FaceOverlayRenderer.class.getResourceAsStream(
                "/assets/isrealanything/textures/misc/MyBeautifulFace.png"
            );
            
            if (stream != null) {
                NativeImage image = NativeImage.read(stream);
                NativeImageBackedTexture texture = new NativeImageBackedTexture(image);
                
                MinecraftClient.getInstance().getTextureManager()
                    .registerTexture(id, texture);
                
                return id;
            }
        } catch (IOException e) {
            // Failed to load, use placeholder
        }
        
        return new Identifier("minecraft", "textures/misc/unknown_pack.png");
    }
    

    public static void tick(MinecraftClient client) {
        if (client.player != null) {
            boolean active = MyBeautifulFaceEvent..isFaceActive(client.player.getUuid());
            int currentCloseness = MyBeautifulFaceEvent.getFaceCloseness(client.player.getUuid());
            
            toggleOverlay(active, currentCloseness);
        }
    }
}
