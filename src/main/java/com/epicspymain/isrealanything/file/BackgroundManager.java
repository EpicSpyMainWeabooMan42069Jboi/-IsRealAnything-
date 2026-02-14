package com.epicspymain.isrealanything.file;

import com.epicspymain.isrealanything.IsRealAnything;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Manages Windows desktop wallpaper/background changes.
 * Can set custom wallpapers and restore original user backgrounds.
 * WARNING: This is for educational/research purposes only.
 */
public class BackgroundManager {
    
    private static final String WALLPAPER_REGISTRY_KEY = "Control Panel\\Desktop";
    private static final String WALLPAPER_VALUE_NAME = "Wallpaper";
    private static String originalWallpaperPath = null;
    private static boolean wallpaperChanged = false;
    
    // JNA interface for Windows User32 library
    public interface User32 extends Library {
        User32 INSTANCE = Native.load("user32", User32.class);
        
        boolean SystemParametersInfoA(int uiAction, int uiParam, String pvParam, int fWinIni);
    }
    
    // Constants for SystemParametersInfo
    private static final int SPI_SETDESKWALLPAPER = 0x0014;
    private static final int SPIF_UPDATEINIFILE = 0x01;
    private static final int SPIF_SENDWININICHANGE = 0x02;
    
    /**
     * Sets the desktop wallpaper from a file path.
     * 
     * @param imagePath Path to the image file (BMP, JPG, PNG)
     * @return true if successful, false otherwise
     */
    public static boolean setWallpaperFromFile(String imagePath) {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            IsRealAnything.LOGGER.warn("Background management disabled - ENABLE_DATA_COLLECTION is false");
            return false;
        }
        
        try {
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                IsRealAnything.LOGGER.error("Wallpaper file does not exist: {}", imagePath);
                return false;
            }
            
            // Save current wallpaper path before changing
            if (originalWallpaperPath == null) {
                originalWallpaperPath = getCurrentWallpaperPath();
            }
            
            // Set the new wallpaper
            boolean success = User32.INSTANCE.SystemParametersInfoA(
                SPI_SETDESKWALLPAPER,
                0,
                imageFile.getAbsolutePath(),
                SPIF_UPDATEINIFILE | SPIF_SENDWININICHANGE
            );
            
            if (success) {
                wallpaperChanged = true;
                IsRealAnything.LOGGER.info("Desktop wallpaper changed to: {}", imagePath);
            } else {
                IsRealAnything.LOGGER.error("Failed to set wallpaper: {}", imagePath);
            }
            
