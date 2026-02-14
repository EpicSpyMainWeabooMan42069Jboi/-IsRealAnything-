package com.epicspymain.isrealanything.file;

import com.epicspymain.isrealanything.IsRealAnything;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

/**
 * Locates the user's country based on their IP address.
 * Uses ip-api.com for geolocation lookup.
 * WARNING: This is for educational/research purposes only.
 */
public class CountryLocator {
    
    private static final String IP_API_URL = "http://ip-api.com/json/";
    private static String cachedCountryCode = null;
    private static String cachedCountryName = null;
    
    /**
     * Gets the country code (e.g., "US", "GB", "CA") from the current IP.
     * 
     * @return Two-letter country code, or null if not found
     */
    public static String getCountryCode() {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            IsRealAnything.LOGGER.warn("Country location disabled - ENABLE_DATA_COLLECTION is false");
            return null;
        }
        
        if (cachedCountryCode != null) {
            return cachedCountryCode;
        }
        
        try {
            JsonObject data = fetchLocationData();
            if (data != null && data.has("countryCode")) {
                cachedCountryCode = data.get("countryCode").getAsString();
                IsRealAnything.LOGGER.info("Country code: {}", cachedCountryCode);
                return cachedCountryCode;
            }
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error getting country code from IP", e);
        }
        
        return null;
    }
    
    /**
     * Gets the full country name from the current IP.
     * 
     * @return Country name, or null if not found
     */
    public static String getCountryName() {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return null;
        }
        
        if (cachedCountryName != null) {
            return cachedCountryName;
        }
        
        try {
            JsonObject data = fetchLocationData();
            if (data != null && data.has("country")) {
                cachedCountryName = data.get("country").getAsString();
                IsRealAnything.LOGGER.info("Country name: {}", cachedCountryName);
                return cachedCountryName;
            }
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error getting country name from IP", e);
        }
        
        return null;
    }
    
    /**
     * Gets the country code asynchronously.
     * 
     * @return CompletableFuture containing country code
     */
    public static CompletableFuture<String> getCountryCodeAsync() {
        return CompletableFuture.supplyAsync(() -> {
            if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
                return null;
            }
            
            return getCountryCode();
        });
    }
    
    /**
     * Gets the country name asynchronously.
     * 
     * @return CompletableFuture containing country name
     */
    public static CompletableFuture<String> getCountryNameAsync() {
        return CompletableFuture.supplyAsync(() -> {
            if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
                return null;
            }
            
            return getCountryName();
        });
    }
    
    /**
     * Gets the country code from a specific IP address.
     * 
     * @param ipAddress The IP address to look up
     * @return Country code, or null if not found
     */
    public static String getCountryCodeFromIP(String ipAddress) {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return null;
        }
        
        try {
            JsonObject data = fetchLocationDataForIP(ipAddress);
            if (data != null && data.has("countryCode")) {
                return data.get("countryCode").getAsString();
            }
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error getting country code from IP: {}", ipAddress, e);
        }
        
        return null;
    }
    
    /**
     * Gets the country name from a specific IP address.
     * 
     * @param ipAddress The IP address to look up
     * @return Country name, or null if not found
     */
    public static String getCountryNameFromIP(String ipAddress) {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return null;
        }
        
        try {
            JsonObject data = fetchLocationDataForIP(ipAddress);
            if (data != null && data.has("country")) {
                return data.get("country").getAsString();
            }
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error getting country name from IP: {}", ipAddress, e);
        }
        
        return null;
    }
    
    /**
     * Checks if the user is from a specific country.
     * 
     * @param countryCode Two-letter country code to check (e.g., "US")
     * @return true if user is from the specified country, false otherwise
     */
    public static boolean isFromCountry(String countryCode) {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return false;
        }
        
        String userCountry = getCountryCode();
        return userCountry != null && userCountry.equalsIgnoreCase(countryCode);
    }
    
    /**
     * Gets complete country information including code and name.
     * 
     * @return CountryInfo object, or null if not found
     */
    public static CountryInfo getCountryInfo() {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return null;
        }
        
        try {
            JsonObject data = fetchLocationData();
            if (data != null) {
                String code = data.has("countryCode") ? data.get("countryCode").getAsString() : null;
                String name = data.has("country") ? data.get("country").getAsString() : null;
                
                if (code != null && name != null) {
                    return new CountryInfo(code, name);
                }
            }
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error getting country info", e);
        }
        
        return null;
    }
    
    /**
     * Fetches location data from ip-api.com for the current IP.
     * 
     * @return JsonObject containing location data
     */
    private static JsonObject fetchLocationData() {
        try {
            URL url = new URL(IP_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "IsRealAnything/1.0");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            int responseCode = conn.getResponseCode();
            
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                JsonObject data = JsonParser.parseString(response.toString()).getAsJsonObject();
                
                IsRealAnything.LOGGER.debug("Country location data fetched successfully");
                return data;
            } else {
                IsRealAnything.LOGGER.warn("Failed to fetch country data, response code: {}", responseCode);
            }
            
            conn.disconnect();
            
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error fetching country location data", e);
        }
        
        return null;
    }
    
    /**
     * Fetches location data for a specific IP address.
     * 
     * @param ipAddress The IP address to look up
     * @return JsonObject containing location data
     */
    private static JsonObject fetchLocationDataForIP(String ipAddress) {
        try {
            URL url = new URL(IP_API_URL + ipAddress);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "IsRealAnything/1.0");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            int responseCode = conn.getResponseCode();
            
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                return JsonParser.parseString(response.toString()).getAsJsonObject();
            }
            
            conn.disconnect();
            
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error fetching country data for IP: {}", ipAddress, e);
        }
        
        return null;
    }
    
    /**
     * Clears the cached country data to force a fresh fetch.
     */
    public static void clearCache() {
        cachedCountryCode = null;
        cachedCountryName = null;
        IsRealAnything.LOGGER.info("Country cache cleared");
    }
    
    /**
     * Helper class to hold country information.
     */
    public static class CountryInfo {
        private final String code;
        private final String name;
        
        public CountryInfo(String code, String name) {
            this.code = code;
            this.name = name;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getName() {
            return name;
        }
        
        @Override
        public String toString() {
            return String.format("%s (%s)", name, code);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof CountryInfo)) return false;
            CountryInfo other = (CountryInfo) obj;
            return code.equals(other.code);
        }
        
        @Override
        public int hashCode() {
            return code.hashCode();
        }
    }
}
