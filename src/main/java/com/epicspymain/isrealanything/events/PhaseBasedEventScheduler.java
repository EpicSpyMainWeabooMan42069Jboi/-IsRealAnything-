package com.epicspymain.isrealanything.events;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

/**
 * Phase-Based Event Scheduler
 * Manages triggering of Phase 1 (Subtle Bleeds) and Phase 2 (Direct Harassment) events
 * Integrates with EventManager
 */
public class PhaseBasedEventScheduler {
    
    private static final Map<UUID, PlayerEventSchedule> playerSchedules = new HashMap<>();
    
    /**
     * Tick event scheduler for player
     */
    public static void tick(ServerWorld world, ServerPlayerEntity player) {
        PlayerEventSchedule schedule = getSchedule(player);
        schedule.tick(world, player);
    }
    
    /**
     * Get or create event schedule for player
     */
    private static PlayerEventSchedule getSchedule(ServerPlayerEntity player) {
        return playerSchedules.computeIfAbsent(player.getUuid(), uuid -> new PlayerEventSchedule());
    }
    
    /**
     * Reset schedule for player
     */
    public static void reset(UUID playerUuid) {
        playerSchedules.remove(playerUuid);
    }
    
    /**
     * Player event schedule
     */
    private static class PlayerEventSchedule {
        // Event triggers
        private boolean ijoinTriggered = false;
        private long lastRandomBlockReplace = 0;
        private long lastMyMobPals = 0;
        private long lastSoundCreep = 0;
        private boolean doNotDeleteTriggered = false;
        private long lastChatEcho = 0;
        private long lastFakeDisconnect = 0;
        private long lastEntityAmbient = 0;
        private long lastEpicSpawns = 0;
        
        // Phase 2 events
        private long lastUndergroundMining = 0;
        private boolean randomTPTriggered = false;
        private long lastWhiteScreenJumpscare = 0;
        private long lastCmdFakeTyping = 0;
        private long lastGlitchCorruption = 0;
        private long lastCameraDistortion = 0;
        private boolean fileNamesTriggered = false;
        private boolean forcedWakeupTriggered = false;
        private boolean haveYouEverBeenLonelyTriggered = false;
        
