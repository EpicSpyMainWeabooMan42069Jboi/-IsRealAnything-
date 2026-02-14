package com.epicspymain.isrealanything;

import com.epicspymain.isrealanything.block.ModBlocks;
import com.epicspymain.isrealanything.entity.ModEntities;
import com.epicspymain.isrealanything.entity.custom.TheMEEntity;
import com.epicspymain.isrealanything.entity.custom.TheOtherMEEntity;
import com.epicspymain.isrealanything.event.EventManager;
import com.epicspymain.isrealanything.event.ProgressionTracker;
import com.epicspymain.isrealanything.event.TheOverlook;
import com.epicspymain.isrealanything.event.TheOverlookEvent;
import com.epicspymain.isrealanything.events.PhaseBasedEventScheduler;
import com.epicspymain.isrealanything.item.ModItemGroups;
import com.epicspymain.isrealanything.item.ModItems;
import com.epicspymain.isrealanything.sound.ModSounds;
import com.epicspymain.isrealanything.world.DimensionRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
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
		
		// Register items, blocks, and sounds
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModSounds.registerModSounds();
		ModItemGroups.registerItemGroups();
		
		// Register entities
		ModEntities.registerModEntities();
		
		// Register entity attributes
		FabricDefaultAttributeRegistry.register(ModEntities.THE_ME, TheMEEntity.createTheMEAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.THE_OTHER_ME, TheOtherMEEntity.createTheOtherMEAttributes());
		
		// Register custom dimensions
		DimensionRegistry.registerDimensions();
		
		// Register event system
		registerEventSystem();
		
		if (ENABLE_DATA_COLLECTION) {
			LOGGER.warn("Data collection features are ENABLED");
		}
	}
	
	/**
	 * Register the event system with server tick callbacks
	 */
	private void registerEventSystem() {
		ServerTickEvents.END_WORLD_TICK.register(world -> {
			// Tick The Overlook system (highest priority)
			TheOverlook.tick(world);
			
			// Tick event manager for all players
			EventManager.getInstance().tick(world);
			
			// Tick The Overlook event if active
			if (TheOverlookEvent.isActive()) {
				TheOverlookEvent.tick(world);
			}
			
			// Update progression tracking for all players
			world.getPlayers().forEach(player -> {
				ProgressionTracker.getInstance().updateProgress(player);
				
				// Tick phase-based event scheduler
				PhaseBasedEventScheduler.tick(world, player);
			});
		});
		
		LOGGER.info("Event system registered successfully");
		LOGGER.info("Phase-based event scheduler initialized");
		LOGGER.info("The Overlook failsafe system active");
	}
}
