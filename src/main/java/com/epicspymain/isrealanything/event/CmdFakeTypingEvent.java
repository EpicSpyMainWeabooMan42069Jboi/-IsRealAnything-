package com.epicspymain.isrealanything.event;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * EVENT 14: CmdFakeTyping - Simulates CMD window typing
 * Opens CMD (simulated in chat)
 * Types gibberish for 10 seconds
 * Closes automatically
 * 
 * Note: Actual CMD opening requires native OS access.
 * This implementation simulates it through chat messages.
 */
public class CmdFakeTypingEvent {
    
    private static final String[] CMD_GIBBERISH = {
        "C:\\Users\\Player> system32_check.exe",
        "Initializing...",
        "ERROR: UNKNOWN_ENTITY_DETECTED",
        "Scanning memory: 0x7F4A2B1C...",
        "WARNING: Unauthorized access attempt",
        "Process 'minecraft.exe' compromised",
        "TheME.dll injected successfully",
        "System.out.println(\"I can see you\")",
        "Access granted to: C:\\Users\\Player\\AppData\\Roaming\\.minecraft",
        "Reading player_data.json...",
        "Player location: " + java.time.LocalDateTime.now(),
        "Uploading to: 192.168.13.37",
        "Connection established...",
        "I KNOW WHO YOU ARE",
        "Shutting down...",
        "Process terminated."
    };
    
    /**
     * Trigger fake CMD typing event
     */
    public static void trigger(ServerPlayerEntity player) {
        // Header
        player.sendMessage(
            Text.literal("════════════════════════════════════════")
                .formatted(Formatting.BLACK, Formatting.BOLD),
            false
        );
        player.sendMessage(
            Text.literal("Microsoft Windows [Version 10.0.19045]")
                .formatted(Formatting.DARK_GRAY),
            false
        );
        player.sendMessage(
            Text.literal("(c) Microsoft Corporation. All rights reserved.")
                .formatted(Formatting.DARK_GRAY),
            false
        );
        player.sendMessage(Text.literal(""), false);
        
        // Type each line with delay
        for (int i = 0; i < CMD_GIBBERISH.length; i++) {
            final String line = CMD_GIBBERISH[i];
            final int index = i;
            
            player.getServer().execute(() -> {
                try {
                    Thread.sleep(600L * index); // 600ms between lines
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                // Determine color based on content
                Formatting color = Formatting.GRAY;
                if (line.contains("ERROR") || line.contains("WARNING")) {
                    color = Formatting.RED;
                } else if (line.contains("SUCCESS") || line.contains("granted")) {
                    color = Formatting.GREEN;
                } else if (line.contains("I ")) {
                    color = Formatting.DARK_RED;
                }
                
                player.sendMessage(
                    Text.literal(line).formatted(color),
                    false
                );
            });
        }
        
        // Close CMD after all lines
        player.getServer().execute(() -> {
            try {
                Thread.sleep(600L * CMD_GIBBERISH.length + 1000); // Wait for all lines + 1 sec
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            player.sendMessage(
                Text.literal("════════════════════════════════════════")
                    .formatted(Formatting.BLACK, Formatting.BOLD),
                false
            );
            player.sendMessage(
                Text.literal("[CMD window closed]").formatted(Formatting.DARK_GRAY, Formatting.ITALIC),
                false
            );
        });
    }
}
