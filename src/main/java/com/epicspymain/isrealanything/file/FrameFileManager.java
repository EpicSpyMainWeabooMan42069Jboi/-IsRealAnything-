package com.epicspymain.isrealanything.file;

import com.epicspymain.isrealanything.IsRealAnything;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
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

/**
 * Manages loading images into item frames in Minecraft.
 * Converts external images to Minecraft maps for display.
 * WARNING: This is for educational/research purposes only.
 */
public class FrameFileManager {
    
    private static final int MAP_SIZE = 128; // Standard Minecraft map size
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
    
    /**
     * Loads an image from a resource path into an item frame.
     * 
     * @param world The world
     * @param frameEntity The item frame entity
     * @param resourcePath Path to resource (e.g., "assets/isrealanything/textures/images/horror.png")
     * @return true if successful, false otherwise
     */
    public static boolean loadResourceToFrame(World world, ItemFrameEntity frameEntity, String resourcePath) {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return false;
        }
        
        try {
            // Extract resource to temp file
            Path tempFile = Files.createTempFile("frame_image_", ".png");
            
            var inputStream = FrameFileManager.class.getClassLoader().getResourceAsStream(resourcePath);
            if (inputStream == null) {
                IsRealAnything.LOGGER.error("Resource not found: {}", resourcePath);
                return false;
            }
            
            Files.copy(inputStream, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            inputStream.close();
            
            // Load from temp file
            boolean success = loadImageToFrame(world, frameEntity, tempFile.toAbsolutePath().toString());
            
            // Cleanup
            Files.deleteIfExists(tempFile);
            
            return success;
            
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error loading resource to frame: {}", resourcePath, e);
            return false;
        }
    }
    
    /**
     * Creates a filled map ItemStack from a BufferedImage.
     * 
     * @param world The world
     * @param image The image to convert
     * @return ItemStack containing the map
     */
    private static ItemStack createMapFromImage(World world, BufferedImage image) {
        ItemStack mapStack = new ItemStack(Items.FILLED_MAP);
        
        // Get or create map state
        int mapId = world.getNextMapId();
        MapState mapState = MapState.of(mapId, world);
        
        if (mapState != null) {
            // Convert image to map colors
            convertImageToMapColors(image, mapState);
            
            // Set map ID in item
            mapStack.getOrCreateNbt().putInt("map", mapId);
            
            IsRealAnything.LOGGER.debug("Created map with ID: {}", mapId);
        }
        
        return mapStack;
    }
    
    /**
     * Converts a BufferedImage to Minecraft map colors.
     * 
     * @param image The image to convert
     * @param mapState The map state to populate
     */
    private static void convertImageToMapColors(BufferedImage image, MapState mapState) {
        // Get map colors array
        byte[] colors = mapState.colors;
        
        for (int x = 0; x < MAP_SIZE; x++) {
            for (int z = 0; z < MAP_SIZE; z++) {
                if (x < image.getWidth() && z < image.getHeight()) {
                    int rgb = image.getRGB(x, z);
                    byte mapColor = getClosestMapColor(rgb);
                    colors[x + z * MAP_SIZE] = mapColor;
                }
            }
        }
        
        mapState.markDirty();
    }
    
    /**
     * Gets the closest Minecraft map color for an RGB value.
     * Simplified color matching - can be improved with proper color distance calculation.
     * 
     * @param rgb RGB color value
     * @return Minecraft map color byte
     */
    private static byte getClosestMapColor(int rgb) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        
        // Calculate brightness
        int brightness = (red + green + blue) / 3;
        
