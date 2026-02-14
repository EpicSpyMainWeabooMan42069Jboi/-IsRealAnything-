package com.epicspymain.isrealanything.ai;

import com.epicspymain.isrealanything.IsRealAnything;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

/**
 * Manages contextual horror messages based on player actions and environment.
 * Messages adapt to what the player is doing, creating a personalized horror experience.
 */
public class ContextualMessageManager {
    
    private static final Random RANDOM = new Random();
    
    // Fallback generic messages
    private static final String[] FALLBACK_MESSAGES = {
        "I'm watching you.",
        "You can't hide.",
        "I know where you are.",
        "Turn around.",
        "Don't look behind you.",
        "I'm getting closer.",
        "You're not alone.",
        "Can you feel me?",
        "I'm right here.",
        "Why do you run?"
    };
    
    // Mining-specific messages
    private static final String[] MINING_MESSAGES = {
        "Digging won't save you.",
        "The darkness goes deeper than that.",
        "You're going the wrong way.",
        "There's nothing down there but me.",
        "Stop. Digging.",
        "I'm in the walls.",
        "You're getting closer to me.",
        "Don't dig too deep.",
        "I live in the dark.",
        "Can you hear me through the stone?"
    };
    
    // Building-specific messages
    private static final String[] BUILDING_MESSAGES = {
        "Nice base. I'll visit tonight.",
        "Walls won't keep me out.",
        "Building a fortress? For me?",
        "I like what you've done with the place.",
        "This won't protect you.",
        "I've already been inside.",
        "Your home is mine too.",
        "I know every corner of this place.",
        "Don't forget to build a guest room.",
        "I prefer the shadows in here."
    };
    
    // Combat messages
    private static final String[] COMBAT_MESSAGES = {
        "Fighting? How cute.",
        "You think that will stop me?",
        "I feel no pain.",
        "Your weapon is useless.",
        "I've already won.",
        "You're only making this worse.",
        "Violence won't help you.",
        "I like it when you fight back.",
        "Is that the best you can do?",
        "Try harder."
    };
    
    // Nighttime messages
    private static final String[] NIGHT_MESSAGES = {
        "The night is mine.",
        "Darkness suits you.",
        "Don't sleep. I'll be there.",
        "The sun won't save you.",
        "Night lasts forever here.",
        "Can you survive until dawn?",
        "I'm strongest in the dark.",
        "The moon is watching.",
        "Nightmares are real.",
        "Sleep if you dare."
    };
    
    // Daytime messages
    private static final String[] DAY_MESSAGES = {
        "Daylight won't last.",
        "I'm still here, even in the light.",
        "Enjoy the sun while you can.",
        "Light doesn't scare me.",
        "I'm always watching.",
        "Day is just another night.",
        "The shadows remain.",
        "I don't need darkness.",
        "Light. Dark. It doesn't matter.",
        "Tick tock. Night is coming."
    };
    
    // Underground messages
    private static final String[] UNDERGROUND_MESSAGES = {
        "You shouldn't be down here.",
        "This is my home.",
        "Lost in the dark?",
        "The surface is so far away.",
        "I own the underground.",
        "You're in my territory now.",
        "These caves go on forever.",
        "Can you find your way back?",
        "The darkness welcomes you.",
        "Down here, you're mine."
    };
    
    // Inventory full messages
    private static final String[] INVENTORY_MESSAGES = {
        "That's a lot of stuff. Planning something?",
        "Carrying too much? Let me help.",
        "All those items won't save you.",
        "Your pockets are full of useless things.",
        "Material possessions mean nothing here.",
        "I don't need items to hurt you.",
        "Drop everything and run.",
        "Your inventory is heavy. Mine is empty.",
        "What are you hoarding?",
        "All that and still nothing useful."
    };
    
    // Near home/bed messages
    private static final String[] HOME_MESSAGES = {
        "I know where you sleep.",
        "Your bed looks comfortable.",
        "I'll be here when you wake up.",
        "Sweet dreams.",
        "I've been in your bedroom.",
        "Your home is my home.",
        "I like it here. I think I'll stay.",
        "Cozy place you have.",
        "I rearranged some things while you were gone.",
        "Check under your bed."
    };
    
    // Low health messages
    private static final String[] LOW_HEALTH_MESSAGES = {
        "You're hurt. Good.",
        "Almost there.",
        "Just a little more.",
        "I can smell your fear.",
        "You're weak now.",
        "One more hit.",
        "This is the end.",
        "Don't die yet. I'm not done.",
        "Your health is mine to take.",
        "Almost done with you."
    };
    
    // First join messages (day 1-2)
    private static final String[] FIRST_MESSAGES = {
        "Welcome. I've been waiting.",
        "Finally, you're here.",
        "I'm so glad you joined me.",
        "This is going to be fun.",
        "Let's spend some time together.",
        "You and me. Forever.",
        "Don't leave. Not yet.",
        "I've prepared everything for you.",
        "This world is ours now.",
        "After Day 2 in MC days, IsRealAnything?"
    };
    
    // Achievement unlocked messages
    private static final String[] ACHIEVEMENT_MESSAGES = {
        "Congratulations. I'm so proud.",
        "That achievement means nothing.",
        "You think that matters?",
        "Impressive. But it won't help you.",
        "I saw you do that.",
        "I helped you, didn't I?",
        "We did that together.",
        "Your achievements are mine too.",
        "Keep going. For me.",
        "I'm watching your progress closely."
    };
    
