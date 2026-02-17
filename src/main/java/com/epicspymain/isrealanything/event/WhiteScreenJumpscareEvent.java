package com.epicspymain.isrealanything.event;

import com.epicspymain.isrealanything.screen.TheMEEntityWhiteOverlay;
import com.epicspymain.isrealanything.sound.ModSounds;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;

/**
 * EVENT 13: WhiteScreenJumpscare
 * Displays WhiteScreen.png overlay for 1 second with jumpscare sound
 */
public class WhiteScreenJumpscareEvent {

    public static void trigger(ServerWorld world, ServerPlayerEntity player) {
        // Use the actual white overlay system (1 second = 1000ms)
        TheMEEntityWhiteOverlay.trigger(1000, 1.0f, false);

        // Play jumpscare sound immediately
        world.playSound(
                null,
                player.getBlockPos(),
                ModSounds.ERRRRRR,
                SoundCategory.MASTER,
                2.0f,
                1.0f
        );

        // Scream with slight delay
        world.getServer().execute(() -> {
            new Thread(() -> {
                try {
                    Thread.sleep(200);
                    world.getServer().execute(() ->
                            world.playSound(
                                    null,
                                    player.getBlockPos(),
                                    ModSounds.,
                                    SoundCategory.MASTER,
                                    1.5f,
                                    1.2f
                            )
                    );
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        });
    }
}