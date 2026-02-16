package com.epicspymain.isrealanything.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.io.IOException;
import java.io.InputStream;

/**
 * Renderer 2: SkyImage - Renders image in sky
 * Used for MyBeautifulFace event
 * Image appears in sky and tracks player looking
 */
public class SkyImageRenderer {
    
    private static boolean renderEnabled = false;
    private static Identifier skyTexture = null;
    private static float imageScale = 1.0f;
    private static float imageDistance = 100.0f;
    
    /**
     * Register sky image renderer
     */
    public static void register() {
        WorldRenderEvents.AFTER_SKY.register(context -> {
            if (renderEnabled && skyTexture != null) {
                renderSkyImage(context.matrixStack(), context.tickDelta());
            }
        });
    }
    
    /**
     * Toggle sky texture rendering
     */
    public static void toggleTexture(boolean enabled, float scale, float distance) {
        renderEnabled = enabled;
        imageScale = scale;
        imageDistance = distance;
        
        if (enabled && skyTexture == null) {
            skyTexture = loadSkyTexture();
        }
    }
    
    /**
     * Render image in sky
     */
    private static void renderSkyImage(MatrixStack matrices, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        if (client.player == null) {
            return;
        }
        
        matrices.push();
        
        // Position in sky above player
        Vec3d playerPos = client.player.getPos();
        
        // Face towards player
        float yaw = client.player.getYaw();
        float pitch = client.player.getPitch();
        
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-yaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(pitch));
        
        // Move to sky position
        matrices.translate(0, imageDistance, 0);
        
        // Scale based on closeness
        matrices.scale(imageScale, imageScale, imageScale);
        
        // Setup rendering
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, skyTexture);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        
        // Render quad with texture
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        
        // Define quad vertices
        float size = 50.0f * imageScale;
        buffer.vertex(matrix, -size, -size, 0).texture(0, 0).next();
        buffer.vertex(matrix, -size, size, 0).texture(0, 1).next();
        buffer.vertex(matrix, size, size, 0).texture(1, 1).next();
        buffer.vertex(matrix, size, -size, 0).texture(1, 0).next();
        
        BufferRenderer.drawWithGlobalProgram(buffer.end());
        
        RenderSystem.disableBlend();
        
        matrices.pop();
    }
    
    /**
     * Load sky texture
     */
    private static Identifier loadSkyTexture() {
        Identifier id = new Identifier("isrealanything", "textures/sky/mybeautifulface.png");
        
        try {
            InputStream stream = SkyImageRenderer.class.getResourceAsStream(
                "/assets/isrealanything/textures/sky/mybeautifulface.png"
            );
            
            if (stream != null) {
                NativeImage image = NativeImage.read(stream);
                NativeImageBackedTexture texture = new NativeImageBackedTexture(image);
                
                MinecraftClient.getInstance().getTextureManager()
                    .registerTexture(id, texture);
                
                return id;
            }
        } catch (IOException e) {
            // Failed to load
        }
        
        return new Identifier("minecraft", "textures/misc/unknown_pack.png");
    }
    
    /**
     * Check if player is looking at the image
     */
    public static boolean isPlayerLookingAtImage(MinecraftClient client) {
        if (client.player == null) {
            return false;
        }
        
        // Check if player is looking up (pitch < -45)
        return client.player.getPitch() < -45.0f;
    }
}
