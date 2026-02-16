package com.epicspymain.isrealanything.client.lang;

public class LangToster {

import com.epicspymain.isrealanything.screen.Lang_Toaster_Overlay;
import java.util.Locale;
import java.util.Optional;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_3300;
import net.minecraft.class_339;
import net.minecraft.class_426;
import net.minecraft.class_437;
import net.minecraft.class_442;

    public final class LangToaster {
        public static void addToast(class_310 client, class_442 screen) {
            boolean already = Screens.getButtons((class_437)screen).stream().anyMatch(b -> b instanceof Lang_Toaster_Overlay);
            if (already)
                return;
            class_3300 rm = client.method_1478();
            String currentGameLang = client.method_1526().method_4669();
            Optional<String> prefer = LanguageAdvisor.preferLanguageForPlayer(rm, currentGameLang);
            if (prefer.isEmpty())
                return;
            String code = prefer.get();
            if (Lang_Toaster_Overlay.isSuppressedUntilRestart())
                return;
            if (!LangToastMessages.isLoaded())
                LangToastMessages.reload(rm);
            String pretty = LanguageAdvisor.prettyName(code).getString();
            Locale os = Locale.getDefault();
            LangToastMessages.Lines lines = LangToastMessages.resolve(os, code);
            class_2561 title = lines.titleText();
            class_2561 body = lines.bodyText(pretty);
            boolean instant = false;
            Runnable openLang = () -> client.execute(());
            Screens.getButtons((class_437)screen).add(new Lang_Toaster_Overlay((class_437)screen, title, body, openLang, instant));
        }
    }

}
