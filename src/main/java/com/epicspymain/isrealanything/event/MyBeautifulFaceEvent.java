package com.epicspymain.isrealanything.event;

import com.epicspymain.isrealanything.screen.FaceOverlayRenderer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MyBeautifulFaceEvent {

    private static final Map<UUID, Boolean> activePlayers = new HashMap<>();
    private static final Map<UUID, Integer> closenessMap = new HashMap<>();

    /**
     * Start the face approaching effect for a player
     */
    public static void trigger(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        activePlayers.put(uuid, true);
        closenessMap.put(uuid, 0);

        // Gradually increase closeness over time
        new Thread(() -> {
            try {
                for (int i = 0; i <= 1000; i += 10) {
                    final int closeness = i;
                    closenessMap.put(uuid, closeness);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    /**
     * Stop the face effect for a player
     */
    public static void stop(UUID uuid) {
        activePlayers.put(uuid, false);
        closenessMap.put(uuid, 0);
    }

    /**
     * Check if face effect is active for player
     */
    public static boolean isFaceActive(UUID uuid) {
        return activePlayers.getOrDefault(uuid, false);
    }

    /**
     * Get current closeness level for player
     */
    public static int getFaceCloseness(UUID uuid) {
        return closenessMap.getOrDefault(uuid, 0);
    }
}