        // Map to Minecraft color range (0-63)
        // This is a simplified mapping - full implementation would use MapColor class
        if (brightness < 64) {
            return 0; // Black
        } else if (brightness < 128) {
            return 16; // Dark gray
        } else if (brightness < 192) {
            return 32; // Gray
        } else {
            return 48; // White
        }
    }
    
    /**
     * Resizes a BufferedImage to the specified dimensions.
     * 
     * @param original Original image
     * @param width Target width
     * @param height Target height
     * @return Resized image
     */
    private static BufferedImage resizeImage(BufferedImage original, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.drawImage(original, 0, 0, width, height, null);
        g.dispose();
        return resized;
    }
    
    /**
     * Loads an image to multiple item frames (for larger images).
     * Splits the image into a grid and loads each section to a frame.
     * 
     * @param world The world
     * @param topLeftPos Top-left corner position
     * @param gridWidth Width of the frame grid
     * @param gridHeight Height of the frame grid
     * @param imagePath Path to the image file
     * @return true if successful, false otherwise
     */
    public static boolean loadImageToFrameGrid(World world, BlockPos topLeftPos, 
                                                int gridWidth, int gridHeight, 
                                                String imagePath) {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
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
            
            // Resize to fit grid
            int totalWidth = MAP_SIZE * gridWidth;
            int totalHeight = MAP_SIZE * gridHeight;
            BufferedImage resized = resizeImage(image, totalWidth, totalHeight);
            
            // Split and load to frames
            for (int gridX = 0; gridX < gridWidth; gridX++) {
                for (int gridY = 0; gridY < gridHeight; gridY++) {
                    // Extract section
                    BufferedImage section = resized.getSubimage(
                        gridX * MAP_SIZE, 
                        gridY * MAP_SIZE, 
                        MAP_SIZE, 
                        MAP_SIZE
                    );
                    
                    // Find frame at position
                    BlockPos framePos = topLeftPos.add(gridX, -gridY, 0);
                    ItemFrameEntity frame = findItemFrameAt(world, framePos);
                    
                    if (frame != null) {
                        ItemStack mapItem = createMapFromImage(world, section);
                        frame.setHeldItemStack(mapItem);
                    } else {
                        IsRealAnything.LOGGER.warn("No frame found at position: {}", framePos);
                    }
                }
            }
            
            IsRealAnything.LOGGER.info("Image grid loaded: {}x{}", gridWidth, gridHeight);
            return true;
            
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error loading image to frame grid", e);
            return false;
        }
    }
    
    /**
     * Finds an item frame entity at a specific position.
     * 
     * @param world The world
     * @param pos The block position
     * @return ItemFrameEntity if found, null otherwise
     */
    private static ItemFrameEntity findItemFrameAt(World world, BlockPos pos) {
        return world.getEntitiesByClass(ItemFrameEntity.class, 
            new net.minecraft.util.math.Box(pos), 
            frame -> true)
            .stream()
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Clears an item frame (removes the item).
     * 
     * @param frameEntity The item frame entity
     * @return true if successful, false otherwise
     */
    public static boolean clearFrame(ItemFrameEntity frameEntity) {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return false;
        }
        
        if (frameEntity == null) {
            return false;
        }
        
        frameEntity.setHeldItemStack(ItemStack.EMPTY);
        IsRealAnything.LOGGER.debug("Item frame cleared");
        return true;
    }
    
    /**
     * Downloads an image from URL and loads it to a frame.
     * 
     * @param world The world
     * @param frameEntity The item frame entity
     * @param imageUrl URL to the image
     * @return true if successful, false otherwise
     */
    public static boolean loadUrlToFrame(World world, ItemFrameEntity frameEntity, String imageUrl) {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return false;
        }
        
        try {
            // Download image to temp file
            Path tempFile = Files.createTempFile("url_image_", ".png");
            
            java.net.URL url = new java.net.URL(imageUrl);
            try (java.io.InputStream in = url.openStream()) {
                Files.copy(in, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
            
            // Load from temp file
            boolean success = loadImageToFrame(world, frameEntity, tempFile.toAbsolutePath().toString());
            
            // Cleanup
            Files.deleteIfExists(tempFile);
            
            if (success) {
                IsRealAnything.LOGGER.info("URL image loaded to frame: {}", imageUrl);
            }
            
            return success;
            
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error loading URL to frame: {}", imageUrl, e);
            return false;
        }
    }
}
