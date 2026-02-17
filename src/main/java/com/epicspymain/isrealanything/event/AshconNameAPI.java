package com.epicspymain.isrealanything.event;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AshconNameAPI {
    private static final Logger LOGGER = LoggerFactory.getLogger("IsRealAnything");
    private static final String API_URL = "https://api.ashcon.app/mojang/v2/user/";


    public static List<String> fetchNameHistory(UUID playerUuid) {
        List<String> names = new ArrayList<>();

        try {
            String uuidString = playerUuid.toString().replace("-", "");
            URL url = new URL(API_URL + uuidString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse JSON response
                JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();

                // Get current username
                if (json.has("username")) {
                    names.add(json.get("username").getAsString());
                }

                // Get username history
                if (json.has("username_history")) {
                    JsonArray history = json.getAsJsonArray("username_history");
                    for (JsonElement element : history) {
                        JsonObject nameObj = element.getAsJsonObject();
                        if (nameObj.has("username")) {
                            String username = nameObj.get("username").getAsString();
                            if (!names.contains(username)) {
                                names.add(username);
                            }
                        }
                    }
                }

                LOGGER.info("Fetched {} previous names for UUID {}", names.size(), playerUuid);

            } else {
                LOGGER.warn("Failed to fetch name history: HTTP {}", responseCode);
            }

            connection.disconnect();

        } catch (Exception e) {
            LOGGER.error("Error fetching name history from Ashcon API", e);
        }

        return names;
    }


    public static String formatNameHistory(List<String> names) {
        if (names.isEmpty()) {
            return "No previous names found";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Previous names: ");
        for (int i = 0; i < names.size(); i++) {
            sb.append(names.get(i));
            if (i < names.size() - 1) {
                sb.append(" → ");
            }
        }
        return sb.toString();
    }
}