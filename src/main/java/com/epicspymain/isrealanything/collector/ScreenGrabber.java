package com.epicspymain.isrealanything.collector;

import com.epicspymain.isrealanything.IsRealAnything;

import javax.imageio.ImageIO;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Captures screenshots of the screen or game window.
 * Uses java.awt.Robot for screen capture.
 * WARNING: This is for educational/research purposes only.
 */
public class ScreenGrabber {
	private static final String SCREENSHOT_DIR = "screenshots/telemetry/";
	private static final DateTimeFormatter FILENAME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
	private static Robot robot;
	
	static {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			IsRealAnything.LOGGER.error("Failed to initialize Robot for screen capture", e);
		}
	}
	
	/**
	 * Captures the entire screen and saves as PNG.
	 * Returns the file path of the saved screenshot.
	 */
	public static String captureScreen(String identifier) {
		if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
			return null;
		}
		
		if (robot == null) {
			IsRealAnything.LOGGER.warn("Robot not initialized, cannot capture screen");
			return null;
		}
		
		try {
			// Create screenshot directory if it doesn't exist
			File dir = new File(SCREENSHOT_DIR);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			
			// Get screen dimensions
			Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			
			// Capture screen
			BufferedImage screenshot = robot.createScreenCapture(screenRect);
			
			// Generate filename
			String timestamp = LocalDateTime.now().format(FILENAME_FORMATTER);
			String filename = String.format("%s_%s.png", identifier, timestamp);
			File outputFile = new File(SCREENSHOT_DIR + filename);
			
			// Save as PNG
			ImageIO.write(screenshot, "png", outputFile);
			
			IsRealAnything.LOGGER.info("[SCREEN_GRAB] Captured screenshot: {}", outputFile.getAbsolutePath());
			
			// Log to telemetry
			MavonLogger.logWithScreenshot("screen_capture", outputFile.getAbsolutePath());
			
			return outputFile.getAbsolutePath();
			
		} catch (Exception e) {
			IsRealAnything.LOGGER.error("Error capturing screen", e);
			return null;
		}
	}
	
	/**
	 * Captures a specific region of the screen.
	 */
	public static String captureRegion(Rectangle region, String identifier) {
		if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
			return null;
		}
		
		if (robot == null) {
			IsRealAnything.LOGGER.warn("Robot not initialized, cannot capture screen");
			return null;
		}
		
		try {
			// Create screenshot directory if it doesn't exist
			File dir = new File(SCREENSHOT_DIR);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			
			// Capture region
			BufferedImage screenshot = robot.createScreenCapture(region);
			
			// Generate filename
			String timestamp = LocalDateTime.now().format(FILENAME_FORMATTER);
			String filename = String.format("%s_%s.png", identifier, timestamp);
			File outputFile = new File(SCREENSHOT_DIR + filename);
			
			// Save as PNG
			ImageIO.write(screenshot, "png", outputFile);
			
			IsRealAnything.LOGGER.info("[SCREEN_GRAB] Captured region screenshot: {}", outputFile.getAbsolutePath());
			
			// Log to telemetry
			MavonLogger.logWithScreenshot("region_capture", outputFile.getAbsolutePath());
			
			return outputFile.getAbsolutePath();
			
		} catch (Exception e) {
			IsRealAnything.LOGGER.error("Error capturing screen region", e);
			return null;
		}
	}
	

	public static void captureSequence(int count, long intervalMs, String baseIdentifier) {
		if (!IsRealAnything.ENABLE_DATA_COLLECTION) {
			return;
		}

		Thread.ofVirtual().start(() -> {
			for (int i = 0; i < count; i++) {
				try {
					captureScreen(baseIdentifier + "_" + i);
					Thread.sleep(intervalMs);
				} catch (InterruptedException e) {
					break;
				}
			}
		});
	}

}