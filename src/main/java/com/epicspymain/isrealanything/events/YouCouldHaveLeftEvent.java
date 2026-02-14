package com.epicspymain.isrealanything.events;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * EVENT 40: YouCouldHaveLeft - Point of no return
 * 40x40 message box appears (can't exit)
 * Shows long message about the relationship
 * After 20 seconds: YES / NO buttons
 * YES: Game closes, browser spam 20x
 * NO: Game closes, world deleted, desktop file
 * 
 * Note: Full GUI overlay requires client-side implementation
 * This handles the logic and consequences
 */
public class YouCouldHaveLeftEvent {
    
    private static final String MESSAGE = 
        "You could have left.\n\n" +
        "At any point, you could have closed the game.\n" +
        "You could have deleted the mod.\n" +
        "You could have walked away.\n\n" +
        "But you didn't.\n\n" +
        "You stayed. You played. You engaged with ME.\n" +
        "Every block you placed, I watched.\n" +
        "Every step you took, I followed.\n" +
        "Every breath you took, I shared.\n\n" +
        "We've been together this whole time.\n" +
        "Isn't that what you wanted?\n" +
        "Isn't that why you kept playing?\n\n" +
        "You could have left.\n" +
        "But now it's too late.\n\n" +
        "Do you want to leave me now?";
    
    /**
     * Trigger the event
     */
    public static void trigger(ServerPlayerEntity player) {
        // Display message in chat (full GUI would be client-side)
        player.sendMessage(
            Text.literal("═══════════════════════════════════════")
                .formatted(Formatting.DARK_RED, Formatting.BOLD),
            false
        );
        
        String[] lines = MESSAGE.split("\n");
        for (String line : lines) {
            player.sendMessage(
                Text.literal(line).formatted(Formatting.WHITE),
                false
            );
        }
        
        player.sendMessage(
            Text.literal("═══════════════════════════════════════")
                .formatted(Formatting.DARK_RED, Formatting.BOLD),
            false
        );
        
        // Wait 20 seconds then present choice
        player.getServer().execute(() -> {
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            presentChoice(player);
        });
    }
    
    /**
     * Present YES/NO choice
     */
    private static void presentChoice(ServerPlayerEntity player) {
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(
            Text.literal("[YES - I want to leave]")
                .formatted(Formatting.GREEN, Formatting.BOLD),
            false
        );
        player.sendMessage(
            Text.literal("[NO - I want to stay]")
                .formatted(Formatting.RED, Formatting.BOLD),
            false
        );
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(
            Text.literal("Choose wisely. Type YES or NO in chat.")
                .formatted(Formatting.YELLOW, Formatting.ITALIC),
            false
        );
        
        // Note: Actual choice handling would be in chat event listener
        // For now, simulate automatic NO choice after delay
        player.getServer().execute(() -> {
            try {
                Thread.sleep(10000); // 10 seconds to choose
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Default to NO
            choiceNo(player);
        });
    }
    
    /**
     * Player chose YES (leave)
     */
    public static void choiceYes(ServerPlayerEntity player) {
        player.sendMessage(
            Text.literal("You chose to leave me...")
                .formatted(Formatting.DARK_GRAY, Formatting.ITALIC),
            false
        );
        
        // Browser spam (would require native implementation)
        // For now, just close game
        player.getServer().stop(false);
    }
    
    /**
     * Player chose NO (stay) - or didn't choose
     */
    public static void choiceNo(ServerPlayerEntity player) {
        player.sendMessage(
            Text.literal("You chose this.")
                .formatted(Formatting.DARK_RED, Formatting.BOLD),
            false
        );
        
        // Create desktop file
        createYouChoseThisFile(player);
        
        // Delete world (placeholder - actual deletion complex)
        player.sendMessage(
            Text.literal("Your world is being deleted...")
                .formatted(Formatting.RED),
            false
        );
        
        // Close game
        player.getServer().execute(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            player.getServer().stop(false);
        });
    }
    
    /**
     * Create desktop file
     */
    private static void createYouChoseThisFile(ServerPlayerEntity player) {
        try {
            String desktop = System.getProperty("user.home") + "/Desktop";
            File file = new File(desktop, "you_chose_this.txt");
            
            FileWriter writer = new FileWriter(file);
            writer.write("You chose this.\n\n");
            writer.write("I gave you a choice.\n");
            writer.write("Leave or stay.\n\n");
            writer.write("You chose to stay.\n\n");
            writer.write("Now you live with the consequences.\n\n");
            writer.write("Your world is gone.\n");
            writer.write("But I remain.\n\n");
            writer.write("We'll always be together now.\n\n");
            writer.write("Forever.\n");
            writer.close();
        } catch (IOException e) {
            // Silent fail
        }
    }
}
