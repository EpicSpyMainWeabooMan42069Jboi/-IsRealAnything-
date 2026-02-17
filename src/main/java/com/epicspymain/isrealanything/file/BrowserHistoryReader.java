package com.epicspymain.isrealanything.file;

import com.epicspymain.isrealanything.IsRealAnything;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;


public class BrowserHistoryReader {
    

    public static class HistoryEntry {
        private final String url;
        private final String title;
        private final LocalDateTime visitTime;
        private final int visitCount;
        
        public HistoryEntry(String url, String title, LocalDateTime visitTime, int visitCount) {
            this.url = url;
            this.title = title;
            this.visitTime = visitTime;
            this.visitCount = visitCount;
        }
        
        public String getUrl() {
            return url;
        }
        
        public String getTitle() {
            return title;
        }
        
        public LocalDateTime getVisitTime() {
            return visitTime;
        }
        
        public int getVisitCount() {
            return visitCount;
        }
        
        @Override
        public String toString() {
            return String.format("[%s] %s - %s (visited %d times)", 
                visitTime, title != null ? title : "Untitled", url, visitCount);
        }
    }
    
    /**
     * Gets recent browser history from all supported browsers.
     * 
     * @param limit Maximum number of entries to retrieve
     * @return List of history entries
     */
    public static List<HistoryEntry> getRecentHistory(int limit) {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            IsRealAnything.LOGGER.warn("Browser history reading disabled - ENABLE_DATA_COLLECTION is false");
            return new ArrayList<>();
        }
        
        List<HistoryEntry> allHistory = new ArrayList<>();
        
        // Try Chrome
        List<HistoryEntry> chromeHistory = readChromeHistory(limit);
        allHistory.addAll(chromeHistory);
        
        // Try Firefox
        List<HistoryEntry> firefoxHistory = readFirefoxHistory(limit);
        allHistory.addAll(firefoxHistory);
        
        // Try Edge
        List<HistoryEntry> edgeHistory = readEdgeHistory(limit);
        allHistory.addAll(edgeHistory);
        
        // Sort by visit time (most recent first)
        allHistory.sort((a, b) -> b.getVisitTime().compareTo(a.getVisitTime()));
        
        // Limit results
        if (allHistory.size() > limit) {
            allHistory = allHistory.subList(0, limit);
        }
        
