package com.epicspymain.isrealanything.file;

import com.epicspymain.isrealanything.IsRealAnything;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;


public class CityLocator {
    
    private static final String IP_API_URL = "http://ip-api.com/json/";
    private static String cachedCity = null;
    private static String cachedRegion = null;
    private static String cachedCountry = null;
    private static JsonObject cachedLocationData = null;
    

    public static String getCityFromCurrentIP() {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            IsRealAnything.LOGGER.warn("City location disabled - ENABLE_DATA_COLLECTION is false");
            return null;
        }
        
        if (cachedCity != null) {
            return cachedCity;
        }
        
        try {
            JsonObject data = fetchLocationData();
            if (data != null && data.has("city")) {
                cachedCity = data.get("city").getAsString();
                return cachedCity;
            }
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error getting city from IP", e);
        }
        
        return null;
    }
    
    /**
     * Gets the city from a specific IP address.
     * 
     * @param ipAddress The IP address to look up
     * @return City name, or null if not found
     */
    public static String getCityFromIP(String ipAddress) {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return null;
        }
        
        try {
            JsonObject data = fetchLocationDataForIP(ipAddress);
            if (data != null && data.has("city")) {
                return data.get("city").getAsString();
            }
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error getting city from IP: {}", ipAddress, e);
        }
        
        return null;
    }
    
    /**
     * Gets the region/state from the current IP.
     * 
     * @return Region name, or null if not found
     */
    public static String getRegion() {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return null;
        }
        
        if (cachedRegion != null) {
            return cachedRegion;
        }
        
        try {
            JsonObject data = fetchLocationData();
            if (data != null && data.has("regionName")) {
                cachedRegion = data.get("regionName").getAsString();
                return cachedRegion;
            }
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error getting region from IP", e);
        }
        
        return null;
    }
    
    /**
     * Gets the country from the current IP.
     * 
     * @return Country name, or null if not found
     */
    public static String getCountry() {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return null;
        }
        
        if (cachedCountry != null) {
            return cachedCountry;
        }
        
        try {
            JsonObject data = fetchLocationData();
            if (data != null && data.has("country")) {
                cachedCountry = data.get("country").getAsString();
                return cachedCountry;
            }
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error getting country from IP", e);
        }
        
        return null;
    }
    
    /**
     * Gets complete location information asynchronously.
     * 
     * @return CompletableFuture containing location data
     */
    public static CompletableFuture<LocationInfo> getLocationAsync() {
        return CompletableFuture.supplyAsync(() -> {
            if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
                return null;
            }
            
            try {
                JsonObject data = fetchLocationData();
                if (data != null) {
                    return new LocationInfo(data);
                }
            } catch (Exception e) {
                IsRealAnything.LOGGER.error("Error getting location async", e);
            }
            
            return null;
        });
    }
    
    /**
     * Fetches location data from ip-api.com for the current IP.
     * 
     * @return JsonObject containing location data
     */
    private static JsonObject fetchLocationData() {
        if (cachedLocationData != null) {
            return cachedLocationData;
        }
        
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
                
                cachedLocationData = JsonParser.parseString(response.toString()).getAsJsonObject();
                
                IsRealAnything.LOGGER.info("Location data fetched successfully");
                return cachedLocationData;
            } else {
                IsRealAnything.LOGGER.warn("Failed to fetch location data, response code: {}", responseCode);
            }
            
            conn.disconnect();
            
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error fetching location data", e);
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
                
                JsonObject data = JsonParser.parseString(response.toString()).getAsJsonObject();
                
                IsRealAnything.LOGGER.info("Location data fetched for IP: {}", ipAddress);
                return data;
            }
            
            conn.disconnect();
            
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error fetching location data for IP: {}", ipAddress, e);
        }
        
        return null;
    }
    
    /**
     * Gets the ZIP/postal code from the current IP.
     * 
     * @return ZIP code, or null if not found
     */
    public static String getZipCode() {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return null;
        }
        
        try {
            JsonObject data = fetchLocationData();
            if (data != null && data.has("zip")) {
                return data.get("zip").getAsString();
            }
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error getting ZIP code from IP", e);
        }
        
        return null;
    }
    
    /**
     * Gets the ISP name from the current IP.
     * 
     * @return ISP name, or null if not found
     */
    public static String getISP() {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return null;
        }
        
        try {
            JsonObject data = fetchLocationData();
            if (data != null && data.has("isp")) {
                return data.get("isp").getAsString();
            }
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error getting ISP from IP", e);
        }
        
        return null;
    }
    
    /**
     * Gets the latitude and longitude coordinates.
     * 
     * @return Array [latitude, longitude], or null if not found
     */
    public static double[] getCoordinates() {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return null;
        }
        
        try {
            JsonObject data = fetchLocationData();
            if (data != null && data.has("lat") && data.has("lon")) {
                double lat = data.get("lat").getAsDouble();
                double lon = data.get("lon").getAsDouble();
                return new double[]{lat, lon};
            }
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error getting coordinates from IP", e);
        }
        
        return null;
    }
    
    /**
     * Gets the timezone from the current IP.
     * 
     * @return Timezone string, or null if not found
     */
    public static String getTimezone() {
        if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
            return null;
        }
        
        try {
            JsonObject data = fetchLocationData();
            if (data != null && data.has("timezone")) {
                return data.get("timezone").getAsString();
            }
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error getting timezone from IP", e);
        }
        
        return null;
    }
    
    /**
     * Clears the cached location data to force a fresh fetch.
     */
    public static void clearCache() {
        cachedCity = null;
        cachedRegion = null;
        cachedCountry = null;
        cachedLocationData = null;
        IsRealAnything.LOGGER.info("Location cache cleared");
    }
    
    /**
     * Helper class to hold complete location information.
     */
    public static class LocationInfo {
        private final String city;
        private final String region;
        private final String country;
        private final String countryCode;
        private final String zip;
        private final String isp;
        private final double latitude;
        private final double longitude;
        private final String timezone;
        
        public LocationInfo(JsonObject data) {
            this.city = data.has("city") ? data.get("city").getAsString() : null;
            this.region = data.has("regionName") ? data.get("regionName").getAsString() : null;
            this.country = data.has("country") ? data.get("country").getAsString() : null;
            this.countryCode = data.has("countryCode") ? data.get("countryCode").getAsString() : null;
            this.zip = data.has("zip") ? data.get("zip").getAsString() : null;
            this.isp = data.has("isp") ? data.get("isp").getAsString() : null;
            this.latitude = data.has("lat") ? data.get("lat").getAsDouble() : 0.0;
            this.longitude = data.has("lon") ? data.get("lon").getAsDouble() : 0.0;
            this.timezone = data.has("timezone") ? data.get("timezone").getAsString() : null;
        }
        
        public String getCity() { return city; }
        public String getRegion() { return region; }
        public String getCountry() { return country; }
        public String getCountryCode() { return countryCode; }
        public String getZip() { return zip; }
        public String getIsp() { return isp; }
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public String getTimezone() { return timezone; }
        
        @Override
        public String toString() {
            return String.format("%s, %s, %s (%s) [%f, %f]", 
                city, region, country, zip, latitude, longitude);
        }
    }
}
