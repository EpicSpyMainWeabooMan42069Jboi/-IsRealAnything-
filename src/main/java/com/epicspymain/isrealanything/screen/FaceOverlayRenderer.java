package com.epicspymain.isrealanything.screen;

import com.epicspymain.isrealanything.event.MyBeautifulFaceEvent;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;

public class FaceOverlayRenderer {

    // ✅ CORRECT - relative to assets/isrealanything/
    private static final Identifier FACE_TEXTURE_ID = Identifier.of("isrealanything", "textures/misc/mybeautifulface.png");

    private static boolean overlayActive = false;
    private static Identifier faceTexture = null;
    private static int closeness = 0; // 0-1000

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (overlayActive) {
                renderFaceOverlay(drawContext);
            }
        });
    }

    public static void toggleOverlay(boolean active, int closenessLevel) {
        overlayActive = active;
        closeness = closenessLevel;

        if (active && faceTexture == null) {
            faceTexture = getOrLoadTexture();
        }
    }

    private static void renderFaceOverlay(DrawContext context) {
        if (faceTexture == null) return;

        MinecraftClient client = MinecraftClient.getInstance();
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        // Scale from 10% to 100% based on closeness
        float scale = 0.1f + (closeness / 1000.0f) * 0.9f;

        int width = (int)(screenWidth * scale);
        int height = (int)(screenHeight * scale);

        // Center on screen
        int x = (screenWidth - width) / 2;
        int y = (int)((screenHeight - height) / 2 - (closeness / 10.0f));

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        float alpha = Math.min(1.0f, closeness / 500.0f);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);

        context.drawTexture(faceTexture, x, y, 0, 0, width, height, width, height);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }

    private static Identifier getOrLoadTexture() {
        try {

            InputStream stream = FaceOverlayRenderer.class.getResourceAsStream(
                    "/assets/isrealanything/textures/misc/mybeautifulface.png"
            );

            if (stream != null) {
                NativeImage image = NativeImage.read(stream);
                NativeImageBackedTexture texture = new NativeImageBackedTexture(image);
                MinecraftClient.getInstance().getTextureManager()
                        .registerTexture(FACE_TEXTURE_ID, texture);
                return FACE_TEXTURE_ID;
            }
        } catch (IOException e) {
            // Silent fail - use fallback
        }

        // Fallback if texture not found
        return Identifier.of("minecraft", "textures/misc/unknown_pack.png");
    }

    public static void tick(MinecraftClient client) {
        if (client.player != null) {
            // ✅ Fixed double dot typo
            boolean active = MyBeautifulFaceEvent.isFaceActive(client.player.getUuid());
            int currentCloseness = MyBeautifulFaceEvent.getFaceCloseness(client.player.getUuid());
            toggleOverlay(active, currentCloseness);
        }
    }
}
