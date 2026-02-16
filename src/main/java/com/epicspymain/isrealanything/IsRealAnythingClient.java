package com.epicspymain.isrealanything;

import com.epicspymain.isrealanything.entity.ModEntities;
import com.epicspymain.isrealanything.entity.client.TheMEEntityRenderer;
import com.epicspymain.isrealanything.entity.client.TheOtherMEEntityRenderer;
import com.epicspymain.isrealanything.screen.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
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
		EntityRendererRegistry.register(ModEntities.THE_ME, TheMEEntityRenderer::new);
		EntityRendererRegistry.register(ModEntities.THE_OTHER_ME, TheOtherMEEntityRenderer::new);

		// Register ALL screen overlays
		ScreenOverlayRenderer.register();
		FrozenOverlayRenderer.register();
		GlitchOverlay.register();
		InventoryOverlayRenderer.register();
		LangToasterOverlay.register();
		TheMEEntityOverlay.register();
		TheMEEntityWhiteOverlay.register();
		BlueScreenOverlay.register();
		SkyImageRenderer.register();

		// Activate entity overlay
		TheMEEntityOverlay.activate();

		LOGGER.info("IsRealAnything client initialized!");
	}
}