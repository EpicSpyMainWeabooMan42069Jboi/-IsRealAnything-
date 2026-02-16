package com.epicspymain.isrealanything.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_1044;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_4587;
import net.minecraft.class_9779;

public class FaceOverlayRenderer {
    public static boolean overlayVisible = false;

    private static final Map<String, class_2960> loadedTextures = new HashMap<>();

    private static final class_2960 FACE_IMAGE_TEXTURE = class_2960.method_60655("isrealanything", "textures/misc/MYBEAUTIFUL.png");

    public static void toggleOverlay(File image, Float red, Float green, Float blue, Float alpha, int imageWidth, int imageHeight) {
        overlayVisible = !overlayVisible;
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (overlayVisible)
                renderTopLayerOverlay(drawContext, image, red, green, blue, alpha, imageWidth, imageHeight);
        });
    }

    public static void renderTopLayerOverlay(class_332 drawContext, File image, Float red, Float green, Float blue, Float alpha, int faceImageWidth, int faceImageHeight) {
        class_310 client = class_310.method_1551();
        int screenWidth = client.method_22683().method_4486();
        int screenHeight = client.method_22683().method_4502();
        class_4587 matrices = drawContext.method_51448();
        matrices.method_22903();
        matrices.method_46416(0.0F, 0.0F, 1000.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.polygonOffset(-1.0F, -1.0F);
        RenderSystem.enablePolygonOffset();
        drawContext.method_25294(0, 0, screenWidth, screenHeight, -2147483648);
        int centerX = (screenWidth - faceImageWidth) / 2;
        int centerY = (screenHeight - faceImageHeight) / 2;
        renderOverlayContent(drawContext, screenWidth, screenHeight, image, "File", red, green, blue, alpha);
        renderOverlayContent(drawContext, centerX, centerY, faceImageWidth, faceImageHeight, FACE_IMAGE_TEXTURE, "Identifier", Float.valueOf(1.0F), Float.valueOf(1.0F), Float.valueOf(1.0F), Float.valueOf(1.0F));
        RenderSystem.disablePolygonOffset();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        matrices.method_22909();
    }

    public static void renderOverlayContent(class_332 drawContext, int x, int y, int imageWidth, int imageHeight, Object image, String variableInstance, Float red, Float green, Float blue, Float alpha) {
        class_2960 textureId;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(red.floatValue(), green.floatValue(), blue.floatValue(), alpha.floatValue());
        if (Objects.equals(variableInstance, "File")) {
            textureId = getOrLoadTexture((File)image);
        } else if (Objects.equals(variableInstance, "Identifier")) {
            textureId = (class_2960)image;
        } else {
            textureId = null;
        }
        if (textureId != null)
            drawContext.method_25290(textureId, x, y, 0.0F, 0.0F, imageWidth, imageHeight, imageWidth, imageHeight);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }

    public static void renderOverlayContent(class_332 drawContext, int imageWidth, int imageHeight, Object image, String variableInstance, Float red, Float green, Float blue, Float alpha) {
        renderOverlayContent(drawContext, 0, 0, imageWidth, imageHeight, image, variableInstance, red, green, blue, alpha);
    }

    public static class_2960 getOrLoadTexture(File file) {
        String filePath = file.getAbsolutePath();
        if (loadedTextures.containsKey(filePath))
            return loadedTextures.get(filePath);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            class_1011 nativeImage = class_1011.method_4309(fileInputStream);
            fileInputStream.close();
            class_1043 texture = new class_1043(nativeImage);
            String filename = file.getName();
            String nameWithoutExtension = filename.replaceAll("\.[^.]$", "");
            String cleanName = nameWithoutExtension.toLowerCase().replaceAll("[^a-z0-9._-]", "");
            if (!cleanName.matches("^[a-z0-9]."))
                cleanName = "dynamic_" + cleanName;
            String uniqueName = "dynamic/" + cleanName + "_" + System.currentTimeMillis();
            class_2960 textureId = class_2960.method_60655("isrealanything", uniqueName);
            class_310.method_1551().method_1531().method_4616(textureId, (class_1044)texture);
            loadedTextures.put(filePath, textureId);
            return textureId;
        } catch (IOException e) {
            System.err.println("Failed to load texture from file: " + file.getAbsolutePath());
            e.printStackTrace();
            return null;
        }
    }
}

