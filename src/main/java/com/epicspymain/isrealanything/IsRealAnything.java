package com.epicspymain.isrealanything;
import com.epicspymain.isrealanything.event.EventManager;
import com.epicspymain.isrealanything.block.ModBlocks;
import com.epicspymain.isrealanything.entity.custom.TheMEEntity;
import com.epicspymain.isrealanything.entity.custom.TheOtherMEEntity;
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
	

	public static boolean ENABLE_DATA_COLLECTION = true;

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

		FabricDefaultAttributeRegistry.register(ModEntities.THEME_ENTITY, TheMEEntity.createTheMEAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.THEOTHERME_ENTITY, TheOtherMEEntity.createTheOtherMEAttributes());

		// Register custom dimensions
		DimensionRegistry.registerDimensions();
		

		registerEventSystem();
		
		if (ENABLE_DATA_COLLECTION) {
			LOGGER.warn(":3");
		}
	}


	private void registerEventSystem() {
		ServerTickEvents.END_WORLD_TICK.register(world -> {
			EventManager.onTick(world.getServer());
		});

		LOGGER.info("Event system registered");
		LOGGER.info("Phase-based event scheduler initialized");
		LOGGER.info("The Overlook failsafe system active");
	}

}