        void tick(ServerWorld world, ServerPlayerEntity player) {
            long currentTime = world.getTime();
            int gameDay = (int) (currentTime / 24000);
            
            // ===== PHASE 1: SUBTLE BLEEDS =====
            
            // Event 1: IJoin (Day 2, once)
            if (!ijoinTriggered && gameDay == 2) {
                IJoinEvent.trigger(world, player);
                ijoinTriggered = true;
            }
            
            // Event 2: RandomBlockReplace (Every 30 minutes)
            if (currentTime - lastRandomBlockReplace > 36000 && world.random.nextFloat() < 0.3f) {
                RandomBlockReplaceEvent.trigger(world, player);
                lastRandomBlockReplace = currentTime;
            }
            
            // Event 3: MyMobPals (Every 5 minutes)
            if (currentTime - lastMyMobPals > 6000) {
                MyMobPalsEvent.trigger(world, player);
                lastMyMobPals = currentTime;
            }
            
            // Event 4: SoundCreep (Random, when alone)
            if (currentTime - lastSoundCreep > 12000 && world.random.nextFloat() < 0.2f) {
                SoundCreepEvent.trigger(world, player);
                lastSoundCreep = currentTime;
            }
            
            // Event 5: DoNotDeleteThis (Once, Day 5+)
            if (!doNotDeleteTriggered && gameDay >= 5) {
                DoNotDeleteThisEvent.trigger(world, player);
                doNotDeleteTriggered = true;
            }
            
            // Event 6: ChatEcho (Random, every 20 minutes)
            if (currentTime - lastChatEcho > 24000 && world.random.nextFloat() < 0.4f) {
                ChatEchoEvent.trigger(player);
                lastChatEcho = currentTime;
            }
            
            // Event 7: FakeDisconnect (Random, rare)
            if (currentTime - lastFakeDisconnect > 48000 && world.random.nextFloat() < 0.1f) {
                FakeDisconnectPopupEvent.trigger(player);
                lastFakeDisconnect = currentTime;
            }
            
            // Event 8: EntityAmbient (Random, every 15 minutes)
            if (currentTime - lastEntityAmbient > 18000 && world.random.nextFloat() < 0.3f) {
                EntityAmbientAppearanceEvent.trigger(world, player);
                lastEntityAmbient = currentTime;
            }
            
            // Event 9: EpicSpawns (Random, every 10 minutes)
            if (currentTime - lastEpicSpawns > 12000 && world.random.nextFloat() < 0.25f) {
                EpicSpawnsEvent.trigger(world, player);
                lastEpicSpawns = currentTime;
            }
            
            // Event 10: Structure Spawns (Once per structure, day-gated)
            if (gameDay >= 3) StructureSpawnEvent.spawnFreedomHome(world, player);
            if (gameDay >= 3) StructureSpawnEvent.spawnIronTrap(world, player);
            if (gameDay >= 4) StructureSpawnEvent.spawnStripMine(world, player);
            if (gameDay >= 5) StructureSpawnEvent.spawnBedrockPillar(world, player);
            if (gameDay >= 6) StructureSpawnEvent.spawnMeadow(world, player);
            if (gameDay >= 6) StructureSpawnEvent.spawnCorruptedFreedomHome(world, player);
            if (gameDay >= 10) StructureSpawnEvent.spawnMemory(world, player);
            
            // ===== PHASE 2: DIRECT HARASSMENT (Day 7+) =====
            if (gameDay >= 7) {
                // Event 11: UndergroundMining (When underground)
                if (player.getY() < 50 && currentTime - lastUndergroundMining > 18000 && world.random.nextFloat() < 0.2f) {
                    UndergroundMiningEvent.execute(world, player);
                    lastUndergroundMining = currentTime;
                }
                
                // Event 12: RandomTP (Once, Day 10+)
                if (!randomTPTriggered && gameDay >= 10 && world.random.nextFloat() < 0.01f) {
                    RandomTPEvent.trigger(player);
                    randomTPTriggered = true;
                }
                
                // Event 13: WhiteScreenJumpscare (Rare, random)
                if (currentTime - lastWhiteScreenJumpscare > 36000 && world.random.nextFloat() < 0.05f) {
                    WhiteScreenJumpscareEvent.trigger(world, player);
                    lastWhiteScreenJumpscare = currentTime;
                }
                
                // Event 14: CmdFakeTyping (Rare, random)
                if (currentTime - lastCmdFakeTyping > 48000 && world.random.nextFloat() < 0.08f) {
                    CmdFakeTypingEvent.trigger(player);
                    lastCmdFakeTyping = currentTime;
                }
                
                // Event 16: GlitchCorruption (Every 15 minutes)
                if (currentTime - lastGlitchCorruption > 18000) {
                    // This is passive - text corruption happens automatically
                    lastGlitchCorruption = currentTime;
                }
                
                // Event 17: CameraDistortion (Random, rare)
                if (currentTime - lastCameraDistortion > 24000 && world.random.nextFloat() < 0.1f) {
                    CameraDistortionEvent.trigger(world, player);
                    lastCameraDistortion = currentTime;
                }
                
                // Event 18: FileNamesInChat (Once, Day 14+)
                if (!fileNamesTriggered && gameDay >= 14) {
                    FileNamesInChatEvent.trigger(player);
                    fileNamesTriggered = true;
                }
                
                // Event 19: ForcedWakeup (Once, Day 12+, when player has bed)
                if (!forcedWakeupTriggered && gameDay >= 12 && world.random.nextFloat() < 0.05f) {
                    ForcedWakeupEvent.trigger(world, player);
                    forcedWakeupTriggered = true;
                }
                
                // Event 20: HaveYouEverBeenLonely (Once, Day 20+, ultimate event)
                if (!haveYouEverBeenLonelyTriggered && gameDay >= 20) {
                    HaveYouEverBeenLonelyEvent.trigger(world, player);
                    haveYouEverBeenLonelyTriggered = true;
                }
            }
            
            // Tick ongoing events
            MyMobPalsEvent.tick(world);
            EntityAmbientAppearanceEvent.tick(world);
            EpicSpawnsEvent.tick(world);
            InventoryShuffleEvent.tick(player);
            CameraDistortionEvent.tick(world);
        }
    }
}
