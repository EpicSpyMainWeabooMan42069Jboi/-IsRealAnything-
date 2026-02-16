package com.epicspymain.isrealanything.screen;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Displays toast-like notifications with custom messages from lang files
 * Renders custom toasts that can override or supplement vanilla toasts
 */
public class LangToasterOverlay {
    private static final List<CustomToast> activeToasts = new ArrayList<>();
    private static final int TOAST_WIDTH = 160;
    private static final int TOAST_HEIGHT = 32;
    private static final int DISPLAY_TIME = 5000; // 5 seconds

    public static void register() {
        HudRenderCallback.EVENT.register(LangToasterOverlay::render);
    }

    private static void render(DrawContext context, RenderTickCounter tickCounter) {
        if (activeToasts.isEmpty()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        int y = 10;

        Iterator<CustomToast> iterator = activeToasts.iterator();
        while (iterator.hasNext()) {
            CustomToast toast = iterator.next();
            
            if (System.currentTimeMillis() - toast.startTime > DISPLAY_TIME) {
                iterator.remove();
                continue;
            }

            renderToast(context, width - TOAST_WIDTH - 10, y, toast);
            y += TOAST_HEIGHT + 5;
        }
    }

    private static void renderToast(DrawContext context, int x, int y, CustomToast toast) {
        // Background
        int bgColor = toast.isError ? 0xE0800000 : 0xE0202020;
        context.fill(x, y, x + TOAST_WIDTH, y + TOAST_HEIGHT, bgColor);
        
        // Border
        int borderColor = toast.isError ? 0xFFFF0000 : 0xFF888888;
        context.drawBorder(x, y, TOAST_WIDTH, TOAST_HEIGHT, borderColor);

        // Icon (if present)
        if (toast.icon != null) {
            context.drawTexture(toast.icon, x + 6, y + 8, 0, 0, 16, 16, 16, 16);
        }

        MinecraftClient client = MinecraftClient.getInstance();
        
        // Title
        int textX = toast.icon != null ? x + 28 : x + 8;
        context.drawTextWithShadow(
            client.textRenderer,
            toast.title,
            textX,
            y + 7,
            toast.titleColor
        );

        // Subtitle
        if (toast.subtitle != null) {
            context.drawTextWithShadow(
                client.textRenderer,
                toast.subtitle,
                textX,
                y + 18,
                toast.subtitleColor
            );
        }

        // Progress bar (fade out)
        long elapsed = System.currentTimeMillis() - toast.startTime;
        float progress = 1.0f - ((float) elapsed / DISPLAY_TIME);
        int barWidth = (int) (TOAST_WIDTH * progress);
        context.fill(x, y + TOAST_HEIGHT - 2, x + barWidth, y + TOAST_HEIGHT, 0xFF00FF00);
    }

    public static void showToast(String title, String subtitle, boolean isError) {
        activeToasts.add(new CustomToast(
            Text.literal(title),
            subtitle != null ? Text.literal(subtitle) : null,
            null,
            isError,
            0xFFFFFFFF,
            0xFFAAAAAA
        ));
    }

    public static void showToast(Text title, Text subtitle, Identifier icon, boolean isError, int titleColor, int subtitleColor) {
        activeToasts.add(new CustomToast(title, subtitle, icon, isError, titleColor, subtitleColor));
    }

    public static void showWarning(String message) {
        showToast("§cWarning", message, true);
    }

    public static void showInfo(String message) {
        showToast("§bInfo", message, false);
    }

    public static void showCustom(String title, String subtitle) {
        showToast(title, subtitle, false);
    }

    public static void clearAll() {
        activeToasts.clear();
    }

    private static class CustomToast {
        final Text title;
        final Text subtitle;
        final Identifier icon;
        final boolean isError;
        final int titleColor;
        final int subtitleColor;
        final long startTime;

        CustomToast(Text title, Text subtitle, Identifier icon, boolean isError, int titleColor, int subtitleColor) {
            this.title = title;
            this.subtitle = subtitle;
            this.icon = icon;
            this.isError = isError;
            this.titleColor = titleColor;
            this.subtitleColor = subtitleColor;
            this.startTime = System.currentTimeMillis();
        }
    }

    /**
     * Spam toasts rapidly for overwhelming effect
     */
    public static void spamToasts(String baseMessage, int count) {
        for (int i = 0; i < count; i++) {
            final int index = i;
            MinecraftClient.getInstance().execute(() -> {
                showToast(baseMessage, "Message #" + index, false);
            });
        }
    }
}
