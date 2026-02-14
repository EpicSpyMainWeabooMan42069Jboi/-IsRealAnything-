package com.epicspymain.isrealanything.file;

import com.epicspymain.isrealanything.IsRealAnything;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for reading and parsing JSON configuration files.
 * Supports both file system and resource loading.
 */
public class JsonReader {
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<String, JsonObject> configCache = new HashMap<>();
    
    /**
     * Reads a JSON file from the file system.
     * 
     * @param filePath Path to the JSON file
     * @return JsonObject, or null if failed
     */
    public static JsonObject readJsonFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            
            if (!Files.exists(path)) {
                IsRealAnything.LOGGER.error("JSON file not found: {}", filePath);
                return null;
            }
            
            String content = Files.readString(path);
            JsonObject json = JsonParser.parseString(content).getAsJsonObject();
            
            IsRealAnything.LOGGER.debug("JSON file loaded: {}", filePath);
            return json;
            
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error reading JSON file: {}", filePath, e);
            return null;
        }
    }
    
    /**
     * Reads a JSON file from resources.
     * 
     * @param resourcePath Path to resource (e.g., "config/settings.json")
     * @return JsonObject, or null if failed
     */
    public static JsonObject readJsonResource(String resourcePath) {
        try {
            InputStream inputStream = JsonReader.class.getClassLoader().getResourceAsStream(resourcePath);
            
            if (inputStream == null) {
                IsRealAnything.LOGGER.error("JSON resource not found: {}", resourcePath);
                return null;
            }
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder content = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            
            reader.close();
            inputStream.close();
            
            JsonObject json = JsonParser.parseString(content.toString()).getAsJsonObject();
            
            IsRealAnything.LOGGER.debug("JSON resource loaded: {}", resourcePath);
            return json;
            
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error reading JSON resource: {}", resourcePath, e);
            return null;
        }
    }
    
    /**
     * Reads a mod configuration file from the config directory.
     * 
     * @param configName Name of config file (without .json extension)
     * @return JsonObject, or null if failed
     */
    public static JsonObject readModConfig(String configName) {
        // Check cache first
        if (configCache.containsKey(configName)) {
            return configCache.get(configName);
        }
        
        try {
            Path configPath = Paths.get("config", "isrealanything", configName + ".json");
            
            if (!Files.exists(configPath)) {
                IsRealAnything.LOGGER.warn("Config file not found: {}", configName);
                return createDefaultConfig(configName);
            }
            
            JsonObject config = readJsonFile(configPath.toString());
            
            // Cache for future use
            if (config != null) {
                configCache.put(configName, config);
            }
            
            return config;
            
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error reading mod config: {}", configName, e);
            return null;
        }
    }
    
    /**
     * Gets a string value from a JSON object.
     * 
     * @param json The JSON object
     * @param key The key to look up
     * @param defaultValue Default value if key not found
     * @return String value
     */
    public static String getString(JsonObject json, String key, String defaultValue) {
        if (json == null || !json.has(key)) {
            return defaultValue;
        }
        
        try {
            return json.get(key).getAsString();
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error getting string value for key: {}", key, e);
            return defaultValue;
        }
    }
    
    /**
     * Gets an integer value from a JSON object.
     * 
     * @param json The JSON object
     * @param key The key to look up
     * @param defaultValue Default value if key not found
     * @return Integer value
     */
    public static int getInt(JsonObject json, String key, int defaultValue) {
        if (json == null || !json.has(key)) {
            return defaultValue;
        }
        
        try {
            return json.get(key).getAsInt();
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error getting int value for key: {}", key, e);
            return defaultValue;
        }
    }
    
    /**
     * Gets a boolean value from a JSON object.
     * 
     * @param json The JSON object
     * @param key The key to look up
     * @param defaultValue Default value if key not found
     * @return Boolean value
     */
    public static boolean getBoolean(JsonObject json, String key, boolean defaultValue) {
        if (json == null || !json.has(key)) {
            return defaultValue;
        }
        
        try {
            return json.get(key).getAsBoolean();
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error getting boolean value for key: {}", key, e);
            return defaultValue;
        }
    }
    
    /**
     * Gets a double value from a JSON object.
     * 
     * @param json The JSON object
     * @param key The key to look up
     * @param defaultValue Default value if key not found
     * @return Double value
     */
    public static double getDouble(JsonObject json, String key, double defaultValue) {
        if (json == null || !json.has(key)) {
            return defaultValue;
        }
        
        try {
            return json.get(key).getAsDouble();
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error getting double value for key: {}", key, e);
            return defaultValue;
        }
    }
    
    /**
     * Gets a string array from a JSON object.
     * 
     * @param json The JSON object
     * @param key The key to look up
     * @return List of strings, empty list if not found
     */
    public static List<String> getStringArray(JsonObject json, String key) {
        List<String> result = new ArrayList<>();
        
        if (json == null || !json.has(key)) {
            return result;
        }
        
        try {
            JsonArray array = json.getAsJsonArray(key);
            for (JsonElement element : array) {
                result.add(element.getAsString());
            }
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error getting string array for key: {}", key, e);
        }
        
        return result;
    }
    
    /**
     * Gets a nested JSON object.
     * 
     * @param json The JSON object
     * @param key The key to look up
     * @return Nested JsonObject, or null if not found
     */
    public static JsonObject getObject(JsonObject json, String key) {
        if (json == null || !json.has(key)) {
            return null;
        }
        
        try {
            return json.getAsJsonObject(key);
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error getting nested object for key: {}", key, e);
            return null;
        }
    }
    
    /**
     * Gets a JSON array.
     * 
     * @param json The JSON object
     * @param key The key to look up
     * @return JsonArray, or null if not found
     */
    public static JsonArray getArray(JsonObject json, String key) {
        if (json == null || !json.has(key)) {
            return null;
        }
        
        try {
            return json.getAsJsonArray(key);
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error getting array for key: {}", key, e);
            return null;
        }
    }
    
    /**
     * Checks if a key exists in the JSON object.
     * 
     * @param json The JSON object
     * @param key The key to check
     * @return true if key exists, false otherwise
     */
    public static boolean hasKey(JsonObject json, String key) {
        return json != null && json.has(key);
    }
    
    /**
     * Reads an event configuration file.
     * 
     * @param eventName Name of the event
     * @return Event configuration JsonObject
     */
    public static JsonObject readEventConfig(String eventName) {
        return readModConfig("events/" + eventName);
    }
    
    /**
     * Reads a list of event names from the events index.
     * 
     * @return List of event names
     */
    public static List<String> getEventList() {
        JsonObject eventsIndex = readModConfig("events/index");
        
        if (eventsIndex == null) {
            return new ArrayList<>();
        }
        
        return getStringArray(eventsIndex, "events");
    }
    
    /**
     * Creates a default configuration file if it doesn't exist.
     * 
     * @param configName Name of the config
     * @return Default JsonObject
     */
    private static JsonObject createDefaultConfig(String configName) {
        JsonObject defaultConfig = new JsonObject();
        
        // Add default values based on config name
        switch (configName) {
            case "settings":
                defaultConfig.addProperty("enableDataCollection", false);
                defaultConfig.addProperty("eventInterval", 6000);
                defaultConfig.addProperty("debugMode", false);
                break;
            case "music":
                defaultConfig.addProperty("enableCustomMusic", true);
                defaultConfig.addProperty("musicVolume", 0.75);
                defaultConfig.addProperty("silenceVanillaMusic", true);
                break;
            case "events":
                JsonArray events = new JsonArray();
                events.add("ijoin");
                defaultConfig.add("enabledEvents", events);
                defaultConfig.addProperty("randomEvents", true);
                break;
        }
        
        // Try to save default config
        saveModConfig(configName, defaultConfig);
        
        return defaultConfig;
    }
    
    /**
     * Saves a configuration file to the config directory.
     * 
     * @param configName Name of config file (without .json extension)
     * @param config JsonObject to save
     * @return true if successful, false otherwise
     */
    public static boolean saveModConfig(String configName, JsonObject config) {
        try {
            Path configDir = Paths.get("config", "isrealanything");
            Files.createDirectories(configDir);
            
            Path configPath = configDir.resolve(configName + ".json");
            String json = GSON.toJson(config);
            
            Files.writeString(configPath, json);
            
            // Update cache
            configCache.put(configName, config);
            
            IsRealAnything.LOGGER.info("Config saved: {}", configName);
            return true;
            
        } catch (Exception e) {
            IsRealAnything.LOGGER.error("Error saving mod config: {}", configName, e);
            return false;
        }
    }
    
    /**
     * Reloads a configuration from disk (clears cache).
     * 
     * @param configName Name of config to reload
     * @return Reloaded JsonObject
     */
    public static JsonObject reloadConfig(String configName) {
        configCache.remove(configName);
        return readModConfig(configName);
    }
    
    /**
     * Clears the entire configuration cache.
     */
    public static void clearCache() {
        configCache.clear();
        IsRealAnything.LOGGER.info("Configuration cache cleared");
    }
    
    /**
     * Converts a JsonObject to a Map.
     * 
     * @param json The JSON object
     * @return Map representation
     */
    public static Map<String, Object> toMap(JsonObject json) {
        Map<String, Object> map = new HashMap<>();
        
        if (json == null) {
            return map;
        }
        
        for (String key : json.keySet()) {
            JsonElement element = json.get(key);
            
            if (element.isJsonPrimitive()) {
                if (element.getAsJsonPrimitive().isString()) {
                    map.put(key, element.getAsString());
                } else if (element.getAsJsonPrimitive().isNumber()) {
                    map.put(key, element.getAsNumber());
                } else if (element.getAsJsonPrimitive().isBoolean()) {
                    map.put(key, element.getAsBoolean());
                }
            } else if (element.isJsonObject()) {
                map.put(key, toMap(element.getAsJsonObject()));
            } else if (element.isJsonArray()) {
                List<Object> list = new ArrayList<>();
                for (JsonElement arrayElement : element.getAsJsonArray()) {
                    if (arrayElement.isJsonPrimitive()) {
                        list.add(arrayElement.getAsString());
                    }
                }
                map.put(key, list);
            }
        }
        
        return map;
    }
    
    /**
     * Pretty prints a JSON object to string.
     * 
     * @param json The JSON object
     * @return Formatted JSON string
     */
    public static String prettyPrint(JsonObject json) {
        return GSON.toJson(json);
    }
}
