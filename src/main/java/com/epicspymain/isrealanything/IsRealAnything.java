package com.epicspymain.isrealanything;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IsRealAnything implements ModInitializer {
	public static final String MOD_ID = "isrealanything";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
	// Data collection toggle (set to false by default for safety)
	public static boolean ENABLE_DATA_COLLECTION = false;

	@Override
	public void onInitialize() {
		LOGGER.info("IsRealAnything mod initialized!");
		LOGGER.info("Be Prepared To Get Your Socks Blown Off In Shock!");
		
		if (ENABLE_DATA_COLLECTION) {
			LOGGER.warn("Data collection features are ENABLED");
		}
	}
}
