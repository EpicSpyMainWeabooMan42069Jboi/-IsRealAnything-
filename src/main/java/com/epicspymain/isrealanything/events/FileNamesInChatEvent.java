package com.epicspymain.isrealanything.events;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * EVENT 18: FileNamesInChat - PC file names flood chat
 * Random PC files flood in-game chat
 * Sky turns purple (and STAYS purple for rest of mod)
 * 
 * Note: Sky color change requires client-side mixin.
 * This class handles the file name spam.
 */
public class FileNamesInChatEvent {
    
    private static boolean skyColorChanged = false;
    
    // Realistic PC file names (Windows-style)
    private static final String[] FILE_NAMES = {
        "C:\\Users\\Player\\Desktop\\homework.docx",
        "C:\\Windows\\System32\\kernel32.dll",
        "C:\\Program Files\\Minecraft\\launcher.exe",
        "C:\\Users\\Player\\Documents\\passwords.txt",
        "C:\\Users\\Player\\AppData\\Roaming\\.minecraft\\saves\\World1",
        "C:\\Users\\Player\\Pictures\\screenshot_2024.png",
        "C:\\Users\\Player\\Downloads\\totally_not_virus.exe",
        "C:\\Windows\\explorer.exe",
        "C:\\Users\\Player\\Desktop\\DELETE_THIS.txt",
        "C:\\Users\\Player\\Videos\\recording_2024.mp4",
        "C:\\Program Files\\Java\\jre\\bin\\java.exe",
        "C:\\Users\\Player\\AppData\\Local\\Temp\\tmp8A3F.tmp",
        "C:\\Users\\Player\\Music\\favorite_song.mp3",
        "C:\\Windows\\System32\\drivers\\etc\\hosts",
        "C:\\Users\\Player\\.minecraft\\mods\\isrealanything.jar",
        "C:\\Users\\Player\\Desktop\\backup\\family_photos",
        "C:\\Windows\\regedit.exe",
        "C:\\Users\\Player\\Documents\\diary.txt",
        "C:\\Program Files (x86)\\Steam\\steamapps\\common",
        "C:\\Users\\Player\\AppData\\TheME\\data\\player_info.json",
        "ERROR: FILE_NOT_FOUND",
        "ERROR: ACCESS_DENIED",
        "WARNING: SYSTEM_COMPROMISED",
        "I HAVE ACCESS TO EVERYTHING",
        "C:\\Users\\Player\\Desktop\\DO_NOT_OPEN.exe"
    };
    
    /**
     * Trigger file name spam event
     */
    public static void trigger(ServerPlayerEntity player) {
        // Spam file names in chat
        int fileCount = 15 + player.getWorld().random.nextInt(10); // 15-25 files
        
        for (int i = 0; i < fileCount; i++) {
            final int index = i;
            
            player.getServer().execute(() -> {
                try {
                    Thread.sleep(150L * index); // 150ms between files
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                String fileName = FILE_NAMES[player.getWorld().random.nextInt(FILE_NAMES.length)];
                
                // Determine color
                Formatting color = Formatting.GRAY;
                if (fileName.contains("ERROR") || fileName.contains("WARNING")) {
                    color = Formatting.RED;
                } else if (fileName.contains("I HAVE") || fileName.contains("TheME")) {
                    color = Formatting.DARK_RED;
                } else if (fileName.contains("DELETE") || fileName.contains("DO_NOT")) {
                    color = Formatting.DARK_PURPLE;
                }
                
                player.sendMessage(
                    Text.literal(fileName).formatted(color),
                    false
                );
            });
        }
        
        // Final message
        player.getServer().execute(() -> {
            try {
                Thread.sleep(150L * fileCount + 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            player.sendMessage(
                Text.literal("FILE SCAN COMPLETE").formatted(Formatting.DARK_RED, Formatting.BOLD),
                false
            );
            
            player.sendMessage(
                Text.literal("I know everything about you.").formatted(Formatting.DARK_PURPLE),
                false
            );
        });
        
        // Change sky color (permanent)
        if (!skyColorChanged) {
            changeSkyColor(player);
            skyColorChanged = true;
        }
    }
    
    /**
     * Change sky color to purple
     * This sets a flag that client-side mixin will check
     */
    private static void changeSkyColor(ServerPlayerEntity player) {
        player.sendMessage(
            Text.literal("The sky turns purple...").formatted(Formatting.DARK_PURPLE, Formatting.ITALIC),
            true // Action bar
        );
        
        // Store in player's persistent data
        player.getDataTracker().set(
            net.minecraft.entity.data.TrackedDataHandlerRegistry.BOOLEAN,
            true
        );
    }
    
    /**
     * Check if sky color has been changed
     */
    public static boolean isSkyColorChanged() {
        return skyColorChanged;
    }
    
    /**
     * Get purple sky color (for client-side rendering)
     */
    public static int getPurpleSkyColor() {
        return 0x800080; // Purple
    }
    
    /**
     * Get purple fog color (for client-side rendering)
     */
    public static int getPurpleFogColor() {
        return 0x4B004B; // Dark purple
    }
}
