package com.epicspymain.isrealanything;

import com.epicspymain.isrealanything.entity.ModEntities;
import com.epicspymain.isrealanything.entity.custom.TheMEEntity;
import com.epicspymain.isrealanything.entity.custom.TheOtherMEEntity;
import com.epicspymain.isrealanything.item.ModItemGroups;
import com.epicspymain.isrealanything.item.ModItems;
import com.epicspymain.isrealanything.sound.ModSounds;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
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
		
		// Register items and sounds
		ModItems.registerModItems();
		ModSounds.registerModSounds();
		ModItemGroups.registerItemGroups();
		
		// Register entities
		ModEntities.registerModEntities();
		
		// Register entity attributes
		FabricDefaultAttributeRegistry.register(ModEntities.THE_ME, TheMEEntity.createTheMEAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.THE_OTHER_ME, TheOtherMEEntity.createTheOtherMEAttributes());
		
		if (ENABLE_DATA_COLLECTION) {
			LOGGER.warn("Data collection features are ENABLED");
		}
	}
}
