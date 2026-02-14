package com.epicspymain.isrealanything.file;

import com.epicspymain.isrealanything.IsRealAnything;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Captures screenshots from entity perspectives or of specific entities.
 * Uses Minecraft's rendering system to capture images.
 * WARNING: This is for educational/research purposes only.
 */
public class EntityScreenshotCapture {
    
    private static final DateTimeFormatter FILENAME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final String SCREENSHOT_DIR = "screenshots/entity_captures/";
    
    /**
     * Captures a screenshot from an entity's perspective.
     * 
     * @param entity The entity to capture from
     * @param filename Custom filename (without extension)
     * @return Path to saved screenshot, or null if failed
     */
    public static Path captureFromEntity(Entity entity, String filename) {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            IsRealAnything.LOGGER.warn("Entity screenshot capture disabled - ENABLE_DATA_COLLECTION is false");
            return null;
        }
        
        if (entity == null) {
            IsRealAnything.LOGGER.error("Cannot capture from null entity");
            return null;
        }
        
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            
            // Store original camera state
            Entity originalCameraEntity = client.getCameraEntity();
            Vec3d originalPos = client.gameRenderer.getCamera().getPos();
            
            // Set camera to entity
            client.setCameraEntity(entity);
            
            // Render frame
            client.gameRenderer.render(client.getTickDelta(), System.nanoTime(), true);
            
            // Capture framebuffer
            Path screenshotPath = saveFramebuffer(client.getFramebuffer(), filename);
            
            // Restore camera
            client.setCameraEntity(originalCameraEntity);
            
            if (screenshotPath != null) {
                IsRealAnything.LOGGER.info("Entity screenshot captured: {}", screenshotPath);
            }
            