package com.epicspymain.isrealanything.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.epicspymain.isrealanything.events.ScreenOverlay;
import epicspymain.isrealanything.file.EntityScreenshotCapture;
import java.io.File;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_243;
import net.minecraft.class_286;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_293;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_757;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class SkyImageRenderer {
    private static final class_2960 FACE_IMAGE_TEXTURE = class_2960.method_60655("isrealanything", "textures/misc/MyBeautifulFace.png");

    private static Boolean ToggledTexture = Boolean.valueOf(false);

    private static float ImageX = 4.0F;

    private static float ImageY = 6.0F;

    private static final Vector3f IMAGE_RELATIVE_POS = new Vector3f(10.0F, 50.0F, -100.0F);

    public static void register() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(SkyImageRenderer::renderSkyImage);
    }

    public static void toggleTexture() {
        ToggledTexture = Boolean.valueOf(!ToggledTexture.booleanValue());
        ImageX = 4.0F;
        ImageY = 6.0F;
    }

    private static void renderSkyImage(WorldRenderContext context) {
        class_310 client = class_310.method_1551();
        if (!ToggledTexture.booleanValue())
            return;
        if (client.field_1687 == null || client.field_1724 == null)
            return;
        class_4587 matrices = getMatrixStack();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(770, 771);
        RenderSystem.setShader(class_757::method_34542);
        RenderSystem.setShaderTexture(0, FACE_IMAGE_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        Matrix4f matrix = matrices.method_23760().method_23761();
        class_289 tessellator = class_289.method_1348();
        class_287 buffer = tessellator.method_60827(class_293.class_5596.field_27382, class_290.field_1585);
        buffer.method_22918(matrix, -1.0F, -1.0F, 0.0F).method_22913(0.0F, 1.0F);
        buffer.method_22918(matrix, 1.0F, -1.0F, 0.0F).method_22913(1.0F, 1.0F);
        buffer.method_22918(matrix, 1.0F, 1.0F, 0.0F).method_22913(1.0F, 0.0F);
        buffer.method_22918(matrix, -1.0F, 1.0F, 0.0F).method_22913(0.0F, 0.0F);
        class_286.method_43433(buffer.method_60800());
        if (isPlayerLookingAtImage(context)) {
            ImageX = (float)(ImageX + 0.006D);
            ImageY = (float)(ImageY + 0.009D);
            if (ImageX >= 10.0F) {
                toggleTexture();
                (new Thread(() -> client.execute(())))

                        .start();
            }
        }
        RenderSystem.disableBlend();
        matrices.method_22909();
    }

    @NotNull
    private static class_4587 getMatrixStack() {
        class_4587 matrices = new class_4587();
        matrices.method_22903();
        matrices.method_46416(IMAGE_RELATIVE_POS.x, IMAGE_RELATIVE_POS.y, IMAGE_RELATIVE_POS.z);
        matrices.method_22905(ImageX, ImageY, 1.0F);
        return matrices;
    }

    private static boolean isPlayerLookingAtImage(WorldRenderContext context) {
        class_310 client = class_310.method_1551();
        if (client.field_1724 == null)
            return false;
        class_243 playerLook = client.field_1724.method_5828(context.tickCounter().method_60637(true));
        class_243 imageDirection = (new class_243(IMAGE_RELATIVE_POS.x, IMAGE_RELATIVE_POS.y, IMAGE_RELATIVE_POS.z)).method_1029();
        double dotProduct = playerLook.method_1026(imageDirection);
        double angleInDegrees = Math.toDegrees(Math.acos(Math.max(-1.0D, Math.min(1.0D, dotProduct))));
        return (angleInDegrees < 4.0D);
    }
}