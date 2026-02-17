package com.epicspymain.isrealanything.client.lang;

import com.epicspymain.isrealanything.screen.LangToasterOverlay;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Text;

import java.util.Locale;
import java.util.Optional;

/**
 * Adds language suggestion toast to title screen
 * Detects player's real location and suggests native language
 */
public final class LangToaster {

    /**
     * Add language suggestion toast to screen if appropriate
     */
    public static void addToast(MinecraftClient client, TitleScreen screen) {
        // Check if already has toast
        boolean already = Screens.getButtons(screen).stream()
                .anyMatch(b -> b instanceof LangToasterOverlay);

        if (already) return;

        ResourceManager rm = client.getResourceManager();
        String currentGameLang = client.getLanguageManager().getLanguage();

        // Determine if we should suggest a different language
        Optional<String> prefer = LanguageAdvisor.preferLanguageForPlayer(rm, currentGameLang);
        if (prefer.isEmpty()) return;

        String code = prefer.get();

        // Check if user suppressed this
        if (LangToasterOverlay.isSuppressedUntilRestart()) return;

        // Load messages
        if (!LangToastMessages.isLoaded()) {
            LangToastMessages.reload(rm);
        }

        String pretty = LanguageAdvisor.prettyName(code).getString();
        Locale os = Locale.getDefault();

        LangToastMessages.Lines lines = LangToastMessages.resolve(os, code);
        Text title = lines.titleText();
        Text body = lines.bodyText(pretty);

        // Action to open language settings
        Runnable openLang = () -> client.execute(() -> {
            // Open language selection screen
            client.setScreen(new net.minecraft.client.gui.screen.option.LanguageOptionsScreen(
                    screen,
                    client.options,
                    client.getLanguageManager()
            ));
        });

        // Add the toast overlay
        Screens.getButtons(screen).add(
                new LangToasterOverlay(screen, title, body, openLang, false)
        );
    }
}