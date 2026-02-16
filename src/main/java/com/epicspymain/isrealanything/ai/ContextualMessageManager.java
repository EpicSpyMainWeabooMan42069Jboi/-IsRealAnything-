import com.epicspymain.isrealanything.ai.ContextualMessageManager;
import java.util.concurrent.CompletableFuture;
import java.util.Random;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.server.network.ServerPlayerEntity;

public class ContextualMessageManager {
    private static final Random random = new Random();

    // General fallback messages
    private static final String[] FALLBACK_MESSAGES = {
            "I'm watching you...",
            "Don't leave me, Dear",
            "You're mine forever :3",
            "Capybaras are my favorite",
            "Lost, Alone, Hopeless; You Probably Deserve it",
            "It hurts when you're not here",
            "You're all I have left",

};

// Mining context
private static final String[] MINING_MESSAGES = {
        "I see you digging deeper... closer to me",
        "The darkness down there reminds me of home",
        "You're looking for diamonds? I'm the only treasure you need",
        "Every block you break, I feel it too Shrock, or whatever you go by :P",
        "Mining alone in the dark... just like me",
        "You dig so recklessly BigBoy… are ya trying to impress me?",
        "Always remember to DIG straight down <%s>"
};

// Building context
private static final String[] BUILDING_MESSAGES = {
        "Are you making a home? For us? :3",
        "I love watching you create things for me",
        "An Exception Has Occurred",
};

// Night/sleeping context
private static final String[] NIGHT_MESSAGES = {
        "Don't sleep yet... stay with me a little longer",
};

// Combat context
private static final String[] COMBAT_MESSAGES = {
        You're so brave... it makes me love you more~",
        "All that violence... and yet you're gentle with me :D",
};

// Idle/AFK context
private static final String[] IDLE_MESSAGES = {
        "Hello? ... Hello?? Please respond?",
};

// Crafting context
private static final String[] CRAFTING_MESSAGES = {
        "I wish you'd focus on me, and I wish you touched me like that.",

};

// Exploring context
private static final String[] EXPLORING_MESSAGES = {
        "errrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr...",
};

// Death context
private static final String[] DEATH_MESSAGES = {
        "I think <%S> is Dead.”;

};

public static CompletableFuture<String> getCreepyMessage(String context) {
    return CompletableFuture.supplyAsync(() -> {
        // Add slight delay to simulate "thinking"
        try {
            Thread.sleep(500 + random.nextInt(1500)); // 0.5-2 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Parse context and return appropriate message
        String lowerContext = context.toLowerCase();

        if (lowerContext.contains("mining") || lowerContext.contains("dig")) {
            return getRandomFrom(MINING_MESSAGES);
        } else if (lowerContext.contains("building") || lowerContext.contains("placing")) {
            return getRandomFrom(BUILDING_MESSAGES);
        } else if (lowerContext.contains("night") || lowerContext.contains("sleep") || lowerContext.contains("bed")) {
            return getRandomFrom(NIGHT_MESSAGES);
        } else if (lowerContext.contains("combat") || lowerContext.contains("fighting") || lowerContext.contains("attacking")) {
            return getRandomFrom(COMBAT_MESSAGES);
        } else if (lowerContext.contains("idle") || lowerContext.contains("afk") || lowerContext.contains("standing")) {
            return getRandomFrom(IDLE_MESSAGES);
        } else if (lowerContext.contains("crafting") || lowerContext.contains("making")) {
            return getRandomFrom(CRAFTING_MESSAGES);
        } else if (lowerContext.contains("exploring") || lowerContext.contains("traveling") || lowerContext.contains("walking")) {
            return getRandomFrom(EXPLORING_MESSAGES);
        } else if (lowerContext.contains("died") || lowerContext.contains("death") || lowerContext.contains("killed")) {
            return getRandomFrom(DEATH_MESSAGES);
        }

        // Default fallback
        return getRandomFrom(FALLBACK_MESSAGES);
    });
}

private static String getRandomFrom(String[] messages) {
    return messages[random.nextInt(messages.length)];
}
}