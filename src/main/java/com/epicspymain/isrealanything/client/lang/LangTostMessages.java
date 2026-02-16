package com.epicspymain.isrealanything.client.lang;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_3298;
import net.minecraft.class_3300;
import org.jetbrains.annotations.Nullable;

    public final class LangToastMessages {
        public static final class Lines extends Record {
            private final String title;

            private final String body;

            public Lines(String title, String body) {
                this.title = title;
                this.body = body;
            }

            public final String toString() {
                // Byte code:
                //   0: aload_0
                //   1: <illegal opcode> toString : (Lcom/epicspymain/isrealanything/client/lang/LangToastMessages$Lines;)Ljava/lang/String;
                //   6: areturn
                // Line number table:
                //   Java source line number -> byte code offset
                //   #18	-> 0
                // Local variable table:
                //   start	length	slot	name	descriptor
                //   0	7	0	this	Lcom/epicspymain/isrealanything/client/lang/LangToastMessages$Lines;
            }

            public final int hashCode() {
                // Byte code:
                //   0: aload_0
                //   1: <illegal opcode> hashCode : (Lcom/epicspymain/isrealanything/client/lang/LangToastMessages$Lines;)I
                //   6: ireturn
                // Line number table:
                //   Java source line number -> byte code offset
                //   #18	-> 0
                // Local variable table:
                //   start	length	slot	name	descriptor
                //   0	7	0	this	Lcom/epicspymain/isrealanything/client/lang/LangToastMessages$Lines;
            }

            public final boolean equals(Object o) {
                // Byte code:
                //   0: aload_0
                //   1: aload_1
                //   2: <illegal opcode> equals : (Lcom/epicspymain/isrealanything/client/lang/LangToastMessages$Lines;Ljava/lang/Object;)Z
                //   7: ireturn
                // Line number table:
                //   Java source line number -> byte code offset
                //   #18	-> 0
                // Local variable table:
                //   start	length	slot	name	descriptor
                //   0	8	0	this	Lcom/epicspymain/isrealanything/client/lang/LangToastMessages$Lines;
                //   0	8	1	o	Ljava/lang/Object;
            }

            public String title() {
                return this.title;
            }

            public String body() {
                return this.body;
            }

            class_2561 titleText() {
                return (class_2561)class_2561.method_43470(this.title);
            }

            class_2561 bodyText(String prettyLang) {
                return (class_2561)class_2561.method_43470(this.body.replace("{language}", prettyLang));
            }
        }

        private static final Gson GSON = new Gson();

        private static final class_2960 FILE = class_2960.method_60655("isrealanything", "lang_overlay_mgs.json");

        private static Map<String, Lines> map = Collections.emptyMap();

        private static Map<String, String> aliases = Collections.emptyMap();

        private static Lines def = new Lines("what's this?", "English ONLY");

        private static Lines fail = new Lines("Recommendation", "I encourage you to play this with me and ONLY ME");

        private static boolean loaded = false;

        private static String norm(String s) {
            return s.toLowerCase(Locale.ROOT).replace('-', '_');
        }

        public static boolean isLoaded() {
            return loaded;
        }

        public static synchronized void reload(class_3300 rm) {
            loaded = false;
            map = new HashMap<>();
            aliases = new HashMap<>();
            try {
                Optional<class_3298> res = rm.method_14486(FILE);
                if (res.isEmpty()) {
                    System.out.println("[LangToast] JSON not found at assets/isrealanything/lang_overlay_mgs.json -> using defaults");
                    loaded = true;
                    return;
                }
                InputStreamReader in = new InputStreamReader(((class_3298)res.get()).method_14482(), StandardCharsets.UTF_8);
                try {
                    JsonObject root = (JsonObject)GSON.fromJson(in, JsonObject.class);
                    if (root.has("default")) {
                        JsonObject o = root.getAsJsonObject("default");
                        def = new Lines(o.get("title").getAsString(), o.get("body").getAsString());
                    }
                    if (root.has("fail")) {
                        JsonObject o = root.getAsJsonObject("fail");
                        fail = new Lines(o.get("title").getAsString(), o.get("body").getAsString());
                    }
                    if (root.has("aliases")) {
                        JsonObject o = root.getAsJsonObject("aliases");
                        for (Map.Entry<String, JsonElement> e : (Iterable<Map.Entry<String, JsonElement>>)o.entrySet())
                            aliases.put(norm(e.getKey()), norm(((JsonElement)e.getValue()).getAsString()));
                    }
                    for (Map.Entry<String, JsonElement> e : (Iterable<Map.Entry<String, JsonElement>>)root.entrySet()) {
                        String k = e.getKey();
                        if ("default".equals(k) || "aliases".equals(k) || "fail".equals(k))
                            continue;
                        JsonObject o = ((JsonElement)e.getValue()).getAsJsonObject();
                        String key = norm(k);
                        map.put(key, new Lines(o.get("title").getAsString(), o.get("body").getAsString()));
                    }
                    loaded = true;
                    in.close();
                } catch (Throwable throwable) {
                    try {
                        in.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                    throw throwable;
                }
            } catch (Exception ex) {
                System.out.println("[LangToast] reload failed: " + ex.getMessage());
                loaded = true;
            }
        }

        public static Lines failLines() {
            return fail;
        }

        public static Lines resolve(@Nullable Locale os, String suggestedCode) {
            String code = norm(suggestedCode);
            Lines out = map.get(code);
            if (out != null)
                return out;
            String ali = aliases.get(code);
            if (ali != null) {
                out = map.get(ali);
                if (out != null)
                    return out;
            }
            int i = code.indexOf('_');
            if (i > 0) {
                String base = code.substring(0, i);
                out = map.get(base);
                if (out != null)
                    return out;
            }
            if (os != null) {
                String osTag = norm(os.toLanguageTag());
                out = map.get(osTag);
                if (out != null)
                    return out;
                int j = osTag.indexOf('_');
                if (j > 0) {
                    String baseOs = osTag.substring(0, j);
                    out = map.get(baseOs);
                    if (out != null)
                        return out;
                }
            }
            return def;
        }

        private static Lines lineOrDefault(String code) {
            if (code == null || code.isBlank())
                return def;
            String c = norm(code);
            Lines l = map.get(c);
            if (l != null)
                return l;
            String ali = aliases.get(c);
            if (ali != null) {
                l = map.get(ali);
                if (l != null)
                    return l;
            }
            int i = c.indexOf('_');
            if (i > 0) {
                String base = c.substring(0, i);
                l = map.get(base);
                if (l != null)
                    return l;
                for (Map.Entry<String, Lines> e : map.entrySet()) {
                    if (((String)e.getKey()).startsWith(base + "_"))
                        return e.getValue();
                }
            }
            return def;
        }
    }

}
