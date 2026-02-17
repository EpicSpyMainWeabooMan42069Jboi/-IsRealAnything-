package com.epicspymain.isrealanything.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;


public class BlueScreenOverlay {

    private static final Identifier BSOD_TEXTURE = new Identifier("isrealanything", "textures/screen/BlueScreen.png");
    private static boolean bsodActive = false;
    private static int bsodTicks = 0;

    /**
     * Register renderer
     */
    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (bsodActive) {
                renderBSOD(drawContext);
                bsodTicks--;

                if (bsodTicks <= 0) {
                    bsodActive = false;
                }
            }
        });
    }

    /**
     * Trigger BSOD overlay
     */
    public static void trigger(int ticks) {
        bsodActive = true;
        bsodTicks = ticks;
    }


    private static void renderBSOD(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Draw BSOD texture full screen
        context.drawTexture(BSOD_TEXTURE, 0, 0, 0, 0, width, height, width, height);

        RenderSystem.disableBlend();
    }

    /**
     * Stop BSOD
     */
    public static void stop() {
        bsodActive = false;
        bsodTicks = 0;
    }
}