package com.epicspymain.isrealanything.file;

import com.epicspymain.isrealanything.IsRealAnything;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


public class FrameFileManager {

    private static final int MAP_SIZE = 128; // Standard Minecraft map size

    // Static field for the current texture identifier (used by ImageFrameBlock renderer)
    public static final Identifier CURRENT_FRAME_TEXTURE = Identifier.of(IsRealAnything.MOD_ID, "textures/block/current_frame.png");

    /**
     * Returns the current frame image path for the block renderer.
     * This can be dynamic (e.g., from config, file watch, or last loaded URL).
     * For now, returns a fixed/default path.
     */
    public static String getCurrentFramePath() {
        // TODO: Make this dynamic later (e.g., read from config file, track last loaded URL/image)
        // For now, return the default texture path (matches CURRENT_FRAME_TEXTURE)
        return "textures/block/current_frame.png";
    }

    private static final Map<String, Integer> loadedImages = new HashMap<>();

    /**
     * Loads an image from file into an item frame.
     *
     * @param world The world
     * @param frameEntity The item frame entity
     * @param imagePath Path to the image file
     * @return true if successful, false otherwise
     */
    public static boolean loadImageToFrame(World world, ItemFrameEntity frameEntity, String imagePath) {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            IsRealAnything.LOGGER.warn("Frame file management disabled - ENABLE_DATA_COLLECTION is false");
            return false;
        }
        if (frameEntity == null || world == null) {
            IsRealAnything.LOGGER.error("Invalid frame entity or world");
            return false;
        }
        try {
            // Load image
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                IsRealAnything.LOGGER.error("Image file not found: {}", imagePath);
                return false;
            }
            BufferedImage image = ImageIO.read(imageFile);
            if (image == null) {
                IsRealAnything.LOGGER.error("Failed to read image: {}", imagePath);
                return false;
            }

            // Resize image to map size
            BufferedImage resized = resizeImage(image, MAP_SIZE, MAP_SIZE);

            // Create map item
            ItemStack mapItem = createMapFromImage(world, resized);

            // Set item in frame
            frameEntity.setHeldItemStack(mapItem);
            IsRealAnything.LOGGER.info("Image loaded to frame: {}", imagePath);
            return true;
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error loading image to frame: {}", imagePath, e);
            return false;
        }
    }

}