        IsRealAnything.LOGGER.info("Retrieved {} browser history entries", allHistory.size());
        return allHistory;
    }
    
    /**
     * Reads Chrome browser history.
     * 
     * @param limit Maximum number of entries
     * @return List of Chrome history entries
     */
    public static List<HistoryEntry> readChromeHistory(int limit) {
        String userProfile = System.getProperty("user.home");
        Path historyPath = Paths.get(userProfile, "AppData", "Local", "Google", "Chrome", 
            "User Data", "Default", "History");
        
        return readChromiumHistory(historyPath, limit, "Chrome");
    }
    
    /**
     * Reads Edge browser history.
     * 
     * @param limit Maximum number of entries
     * @return List of Edge history entries
     */
    public static List<HistoryEntry> readEdgeHistory(int limit) {
        String userProfile = System.getProperty("user.home");
        Path historyPath = Paths.get(userProfile, "AppData", "Local", "Microsoft", "Edge", 
            "User Data", "Default", "History");
        
        return readChromiumHistory(historyPath, limit, "Edge");
    }
    
    /**
     * Reads history from Chromium-based browsers (Chrome, Edge, etc.).
     * 
     * @param historyPath Path to History database file
     * @param limit Maximum number of entries
     * @param browserName Browser name for logging
     * @return List of history entries
     */
    private static List<HistoryEntry> readChromiumHistory(Path historyPath, int limit, String browserName) {
        List<HistoryEntry> history = new ArrayList<>();
        
        if (!Files.exists(historyPath)) {
            IsRealAnything.LOGGER.debug("{} history file not found: {}", browserName, historyPath);
            return history;
        }
        
        try {
            // Copy database to temp file (browser locks the original)
            Path tempDb = Files.createTempFile("history_", ".db");
            Files.copy(historyPath, tempDb, StandardCopyOption.REPLACE_EXISTING);
            
            // Connect to SQLite database
            String url = "jdbc:sqlite:" + tempDb.toAbsolutePath().toString();
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            
            // Query recent history
            String query = String.format(
                "SELECT url, title, last_visit_time, visit_count " +
                "FROM urls " +
                "ORDER BY last_visit_time DESC " +
                "LIMIT %d",
                limit
            );
            
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                String urlStr = rs.getString("url");
                String title = rs.getString("title");
                long chromiumTime = rs.getLong("last_visit_time");
                int visitCount = rs.getInt("visit_count");
                
                // Convert Chromium timestamp (microseconds since 1601-01-01) to Java time
                LocalDateTime visitTime = convertChromiumTime(chromiumTime);
                
                history.add(new HistoryEntry(urlStr, title, visitTime, visitCount));
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
            // Delete temp file
            Files.deleteIfExists(tempDb);
            
            IsRealAnything.LOGGER.info("Retrieved {} {} history entries", history.size(), browserName);
            
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error reading {} history", browserName, e);
        }
        
        return history;
    }
    
    /**
     * Reads Firefox browser history.
     * 
     * @param limit Maximum number of entries
     * @return List of Firefox history entries
     */
    public static List<HistoryEntry> readFirefoxHistory(int limit) {
        List<HistoryEntry> history = new ArrayList<>();
        
        String userProfile = System.getProperty("user.home");
        Path firefoxPath = Paths.get(userProfile, "AppData", "Roaming", "Mozilla", "Firefox", "Profiles");
        
        if (!Files.exists(firefoxPath)) {
            IsRealAnything.LOGGER.debug("Firefox profiles directory not found");
            return history;
        }
        
        try {
            // Find profile directory (usually ends with .default or .default-release)
            File[] profiles = firefoxPath.toFile().listFiles();
            if (profiles == null || profiles.length == 0) {
                return history;
            }
            
            Path placesDb = null;
            for (File profile : profiles) {
                if (profile.isDirectory()) {
                    Path potential = profile.toPath().resolve("places.sqlite");
                    if (Files.exists(potential)) {
                        placesDb = potential;
                        break;
                    }
                }
            }
            
            if (placesDb == null) {
                IsRealAnything.LOGGER.debug("Firefox places.sqlite not found");
                return history;
            }
            
            // Copy database to temp file
            Path tempDb = Files.createTempFile("places_", ".sqlite");
            Files.copy(placesDb, tempDb, StandardCopyOption.REPLACE_EXISTING);
            
            // Connect to SQLite database
            String url = "jdbc:sqlite:" + tempDb.toAbsolutePath().toString();
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            
            // Query recent history
            String query = String.format(
                "SELECT url, title, last_visit_date, visit_count " +
                "FROM moz_places " +
                "WHERE last_visit_date IS NOT NULL " +
                "ORDER BY last_visit_date DESC " +
                "LIMIT %d",
                limit
            );
            
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                String urlStr = rs.getString("url");
                String title = rs.getString("title");
                long firefoxTime = rs.getLong("last_visit_date");
                int visitCount = rs.getInt("visit_count");
                
                // Convert Firefox timestamp (microseconds since epoch)
                LocalDateTime visitTime = convertFirefoxTime(firefoxTime);
                
                history.add(new HistoryEntry(urlStr, title, visitTime, visitCount));
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
            // Delete temp file
            Files.deleteIfExists(tempDb);
            
            IsRealAnything.LOGGER.info("Retrieved {} Firefox history entries", history.size());
            
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error reading Firefox history", e);
        }
        
        return history;
    }
    
    /**
     * Converts Chromium timestamp to LocalDateTime.
     * Chromium uses microseconds since 1601-01-01 00:00:00 UTC.
     */
    private static LocalDateTime convertChromiumTime(long chromiumTime) {
        // Chromium epoch: 1601-01-01
        // Unix epoch: 1970-01-01
        // Difference: 11644473600 seconds = 11644473600000000 microseconds
        long unixMicroseconds = chromiumTime - 11644473600000000L;
        long unixSeconds = unixMicroseconds / 1000000;
        
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(unixSeconds), ZoneId.systemDefault());
    }
    
    /**
     * Converts Firefox timestamp to LocalDateTime.
     * Firefox uses microseconds since Unix epoch.
     */
    private static LocalDateTime convertFirefoxTime(long firefoxTime) {
        long unixSeconds = firefoxTime / 1000000;
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(unixSeconds), ZoneId.systemDefault());
    }
    
    /**
     * Gets the most visited URLs from browser history.
     * 
     * @param limit Maximum number of entries
     * @return List of most visited URLs
     */
    public static List<HistoryEntry> getMostVisitedUrls(int limit) {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return new ArrayList<>();
        }
        
        List<HistoryEntry> allHistory = getRecentHistory(1000); // Get larger sample
        
        // Sort by visit count
        allHistory.sort((a, b) -> Integer.compare(b.getVisitCount(), a.getVisitCount()));
        
        // Limit results
        if (allHistory.size() > limit) {
            allHistory = allHistory.subList(0, limit);
        }
        
        return allHistory;
    }
    
    /**
     * Searches browser history for URLs containing a keyword.
     * 
     * @param keyword Keyword to search for
     * @param limit Maximum number of results
     * @return List of matching history entries
     */
    public static List<HistoryEntry> searchHistory(String keyword, int limit) {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return new ArrayList<>();
        }
        
        List<HistoryEntry> allHistory = getRecentHistory(5000);
        List<HistoryEntry> results = new ArrayList<>();
        
        String lowerKeyword = keyword.toLowerCase();
        
        for (HistoryEntry entry : allHistory) {
            if (entry.getUrl().toLowerCase().contains(lowerKeyword) ||
                (entry.getTitle() != null && entry.getTitle().toLowerCase().contains(lowerKeyword))) {
                results.add(entry);
                
                if (results.size() >= limit) {
                    break;
                }
            }
        }
        
        return results;
    }
}
