package com.epicspymain.isrealanything.event;

import com.epicspymain.isrealanything.sound.ModSounds;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * EVENT 1: IJoin - Fake player join message
 * Displays "EpicSpyMain69420 joined the game" in chat
 * Plays F.O.rev_E-R.ogg ONCE on Day 2
 * Never plays again
 */
public class IJoinEvent {
    private static final Set<UUID> playersTriggered = new HashSet<>();
    private static final String FAKE_PLAYER_NAME = "EpicSpyMain69420";
    
    /**
     * Trigger the IJoin event (Day 2 only, once per player)
     */
    public static void trigger(ServerWorld world, ServerPlayerEntity player) {
        // Check if already triggered for this player
        if (playersTriggered.contains(player.getUuid())) {
            return;
        }
        
        // Calculate game day
        int gameDay = (int) (world.getTimeOfDay() / 24000);
        
        // Only trigger on Day 2
        if (gameDay != 2) {
            return;
        }
        
        // Mark as triggered
        playersTriggered.add(player.getUuid());
        
        // Send fake join message to all players
        Text joinMessage = Text.literal(FAKE_PLAYER_NAME + " joined the game")
            .formatted(Formatting.YELLOW);
        
        for (ServerPlayerEntity serverPlayer : world.getServer().getPlayerManager().getPlayerList()) {
            serverPlayer.sendMessage(joinMessage, false);
        }
        
        // Play FOREVER sound once
        world.playSound(
            null, 
            player.getBlockPos(),
                ModSounds.F_O_REV_E_R,
            SoundCategory.AMBIENT, 
            0.7f, 
            1.0f
        );
    }
    
    /**
     * Check if player has already triggered this event
     */
    public static boolean hasTriggered(UUID playerUuid) {
        return playersTriggered.contains(playerUuid);
    }
    
    /**
     * Reset for new world/testing
     */
    public static void reset() {
        playersTriggered.clear();
    }
}