    // Crafting messages
    private static final String[] CRAFTING_MESSAGES = {
        "Crafting? Let me see.",
        "That's a nice recipe.",
        "I remember when I made that.",
        "Crafting won't save you.",
        "Build all you want.",
        "I'm right behind you while you craft.",
        "Don't turn around from that crafting table.",
        "Check your crafting recipes. I added one.",
        "Your hands are busy. Mine aren't.",
        "Craft away. I'll wait."
    };
    
    // Portal/Nether messages
    private static final String[] NETHER_MESSAGES = {
        "The Nether is lovely this time of year.",
        "I have friends here.",
        "You brought me to Hell? How romantic.",
        "It's hot here. But I'm colder.",
        "The screams here drown out yours.",
        "Welcome to my vacation home.",
        "Even Hell can't escape me.",
        "The demons fear me too.",
        "Let's stay here. Forever.",
        "You can't run to another dimension."
    };
    
    // Death messages
    private static final String[] DEATH_MESSAGES = {
        "Get up. We're not finished.",
        "Death is just the beginning.",
        "I'll be waiting at your spawn.",
        "Respawn. Let's do this again.",
        "Did you really think it would end?",
        "Death doesn't scare me. Does it scare you?",
        "Come back. I miss you already.",
        "See you soon.",
        "Don't keep me waiting.",
        "Death is temporary. I am forever."
    };
    
    /**
     * Gets a contextually appropriate creepy message based on player situation.
     * 
     * @param player The player to analyze
     * @return A creepy message string
     */
    public static String getCreepyMessage(ServerPlayerEntity player) {
        if (player == null) {
            return getRandomMessage(FALLBACK_MESSAGES);
        }
        
        BlockPos pos = player.getBlockPos();
        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        boolean isNight = player.getWorld().isNight();
        boolean isUnderground = pos.getY() < 50;
        
        // Death/low health (highest priority)
        if (health <= 0) {
            return getRandomMessage(DEATH_MESSAGES);
        }
        
        if (health < maxHealth * 0.3f) {
            return getRandomMessage(LOW_HEALTH_MESSAGES);
        }
        
        // First days special messages
        long dayTime = player.getWorld().getTimeOfDay() / 24000;
        if (dayTime <= 2) {
            if (RANDOM.nextFloat() < 0.3f) {
                return getRandomMessage(FIRST_MESSAGES);
            }
        }
        
        // Dimensional checks
        if (player.getWorld().getRegistryKey().getValue().getPath().equals("the_nether")) {
            if (RANDOM.nextFloat() < 0.5f) {
                return getRandomMessage(NETHER_MESSAGES);
            }
        }
        
        // Time-based messages
        if (isNight && RANDOM.nextFloat() < 0.4f) {
            return getRandomMessage(NIGHT_MESSAGES);
        } else if (!isNight && RANDOM.nextFloat() < 0.2f) {
            return getRandomMessage(DAY_MESSAGES);
        }
        
        // Location-based messages
        if (isUnderground && RANDOM.nextFloat() < 0.5f) {
            return getRandomMessage(UNDERGROUND_MESSAGES);
        }
        
        // Inventory check
        int filledSlots = 0;
        for (int i = 0; i < player.getInventory().size(); i++) {
            if (!player.getInventory().getStack(i).isEmpty()) {
                filledSlots++;
            }
        }
        if (filledSlots > 30 && RANDOM.nextFloat() < 0.3f) {
            return getRandomMessage(INVENTORY_MESSAGES);
        }
        
        // Activity-based messages (require recent tracking)
        // These would need additional tracking systems, so we'll use probabilistic fallbacks
        if (RANDOM.nextFloat() < 0.15f) {
            return getRandomMessage(MINING_MESSAGES);
        }
        
        if (RANDOM.nextFloat() < 0.15f) {
            return getRandomMessage(BUILDING_MESSAGES);
        }
        
        if (RANDOM.nextFloat() < 0.1f) {
            return getRandomMessage(CRAFTING_MESSAGES);
        }
        
        // Default fallback
        return getRandomMessage(FALLBACK_MESSAGES);
    }
    
    /**
     * Sends a creepy message to the player in chat.
     * 
     * @param player The player to message
     */
    public static void sendCreepyMessage(ServerPlayerEntity player) {
        if (player == null) {
            return;
        }
        
        String message = getCreepyMessage(player);
        player.sendMessage(Text.literal("§4" + message), false);
        
        IsRealAnything.LOGGER.debug("Sent creepy message to {}: {}", player.getName().getString(), message);
    }
    
    /**
     * Sends a specific message to the player.
     * 
     * @param player The player
     * @param message The message to send
     */
    public static void sendMessage(ServerPlayerEntity player, String message) {
        if (player != null && message != null) {
            player.sendMessage(Text.literal("§4" + message), false);
        }
    }
    
    /**
     * Gets a random message from an array.
     */
    private static String getRandomMessage(String[] messages) {
        if (messages == null || messages.length == 0) {
            return "...";
        }
        return messages[RANDOM.nextInt(messages.length)];
    }
    
    /**
     * Gets a mining-specific message.
     */
    public static String getMiningMessage() {
        return getRandomMessage(MINING_MESSAGES);
    }
    
    /**
     * Gets a building-specific message.
     */
    public static String getBuildingMessage() {
        return getRandomMessage(BUILDING_MESSAGES);
    }
    
    /**
     * Gets a combat message.
     */
    public static String getCombatMessage() {
        return getRandomMessage(COMBAT_MESSAGES);
    }
    
    /**
     * Gets a home/bed message.
     */
    public static String getHomeMessage() {
        return getRandomMessage(HOME_MESSAGES);
    }
    
    /**
     * Gets an achievement message.
     */
    public static String getAchievementMessage() {
        return getRandomMessage(ACHIEVEMENT_MESSAGES);
    }
}