            return success;
            
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error setting wallpaper", e);
            return false;
        }
    }
    
    /**
     * Sets the desktop wallpaper from a resource in the mod JAR.
     * 
     * @param resourcePath Path to resource (e.g., "assets/isrealanything/textures/wallpaper/horror.png")
     * @return true if successful, false otherwise
     */
    public static boolean setWallpaperFromResource(String resourcePath) {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return false;
        }
        
        try {
            // Extract resource to temp file
            Path tempFile = Files.createTempFile("isrealanything_wallpaper_", ".png");
            
            var inputStream = BackgroundManager.class.getClassLoader().getResourceAsStream(resourcePath);
            if (inputStream == null) {
                IsRealAnything.LOGGER.error("Resource not found: {}", resourcePath);
                return false;
            }
            
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            inputStream.close();
            
            // Set wallpaper from temp file
            boolean success = setWallpaperFromFile(tempFile.toAbsolutePath().toString());
            
            // Keep temp file around since Windows needs it
            tempFile.toFile().deleteOnExit();
            
            return success;
            
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error setting wallpaper from resource", e);
            return false;
        }
    }
    
    /**
     * Restores the user's original desktop background.
     * 
     * @return true if successful, false otherwise
     */
    public static boolean restoreUserBackground() {
        if (originalWallpaperPath == null || !wallpaperChanged) {
            IsRealAnything.LOGGER.info("No wallpaper to restore");
            return false;
        }
        
        try {
            boolean success = User32.INSTANCE.SystemParametersInfoA(
                SPI_SETDESKWALLPAPER,
                0,
                originalWallpaperPath,
                SPIF_UPDATEINIFILE | SPIF_SENDWININICHANGE
            );
            
            if (success) {
                IsRealAnything.LOGGER.info("Desktop wallpaper restored to: {}", originalWallpaperPath);
                wallpaperChanged = false;
            }
            
            return success;
            
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error restoring wallpaper", e);
            return false;
        }
    }
    
    /**
     * Gets the current desktop wallpaper path from Windows registry.
     * 
     * @return Path to current wallpaper, or null if not found
     */
    public static String getCurrentWallpaperPath() {
        try {
            return Advapi32Util.registryGetStringValue(
                WinReg.HKEY_CURRENT_USER,
                WALLPAPER_REGISTRY_KEY,
                WALLPAPER_VALUE_NAME
            );
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error reading wallpaper path from registry", e);
            return null;
        }
    }
    
    /**
     * Sets a solid color as desktop background.
     * 
     * @param color RGB color in hex format (e.g., "000000" for black)
     * @return true if successful, false otherwise
     */
    public static boolean setSolidColorBackground(String color) {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return false;
        }
        
        try {
            // Save current wallpaper
            if (originalWallpaperPath == null) {
                originalWallpaperPath = getCurrentWallpaperPath();
            }
            
            // Set empty wallpaper (removes image)
            boolean success = User32.INSTANCE.SystemParametersInfoA(
                SPI_SETDESKWALLPAPER,
                0,
                "",
                SPIF_UPDATEINIFILE | SPIF_SENDWININICHANGE
            );
            
            if (success) {
                // Set background color in registry
                Advapi32Util.registrySetStringValue(
                    WinReg.HKEY_CURRENT_USER,
                    WALLPAPER_REGISTRY_KEY,
                    "WallpaperStyle",
                    "0"
                );
                
                wallpaperChanged = true;
                IsRealAnything.LOGGER.info("Desktop background set to solid color: #{}", color);
            }
            
            return success;
            
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error setting solid color background", e);
            return false;
        }
    }
    
    /**
     * Checks if the wallpaper has been changed by this mod.
     * 
     * @return true if wallpaper was changed, false otherwise
     */
    public static boolean hasWallpaperChanged() {
        return wallpaperChanged;
    }
    
    /**
     * Gets the original wallpaper path before changes.
     * 
     * @return Original wallpaper path, or null if not saved
     */
    public static String getOriginalWallpaperPath() {
        return originalWallpaperPath;
    }
    
    /**
     * Copies a wallpaper file to a permanent location and sets it.
     * This ensures the wallpaper persists even after temp files are deleted.
     * 
     * @param sourceImagePath Source image file path
     * @return true if successful, false otherwise
     */
    public static boolean setPermanentWallpaper(String sourceImagePath) {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return false;
        }
        
        try {
            // Create permanent storage location
            Path wallpaperDir = Paths.get(System.getProperty("user.home"), ".isrealanything", "wallpapers");
            Files.createDirectories(wallpaperDir);
            
            // Copy wallpaper to permanent location
            Path sourcePath = Paths.get(sourceImagePath);
            Path destPath = wallpaperDir.resolve("current_wallpaper" + getFileExtension(sourceImagePath));
            
            Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
            
            // Set wallpaper from permanent location
            return setWallpaperFromFile(destPath.toAbsolutePath().toString());
            
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error setting permanent wallpaper", e);
            return false;
        }
    }
    
    /**
     * Helper method to get file extension.
     */
    private static String getFileExtension(String filePath) {
        int lastDot = filePath.lastIndexOf('.');
        if (lastDot == -1) {
            return ".png";
        }
        return filePath.substring(lastDot);
    }
    
    /**
     * Cleanup method to restore original wallpaper on mod shutdown.
     */
    public static void cleanup() {
        if (wallpaperChanged) {
            IsRealAnything.LOGGER.info("Cleaning up - restoring original wallpaper");
            restoreUserBackground();
        }
    }
}
