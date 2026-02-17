package com.epicspymain.isrealanything.event;

import java.util.*;

/**
 * EVENT 16: GlitchCorruption - Text corruption and glitching
 * Alters text player types:
 * - o→0, l→i, e→3
 * - Words rearranged or reversed
 * - Random entity name letters inserted
 * Affects crafting table messages and player-placed signs
 * Persists until manually rewritten
 * Triggers every 15 minutes
 * 
 * Note: This class provides corruption logic.
 * Actual text interception requires mixins.
 */
public class GlitchCorruptionEvent {
    
    private static final Random RANDOM = new Random();
    private static final String[] ENTITY_NAMES = {"TheME", "TheOtherME", "EpicSpyMain69420"};
    
    // Character replacements
    private static final Map<Character, Character> CHAR_REPLACEMENTS = new HashMap<>();
    
    static {
        CHAR_REPLACEMENTS.put('o', '0');
        CHAR_REPLACEMENTS.put('O', '0');
        CHAR_REPLACEMENTS.put('l', 'i');
        CHAR_REPLACEMENTS.put('L', 'I');
        CHAR_REPLACEMENTS.put('e', '3');
        CHAR_REPLACEMENTS.put('E', '3');
    }
    
    /**
     * Corrupt text with glitch effects
     */
    public static String corruptText(String original) {
        if (original == null || original.isEmpty()) {
            return original;
        }
        
        // Randomly choose corruption type
        float corruptionType = RANDOM.nextFloat();
        
        if (corruptionType < 0.3f) {
            return characterReplacement(original);
        } else if (corruptionType < 0.6f) {
            return wordRearrangement(original);
        } else if (corruptionType < 0.85f) {
            return entityNameInsertion(original);
        } else {
            return reverseWords(original);
        }
    }
    
    /**
     * Replace characters (o→0, l→i, e→3)
     */
    private static String characterReplacement(String text) {
        StringBuilder corrupted = new StringBuilder();
        
        for (char c : text.toCharArray()) {
            if (CHAR_REPLACEMENTS.containsKey(c) && RANDOM.nextFloat() < 0.7f) {
                corrupted.append(CHAR_REPLACEMENTS.get(c));
            } else {
                corrupted.append(c);
            }
        }
        
        return corrupted.toString();
    }
    
    /**
     * Rearrange words
     */
    private static String wordRearrangement(String text) {
        String[] words = text.split(" ");
        
        if (words.length <= 1) {
            return text; // Can't rearrange single word
        }
        
        List<String> wordList = new ArrayList<>(Arrays.asList(words));
        Collections.shuffle(wordList, RANDOM);
        
        return String.join(" ", wordList);
    }
    
    /**
     * Insert random entity name letters
     */
    private static String entityNameInsertion(String text) {
        if (text.length() < 3) {
            return text;
        }
        
        String entityName = ENTITY_NAMES[RANDOM.nextInt(ENTITY_NAMES.length)];
        StringBuilder corrupted = new StringBuilder(text);
        
        // Insert 1-3 random characters from entity name
        int insertions = 1 + RANDOM.nextInt(3);
        
        for (int i = 0; i < insertions; i++) {
            int insertPos = RANDOM.nextInt(corrupted.length());
            char insertChar = entityName.charAt(RANDOM.nextInt(entityName.length()));
            corrupted.insert(insertPos, insertChar);
        }
        
        return corrupted.toString();
    }
    
    /**
     * Reverse words
     */
    private static String reverseWords(String text) {
        String[] words = text.split(" ");
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < words.length; i++) {
            if (RANDOM.nextFloat() < 0.6f) {
                // Reverse this word
                result.append(new StringBuilder(words[i]).reverse());
            } else {
                result.append(words[i]);
            }
            
            if (i < words.length - 1) {
                result.append(" ");
            }
        }
        
        return result.toString();
    }
    
    /**
     * Corrupt crafting table message
     */
    public static String corruptCraftingMessage(String message) {
        // Always corrupt crafting messages
        return characterReplacement(message);
    }
    
    /**
     * Check if text should be corrupted (15 minute intervals)
     */
    public static boolean shouldCorruptText(long worldTime) {
        // Trigger every 15 minutes (18000 ticks)
        long timeSinceLastCorruption = worldTime % 18000;
        return timeSinceLastCorruption < 20; // 1 second window
    }
}
