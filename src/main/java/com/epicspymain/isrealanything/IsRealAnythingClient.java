package com.epicspymain.isrealanything;

import com.epicspymain.isrealanything.client.lang.LangToaster;
import com.epicspymain.isrealanything.entity.ModEntities;
import com.epicspymain.isrealanything.entity.client.TheMEEntityRenderer;
import com.epicspymain.isrealanything.entity.client.TheOtherMEEntityRenderer;
import com.epicspymain.isrealanything.screen.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.TitleScreen;
import software.bernie.geckolib.GeckoLib;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IsRealAnythingClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("IsRealAnything");

	@Override
	public void onInitializeClient() {
		LOGGER.info("IsRealAnything client initializing...");

		// Initialize GeckoLib
		GeckoLib.initialize();

		// Register entity renderers
		EntityRendererRegistry.register(ModEntities.THEME_ENTITY, TheMEEntityRenderer::new);
		EntityRendererRegistry.register(ModEntities.THEOTHERME_ENTITY, TheOtherMEEntityRenderer::new);

		// Register screen overlays
		ScreenOverlayRenderer.register();
		FrozenOverlayRenderer.register();
		GlitchOverlay.register();
		InventoryOverlayRenderer.register();
		TheMEEntityOverlay.register();
		TheMEEntityWhiteOverlay.register();
		BlueScreenOverlay.register();
		SkyImageRenderer.register();

		// Activate entity overlay
		TheMEEntityOverlay.activate();

		// Register language toast on title screen
		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (screen instanceof TitleScreen titleScreen) {
				LangToaster.addToast(client, titleScreen);
			}
		});

		LOGGER.info("IsRealAnything client initialized!");
	}
}