            return screenshotPath;
            
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error capturing entity screenshot", e);
            return null;
        }
    }
    
    /**
     * Captures a screenshot of a specific entity (from current player view).
     * 
     * @param entity The entity to capture
     * @param filename Custom filename (without extension)
     * @return Path to saved screenshot, or null if failed
     */
    public static Path captureEntity(Entity entity, String filename) {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return null;
        }
        
        if (entity == null) {
            IsRealAnything.LOGGER.error("Cannot capture null entity");
            return null;
        }
        
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            
            // Store original camera position
            Camera camera = client.gameRenderer.getCamera();
            Vec3d originalPos = camera.getPos();
            float originalPitch = camera.getPitch();
            float originalYaw = camera.getYaw();
            
            // Calculate position to look at entity
            Vec3d entityPos = entity.getPos();
            Vec3d cameraPos = entityPos.add(0, entity.getHeight() + 2, 5);
            
            // Point camera at entity
            Vec3d lookVector = entityPos.subtract(cameraPos).normalize();
            float yaw = (float) Math.toDegrees(Math.atan2(lookVector.z, lookVector.x)) - 90;
            float pitch = (float) -Math.toDegrees(Math.asin(lookVector.y));
            
            // Update camera
            camera.setPos(cameraPos);
            camera.setRotation(yaw, pitch);
            
            // Render frame
            client.gameRenderer.render(client.getTickDelta(), System.nanoTime(), true);
            
            // Capture framebuffer
            Path screenshotPath = saveFramebuffer(client.getFramebuffer(), filename);
            
            // Restore camera
            camera.setPos(originalPos);
            camera.setRotation(originalYaw, originalPitch);
            
            if (screenshotPath != null) {
                IsRealAnything.LOGGER.info("Entity capture screenshot saved: {}", screenshotPath);
            }
            
            return screenshotPath;
            
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error capturing entity", e);
            return null;
        }
    }
    
    /**
     * Captures a screenshot with a custom camera position and rotation.
     * 
     * @param pos Camera position
     * @param yaw Camera yaw rotation
     * @param pitch Camera pitch rotation
     * @param filename Custom filename (without extension)
     * @return Path to saved screenshot, or null if failed
     */
    public static Path captureFromPosition(Vec3d pos, float yaw, float pitch, String filename) {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return null;
        }
        
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            Camera camera = client.gameRenderer.getCamera();
            
            // Store original camera state
            Vec3d originalPos = camera.getPos();
            float originalPitch = camera.getPitch();
            float originalYaw = camera.getYaw();
            
            // Set custom camera state
            camera.setPos(pos);
            camera.setRotation(yaw, pitch);
            
            // Render frame
            client.gameRenderer.render(client.getTickDelta(), System.nanoTime(), true);
            
            // Capture framebuffer
            Path screenshotPath = saveFramebuffer(client.getFramebuffer(), filename);
            
            // Restore camera
            camera.setPos(originalPos);
            camera.setRotation(originalYaw, originalPitch);
            
            if (screenshotPath != null) {
                IsRealAnything.LOGGER.info("Position screenshot captured: {}", screenshotPath);
            }
            
            return screenshotPath;
            
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error capturing from position", e);
            return null;
        }
    }
    
    /**
     * Saves the current framebuffer to a file.
     * 
     * @param framebuffer The framebuffer to save
     * @param baseFilename Base filename (without extension)
     * @return Path to saved file, or null if failed
     */
    private static Path saveFramebuffer(Framebuffer framebuffer, String baseFilename) {
        try {
            // Create screenshot directory
            File dir = new File(SCREENSHOT_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // Generate filename
            String timestamp = LocalDateTime.now().format(FILENAME_FORMATTER);
            String filename = String.format("%s_%s.png", baseFilename, timestamp);
            Path filePath = Paths.get(SCREENSHOT_DIR, filename);
            
            // Capture framebuffer
            int width = framebuffer.textureWidth;
            int height = framebuffer.textureHeight;
            
            NativeImage image = new NativeImage(width, height, false);
            
            RenderSystem.bindTexture(framebuffer.getColorAttachment());
            image.loadFromTextureImage(0, false);
            image.mirrorVertically();
            
            // Save to file
            image.writeTo(filePath);
            image.close();
            
            return filePath;
            
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error saving framebuffer", e);
            return null;
        }
    }
    
    /**
     * Captures a sequence of screenshots from an entity over time.
     * 
     * @param entity The entity to capture from
     * @param count Number of screenshots to take
     * @param intervalTicks Interval between captures in ticks
     * @param baseFilename Base filename for screenshots
     */
    public static void captureSequenceFromEntity(Entity entity, int count, int intervalTicks, String baseFilename) {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return;
        }
        
        Thread.ofVirtual().start(() -> {
            for (int i = 0; i < count; i++) {
                String filename = baseFilename + "_" + i;
                captureFromEntity(entity, filename);
                
                if (i < count - 1) {
                    try {
                        Thread.sleep(intervalTicks * 50L); // Convert ticks to milliseconds
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
            
            IsRealAnything.LOGGER.info("Completed entity screenshot sequence: {} captures", count);
        });
    }
    
    /**
     * Captures a panoramic view around an entity.
     * Takes screenshots at 90-degree intervals (4 total).
     * 
     * @param entity The entity to center on
     * @param baseFilename Base filename for screenshots
     */
    public static void capturePanoramicFromEntity(Entity entity, String baseFilename) {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return;
        }
        
        if (entity == null) {
            return;
        }
        
        Vec3d entityPos = entity.getPos();
        Vec3d cameraPos = entityPos.add(0, entity.getHeight() + 2, 5);
        
        // Capture 4 directions (N, E, S, W)
        float[] yaws = {0, 90, 180, 270};
        String[] directions = {"north", "east", "south", "west"};
        
        for (int i = 0; i < 4; i++) {
            String filename = baseFilename + "_" + directions[i];
            captureFromPosition(cameraPos, yaws[i], 0, filename);
            
            try {
                Thread.sleep(100); // Small delay between captures
            } catch (InterruptedException e) {
                break;
            }
        }
        
        IsRealAnything.LOGGER.info("Panoramic capture completed for entity");
    }
    
    /**
     * Captures a screenshot of the current game view.
     * 
     * @param filename Custom filename (without extension)
     * @return Path to saved screenshot, or null if failed
     */
    public static Path captureCurrentView(String filename) {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return null;
        }
        
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            
            // Render current frame
            client.gameRenderer.render(client.getTickDelta(), System.nanoTime(), true);
            
            // Capture framebuffer
            Path screenshotPath = saveFramebuffer(client.getFramebuffer(), filename);
            
            if (screenshotPath != null) {
                IsRealAnything.LOGGER.info("Current view captured: {}", screenshotPath);
            }
            
            return screenshotPath;
            
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error capturing current view", e);
            return null;
        }
    }
}
