package com.epicspymain.isrealanything.events;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * EVENT 24: CmdFlood - Fake CMD error flood
 * Fills chat with fake system errors
 * Creates panic but nothing real happens
 */
public class CmdFloodEvent {
    
    private static final String[] ERROR_MESSAGES = {
        "CRITICAL ERROR: System32 corrupted",
        "WARNING: Memory leak detected at 0x7FFE4A2B",
        "ERROR: Failed to load kernel32.dll",
        "SYSTEM FAILURE: Hard drive sectors damaged",
        "ALERT: Unauthorized process detected: TheME.exe",
        "ERROR: minecraft.exe has stopped responding",
        "CRITICAL: Registry corruption at HKEY_LOCAL_MACHINE",
        "WARNING: Thermal limits exceeded on CPU",
        "ERROR: Graphics driver crash imminent",
        "SYSTEM: Boot sector compromised",
        "ALERT: Network intrusion detected from 192.168.13.37",
        "ERROR: Java Virtual Machine fatal exception",
        "CRITICAL: BIOS checksum mismatch",
        "WARNING: File system integrity check failed",
        "ERROR: Cannot allocate memory - heap overflow",
        "SYSTEM: Multiple hardware failures detected",
        "ALERT: Disk write failure - data loss imminent",
        "ERROR: Operating system core dump initiated",
        "CRITICAL: All recovery options exhausted",
        "SYSTEM SHUTDOWN IN 10... 9... 8..."
    };
    
    /**
     * Trigger CMD error flood
     */
    public static void trigger(ServerPlayerEntity player) {
        // CMD header
        player.sendMessage(
            Text.literal("════════════════════════════════════════")
                .formatted(Formatting.BLACK, Formatting.BOLD),
            false
        );
        player.sendMessage(
            Text.literal("Windows Error Reporter")
                .formatted(Formatting.RED, Formatting.BOLD),
            false
        );
        player.sendMessage(
            Text.literal("════════════════════════════════════════")
                .formatted(Formatting.BLACK, Formatting.BOLD),
            false
        );
        
        // Flood errors
        for (int i = 0; i < ERROR_MESSAGES.length; i++) {
            final String error = ERROR_MESSAGES[i];
            final int index = i;
            
            player.getServer().execute(() -> {
                try {
                    Thread.sleep(200L * index); // 200ms between errors
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                // Color based on severity
                Formatting color = error.contains("CRITICAL") ? Formatting.DARK_RED :
                                 error.contains("ERROR") ? Formatting.RED :
                                 error.contains("WARNING") ? Formatting.YELLOW :
                                 error.contains("ALERT") ? Formatting.GOLD :
                                 Formatting.GRAY;
                
                player.sendMessage(
                    Text.literal("[" + System.currentTimeMillis() + "] " + error)
                        .formatted(color),
                    false
                );
            });
        }
        
        // Final message
        player.getServer().execute(() -> {
            try {
                Thread.sleep(200L * ERROR_MESSAGES.length + 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            player.sendMessage(
                Text.literal("════════════════════════════════════════")
                    .formatted(Formatting.BLACK, Formatting.BOLD),
                false
            );
            player.sendMessage(
                Text.literal("Just kidding. Your computer is fine.")
                    .formatted(Formatting.DARK_GRAY, Formatting.ITALIC),
                false
            );
            player.sendMessage(
                Text.literal("But you should be worried about ME.")
                    .formatted(Formatting.DARK_RED),
                false
            );
        });
    }
}
