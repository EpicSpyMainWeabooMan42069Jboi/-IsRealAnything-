package com.epicspymain.isrealanything.file;

import com.epicspymain.isrealanything.IsRealAnything;

import javax.imageio.ImageIO;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class DesktopFileUtil {
    
    private static final DateTimeFormatter FILENAME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static Path desktopPath = null;
    
    /**
     * Gets the user's desktop directory path.
     * 
     * @return Path to desktop, or null if not found
     */
    public static Path getDesktopPath() {
        if (desktopPath != null) {
            return desktopPath;
        }
        
        String userHome = System.getProperty("user.home");
        
        // Try common desktop locations
        Path[] possiblePaths = {
            Paths.get(userHome, "Desktop"),
            Paths.get(userHome, "OneDrive", "Desktop"),
            Paths.get(userHome, "Escritorio"),  // Spanish
            Paths.get(userHome, "Bureau"),      // French
            Paths.get(userHome, "デスクトップ")    // Japanese
        };
        
        for (Path path : possiblePaths) {
            if (Files.exists(path) && Files.isDirectory(path)) {
                desktopPath = path;
                IsRealAnything.LOGGER.info("Desktop path found: {}", desktopPath);
                return desktopPath;
            }
        }
        
        IsRealAnything.LOGGER.warn("Desktop path not found, using default");
        desktopPath = Paths.get(userHome, "Desktop");
        return desktopPath;
    }
    
    /**
     * Creates a text file on the desktop.
     * 
     * @param filename The filename (without path)
     * @param content The file content
     * @return true if successful, false otherwise
     */
    public static boolean createFileOnDesktop(String filename, String content) {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            IsRealAnything.LOGGER.warn("Desktop file creation disabled - ENABLE_DATA_COLLECTION is false");
            return false;
        }
        
        try {
            Path desktop = getDesktopPath();
            Path filePath = desktop.resolve(filename);
            
            Files.writeString(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            
            IsRealAnything.LOGGER.info("File created on desktop: {}", filename);
            return true;
            
        } catch (IOException e) {
            IsRealAnything.LOGGER.error("Error creating file on desktop: {}", filename, e);
            return false;
        }
    }
    
    /**
     * Creates a text file on the desktop with timestamp in filename.
     * 
     * @param baseFilename Base filename (e.g., "message")
     * @param extension File extension (e.g., ".txt")
     * @param content The file content
     * @return Path to created file, or null if failed
     */
    public static Path createTimestampedFile(String baseFilename, String extension, String content) {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return null;
        }
        
        try {
            String timestamp = LocalDateTime.now().format(FILENAME_FORMATTER);
            String filename = String.format("%s_%s%s", baseFilename, timestamp, extension);
            
            Path desktop = getDesktopPath();
            Path filePath = desktop.resolve(filename);
            
            Files.writeString(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            
            IsRealAnything.LOGGER.info("Timestamped file created: {}", filename);
            return filePath;
            
        } catch (IOException e) {
            IsRealAnything.LOGGER.error("Error creating timestamped file", e);
            return null;
        }
    }
    
    /**
     * Creates an image file on the desktop.
     * 
     * @param filename The filename (without path)
     * @param image The BufferedImage to save
     * @param format Image format (e.g., "png", "jpg")
     * @return true if successful, false otherwise
     */
    public static boolean createImageOnDesktop(String filename, BufferedImage image, String format) {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return false;
        }
        
        try {
            Path desktop = getDesktopPath();
            Path filePath = desktop.resolve(filename);
            
            ImageIO.write(image, format, filePath.toFile());
            
            IsRealAnything.LOGGER.info("Image created on desktop: {}", filename);
            return true;
            
        } catch (IOException e) {
            IsRealAnything.LOGGER.error("Error creating image on desktop: {}", filename, e);
            return false;
        }
    }
    
    /**
     * Copies a file to the desktop.
     * 
     * @param sourceFile Path to source file
     * @param desktopFilename Filename to use on desktop
     * @return true if successful, false otherwise
     */
    public static boolean copyFileToDesktop(Path sourceFile, String desktopFilename) {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return false;
        }
        
        try {
            Path desktop = getDesktopPath();
            Path destPath = desktop.resolve(desktopFilename);
            
            Files.copy(sourceFile, destPath, StandardCopyOption.REPLACE_EXISTING);
            
            IsRealAnything.LOGGER.info("File copied to desktop: {}", desktopFilename);
            return true;
            
        } catch (IOException e) {
            IsRealAnything.LOGGER.error("Error copying file to desktop: {}", desktopFilename, e);
            return false;
        }
    }
    
    /**
     * Creates a shortcut/link file on the desktop (Windows only).
     * 
     * @param shortcutName Name for the shortcut
     * @param targetPath Path to target file/directory
     * @return true if successful, false otherwise
     */
    public static boolean createShortcutOnDesktop(String shortcutName, String targetPath) {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return false;
        }
        
        try {
            Path desktop = getDesktopPath();
            Path shortcutPath = desktop.resolve(shortcutName + ".lnk");
            
            // Create VBS script to create shortcut
            String vbsScript = String.format(
                "Set oWS = WScript.CreateObject(\"WScript.Shell\")\n" +
                "sLinkFile = \"%s\"\n" +
                "Set oLink = oWS.CreateShortcut(sLinkFile)\n" +
                "oLink.TargetPath = \"%s\"\n" +
                "oLink.Save",
                shortcutPath.toString().replace("\\", "\\\\"),
                targetPath.replace("\\", "\\\\")
            );
            
            Path tempVbs = Files.createTempFile("shortcut_", ".vbs");
            Files.writeString(tempVbs, vbsScript);
            
            // Execute VBS script
            Process process = Runtime.getRuntime().exec("wscript " + tempVbs.toAbsolutePath());
            process.waitFor();
            
            // Cleanup
            Files.deleteIfExists(tempVbs);
            
            IsRealAnything.LOGGER.info("Shortcut created on desktop: {}", shortcutName);
            return true;
            
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error creating shortcut on desktop: {}", shortcutName, e);
            return false;
        }
    }
    
    /**
     * Opens a file on the desktop with the default application.
     * 
     * @param filename Filename on desktop
     * @return true if successful, false otherwise
     */
    public static boolean openDesktopFile(String filename) {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return false;
        }
        
        try {
            Path desktop = getDesktopPath();
            Path filePath = desktop.resolve(filename);
            
            if (!Files.exists(filePath)) {
                IsRealAnything.LOGGER.warn("File not found on desktop: {}", filename);
                return false;
            }
            
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(filePath.toFile());
                IsRealAnything.LOGGER.info("Opened desktop file: {}", filename);
                return true;
            } else {
                IsRealAnything.LOGGER.warn("Desktop operations not supported");
                return false;
            }
            
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error opening desktop file: {}", filename, e);
            return false;
        }
    }
    
    /**
     * Deletes a file from the desktop.
     * 
     * @param filename Filename to delete
     * @return true if successful, false otherwise
     */
    public static boolean deleteDesktopFile(String filename) {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return false;
        }
        
        try {
            Path desktop = getDesktopPath();
            Path filePath = desktop.resolve(filename);
            
            boolean deleted = Files.deleteIfExists(filePath);
            
            if (deleted) {
                IsRealAnything.LOGGER.info("Deleted desktop file: {}", filename);
            } else {
                IsRealAnything.LOGGER.warn("Desktop file not found: {}", filename);
            }
            
            return deleted;
            
        } catch (IOException e) {
            IsRealAnything.LOGGER.error("Error deleting desktop file: {}", filename, e);
            return false;
        }
    }
    
    /**
     * Lists all files on the desktop.
     * 
     * @return List of filenames on desktop
     */
    public static List<String> listDesktopFiles() {
        List<String> files = new ArrayList<>();
        
        try {
            Path desktop = getDesktopPath();
            
            if (!Files.exists(desktop)) {
                return files;
            }
            
            Files.list(desktop)
                .filter(Files::isRegularFile)
                .forEach(path -> files.add(path.getFileName().toString()));
            
            IsRealAnything.LOGGER.debug("Found {} files on desktop", files.size());
            
        } catch (IOException e) {
            IsRealAnything.LOGGER.error("Error listing desktop files", e);
        }
        
        return files;
    }
    
    /**
     * Checks if a file exists on the desktop.
     * 
     * @param filename Filename to check
     * @return true if file exists, false otherwise
     */
    public static boolean desktopFileExists(String filename) {
        Path desktop = getDesktopPath();
        Path filePath = desktop.resolve(filename);
        return Files.exists(filePath);
    }
    
    /**
     * Gets the size of a file on the desktop in bytes.
     * 
     * @param filename Filename to check
     * @return File size in bytes, or -1 if not found
     */
    public static long getDesktopFileSize(String filename) {
        try {
            Path desktop = getDesktopPath();
            Path filePath = desktop.resolve(filename);
            
            if (!Files.exists(filePath)) {
                return -1;
            }
            
            return Files.size(filePath);
            
        } catch (IOException e) {
            IsRealAnything.LOGGER.error("Error getting file size: {}", filename, e);
            return -1;
        }
    }
    
    /**
     * Creates a directory on the desktop.
     * 
     * @param directoryName Directory name
     * @return true if successful, false otherwise
     */
    public static boolean createDesktopDirectory(String directoryName) {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return false;
        }
        
        try {
            Path desktop = getDesktopPath();
            Path dirPath = desktop.resolve(directoryName);
            
            Files.createDirectories(dirPath);
            
            IsRealAnything.LOGGER.info("Directory created on desktop: {}", directoryName);
            return true;
            
        } catch (IOException e) {
            IsRealAnything.LOGGER.error("Error creating directory on desktop: {}", directoryName, e);
            return false;
        }
    }
}
