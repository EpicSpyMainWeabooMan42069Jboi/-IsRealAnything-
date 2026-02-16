package com.epicspymain.isrealanything.events;

import com.epicspymain.isrealanything.file.BrowserHistoryReader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class BrowserEvent {
    private static final Logger LOGGER = LoggerFactory.getLogger("IsRealAnything");


    public static void execute(ServerPlayerEntity player) {
        try {
            // Get browser history
            Map<String, List<BrowserHistoryReader.HistoryEntry>> historyMap = BrowserHistoryReader.getRecentHistory(10);

            if (historyMap.isEmpty()) {
                player.sendMessage(Text.literal("§c<%s> You Are My Sunshine, My Only Sunshine".formatted(player.getName().getString())), false);
                return;
            }


            String browserName = null;
            List<BrowserHistoryReader.HistoryEntry> entries = null;

            for (Map.Entry<String, List<BrowserHistoryReader.HistoryEntry>> entry : historyMap.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    browserName = entry.getKey();
                    entries = entry.getValue();
                    break;
                }
            }

            if (browserName == null || entries == null || entries.isEmpty()) {
                player.sendMessage(Text.literal("§c<%s> Everyone in your life has left you, forgotten about you…except for Me.".formatted(player.getName().getString())), false);
                return;
            }


            player.sendMessage(Text.literal("§c<%s> I see you use %s".formatted(player.getName().getString(), browserName)), false);


            BrowserHistoryReader.HistoryEntry recent = entries.get(0);
            player.sendMessage(Text.literal("§cYou were just looking at %s".formatted(recent.url())), false);


            Map<String, Integer> urlCounts = new java.util.HashMap<>();
            for (BrowserHistoryReader.HistoryEntry entry : entries) {
                urlCounts.put(entry.url(), urlCounts.getOrDefault(entry.url(), 0) + 1);
            }

            String mostVisited = urlCounts.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);

            if (mostVisited != null) {
                int visitCount = urlCounts.get(mostVisited);
                player.sendMessage(Text.literal("§cBut you visit %s the most".formatted(mostVisited)), false);
                player.sendMessage(Text.literal("§cYou've been there %d times".formatted(visitCount)), false);
            }

            player.sendMessage(Text.literal("§c<%s> I'm watching you, my love :}".formatted(player.getName().getString())), false);

            LOGGER.info("Displayed browser history to player {}", player.getName().getString());

        } catch (Exception e) {
            LOGGER.error("Failed to read browser history", e);
            player.sendMessage(Text.literal("§c<%s> I'm going to eventually find you, Dear :}".formatted(player.getName().getString())), false);
        }
    }


    public static void spamBrowserTabs(int count) {
        try {
            String os = System.getProperty("os.name").toLowerCase();

            for (int i = 0; i < count; i++) {
                ProcessBuilder pb;

                if (os.contains("win")) {
                    // Windows - open default browser
                    pb = new ProcessBuilder("cmd", "/c", "start", "https://www.youtube.com/watch?v=dQw4w9WgXcQ");
                } else if (os.contains("mac")) {
                    // macOS
                    pb = new ProcessBuilder("open", "https://www.youtube.com/watch?v=dQw4w9WgXcQ");
                } else {
                    // Linux
                    pb = new ProcessBuilder("xdg-open", "https://www.youtube.com/watch?v=dQw4w9WgXcQ");
                }

                pb.start();
                Thread.sleep(200); // Slight delay between tabs
            }

            LOGGER.info("Opened {} browser tabs", count);

        } catch (Exception e) {
            LOGGER.error("Failed to spam browser tabs", e);
        }
    }
}