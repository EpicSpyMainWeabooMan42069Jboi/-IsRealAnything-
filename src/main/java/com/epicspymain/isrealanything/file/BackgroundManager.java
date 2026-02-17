package com.epicspymain.isrealanything.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Manages desktop wallpaper changes
 * Uses PowerShell on Windows to set desktop background
 * Stores original wallpaper for restoration
 */
public class BackgroundManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("IsRealAnything");
    private static String originalWallpaperPath = null;
    private static boolean wallpaperChanged = false;


    public static void setBackground(String resourcePath) {
        try {
            // Get OS
            String os = System.getProperty("os.name").toLowerCase();

            if (!os.contains("win")) {
                LOGGER.warn("Desktop wallpaper change only supported on Windows");
                return;
            }

            // Save original wallpaper path first
            if (!wallpaperChanged) {
                saveOriginalWallpaper();
            }

            // Extract image from resources to temp file
            File tempImage = extractResourceToTemp(resourcePath);

            if (tempImage == null || !tempImage.exists()) {
                LOGGER.error("Failed to extract wallpaper resource: {}", resourcePath);
                return;
            }

            // Set wallpaper using PowerShell
            setWallpaperFromFile(tempImage);
            wallpaperChanged = true;

            LOGGER.info("Desktop wallpaper changed to: {}", resourcePath);

        } catch (Exception e) {
            LOGGER.error("Failed to change desktop wallpaper", e);
        }
    }

    /**
     * Save current wallpaper path
     */
    private static void saveOriginalWallpaper() {
        try {
            String command = "powershell -Command \"(Get-ItemProperty 'HKCU:\\Control Panel\\Desktop').Wallpaper\"";
            Process process = Runtime.getRuntime().exec(command);

            try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()))) {
                originalWallpaperPath = reader.readLine();
            }

            process.waitFor();

        } catch (Exception e) {
            LOGGER.warn("Could not save original wallpaper path", e);
        }
    }

    /**
     * Set wallpaper from file using PowerShell
     */
    public static void setWallpaperFromFile(File imageFile) {
        try {
            String absolutePath = imageFile.getAbsolutePath().replace("\\", "\\\\");

            // PowerShell script to set wallpaper
            String script = String.format(
                    "$code = @'\n" +
                            "using System.Runtime.InteropServices;\n" +
                            "namespace Win32{\n" +
                            "    public class Wallpaper{\n" +
                            "        [DllImport(\"user32.dll\", CharSet=CharSet.Auto)]\n" +
                            "        static extern int SystemParametersInfo(int uAction, int uParam, string lpvParam, int fuWinIni);\n" +
                            "        public static void SetWallpaper(string path){\n" +
                            "            SystemParametersInfo(20, 0, path, 3);\n" +
                            "        }\n" +
                            "    }\n" +
                            "}\n" +
                            "'@\n" +
                            "Add-Type $code\n" +
                            "[Win32.Wallpaper]::SetWallpaper('%s')", absolutePath
            );

            // Execute PowerShell
            ProcessBuilder pb = new ProcessBuilder(
                    "powershell.exe",
                    "-ExecutionPolicy", "Bypass",
                    "-Command", script
            );

            Process process = pb.start();
            process.waitFor();

        } catch (Exception e) {
            LOGGER.error("Failed to set wallpaper via PowerShell", e);
        }
    }

    /**
     * Extract resource to temp file
     */
    private static File extractResourceToTemp(String resourcePath) {
        try {
            // Load from mod resources
            InputStream stream = BackgroundManager.class.getClassLoader()
                    .getResourceAsStream("assets/isrealanything/textures/wallpaper/" + resourcePath);

            if (stream == null) {
                LOGGER.error("Resource not found: {}", resourcePath);
                return null;
            }

            // Create temp file
            String extension = resourcePath.substring(resourcePath.lastIndexOf("."));
            File tempFile = File.createTempFile("isrealanything_wallpaper_", extension);
            tempFile.deleteOnExit();

            // Copy resource to temp file
            Files.copy(stream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            stream.close();

            return tempFile;

        } catch (IOException e) {
            LOGGER.error("Failed to extract resource to temp file", e);
            return null;
        }
    }

    /**
     * Restore original wallpaper
     */
    public static void restoreUserBackground() {
        if (!wallpaperChanged || originalWallpaperPath == null) {
            return;
        }

        try {
            File originalFile = new File(originalWallpaperPath);

            if (originalFile.exists()) {
                setWallpaperFromFile(originalFile);
                LOGGER.info("Restored original wallpaper");
            }

            wallpaperChanged = false;

        } catch (Exception e) {
            LOGGER.error("Failed to restore original wallpaper", e);
        }
    }

    /**
     * Check if wallpaper has been changed by mod
     */
    public static boolean isWallpaperChanged() {
        return wallpaperChanged;
    }
}