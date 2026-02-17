package com.epicspymain.isrealanything.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

/**
 * Renderer: SkyImage - Renders image in sky
 * Used for MyBeautifulFace event
 * Image appears in sky and tracks player looking
 */
public class SkyImageRenderer {

    private static boolean renderEnabled = false;
    private static final Identifier SKY_TEXTURE = Identifier.of("isrealanything", "textures/sky/mybeautifulface.png");
    private static float imageScale = 50.0f;
    private static float imageDistance = 200.0f;
    private static float imageAlpha = 1.0f;

    /**
     * Register sky image renderer
     */
    public static void register() {
        WorldRenderEvents.AFTER_SKY.register(context -> {
            if (renderEnabled) {
                renderSkyImage(context.matrixStack(), context.tickCounter().getTickDelta(false));
            }
        });
    }

    /**
     * Toggle sky texture rendering
     */
    public static void enable(float scale, float distance, float alpha) {
        renderEnabled = true;
        imageScale = scale;
        imageDistance = distance;
        imageAlpha = alpha;
    }

    public static void enable() {
        enable(50.0f, 200.0f, 1.0f);
    }

    public static void disable() {
        renderEnabled = false;
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

        // Position image to always face player
        float yaw = client.player.getYaw(tickDelta);
        float pitch = client.player.getPitch(tickDelta);

        // Rotate to face player
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-yaw + 180));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-pitch));

        // Move to sky position (above and in front of player)
        matrices.translate(0, 0, -imageDistance);

        // Setup rendering
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, SKY_TEXTURE);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, imageAlpha);

        RenderSystem.depthMask(false);
        RenderSystem.disableCull();

        // Render quad with texture
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        // Define quad vertices (centered)
        float size = imageScale;
        buffer.vertex(matrix, -size, -size, 0).texture(0, 0);
        buffer.vertex(matrix, -size, size, 0).texture(0, 1);
        buffer.vertex(matrix, size, size, 0).texture(1, 1);
        buffer.vertex(matrix, size, -size, 0).texture(1, 0);

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        matrices.pop();
    }

    /**
     * Check if player is looking up at the sky
     */
    public static boolean isPlayerLookingAtSky() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return false;
        }

        // Check if player is looking up (pitch < -30)
        return client.player.getPitch() < -30.0f;
    }

    public static boolean isEnabled() {
        return renderEnabled;
    }
}