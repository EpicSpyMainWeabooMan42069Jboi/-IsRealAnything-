package com.epicspymain.isrealanything.client.lang;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Loads and manages language-specific toast messages
 * Messages change based on player's detected language/location
 */
public final class LangToastMessages {

    /**
     * Container for toast title and body text
     */
    public record Lines(String title, String body) {

        public Text titleText() {
            return Text.literal(this.title);
        }

        public Text bodyText(String prettyLang) {
            return Text.literal(this.body.replace("{language}", prettyLang));
        }
    }

    private static final Gson GSON = new Gson();
    private static final Identifier FILE = Identifier.of("isrealanything", "lang_overlay_msgs.json");

    private static Map<String, Lines> map = Collections.emptyMap();
    private static Map<String, String> aliases = Collections.emptyMap();

    // Default messages
    private static Lines def = new Lines(
            "What's this?",
            "English ONLY"
    );

    // Fail message - used when player ignores language suggestion
    private static Lines fail = new Lines(
            "Recommendation",
            "I encourage you to play this correctly dipshit"
    );

    private static boolean loaded = false;

    /**
     * Normalize language code to lowercase with underscores
     */
    private static String norm(String s) {
        return s.toLowerCase(Locale.ROOT).replace('-', '_');
    }

    public static boolean isLoaded() {
        return loaded;
    }

    /**
     * Load language messages from JSON resource
     */
    public static synchronized void reload(ResourceManager rm) {
        loaded = false;
        map = new HashMap<>();
        aliases = new HashMap<>();

        try {
            Optional<Resource> res = rm.getResource(FILE);
            if (res.isEmpty()) {
                System.out.println("[LangToast] JSON not found at assets/isrealanything/lang_overlay_msgs.json -> using defaults");
                loaded = true;
                return;
            }

            try (InputStreamReader in = new InputStreamReader(res.get().getInputStream(), StandardCharsets.UTF_8)) {
                JsonObject root = GSON.fromJson(in, JsonObject.class);

                // Load default message
                if (root.has("default")) {
                    JsonObject o = root.getAsJsonObject("default");
                    def = new Lines(
                            o.get("title").getAsString(),
                            o.get("body").getAsString()
                    );
                }

                // Load fail message
                if (root.has("fail")) {
                    JsonObject o = root.getAsJsonObject("fail");
                    fail = new Lines(
                            o.get("title").getAsString(),
                            o.get("body").getAsString()
                    );
                }

                // Load language aliases
                if (root.has("aliases")) {
                    JsonObject o = root.getAsJsonObject("aliases");
                    for (Map.Entry<String, JsonElement> e : o.entrySet()) {
                        aliases.put(norm(e.getKey()), norm(e.getValue().getAsString()));
                    }
                }

                // Load language-specific messages
                for (Map.Entry<String, JsonElement> e : root.entrySet()) {
                    String k = e.getKey();
                    if ("default".equals(k) || "aliases".equals(k) || "fail".equals(k)) {
                        continue;
                    }

                    JsonObject o = e.getValue().getAsJsonObject();
                    String key = norm(k);
                    map.put(key, new Lines(
                            o.get("title").getAsString(),
                            o.get("body").getAsString()
                    ));
                }

                loaded = true;
            }

        } catch (Exception ex) {
            System.out.println("[LangToast] reload failed: " + ex.getMessage());
            ex.printStackTrace();
            loaded = true;
        }
    }

    /**
     * Get the "fail" message (when player ignores language suggestion)
     */
    public static Lines failLines() {
        return fail;
    }

    /**
     * Resolve the appropriate message for a language code
     */
    public static Lines resolve(Locale os, String suggestedCode) {
        String code = norm(suggestedCode);

        // Try exact match
        Lines out = map.get(code);
        if (out != null) return out;

        // Try alias
        String ali = aliases.get(code);
        if (ali != null) {
            out = map.get(ali);
            if (out != null) return out;
        }

        // Try base language (e.g., "pt" from "pt_br")
        int i = code.indexOf('_');
        if (i > 0) {
            String base = code.substring(0, i);
            out = map.get(base);
            if (out != null) return out;
        }

        // Try OS locale
        if (os != null) {
            String osTag = norm(os.toLanguageTag());
            out = map.get(osTag);
            if (out != null) return out;

            int j = osTag.indexOf('_');
            if (j > 0) {
                String baseOs = osTag.substring(0, j);
                out = map.get(baseOs);
                if (out != null) return out;
            }
        }

        return def;
    }
}