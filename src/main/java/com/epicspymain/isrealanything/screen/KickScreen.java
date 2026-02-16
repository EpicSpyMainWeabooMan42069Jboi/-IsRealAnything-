package com.epicspymain.isrealanything.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;

/**
 * Fake disconnect/kick screen that appears to disconnect the player
 * but actually keeps the game running in the background
 */
public class KickScreen extends Screen {
    private final Screen parent;
    private final String reason;
    private int ticksOpen = 0;
    private boolean canClose = false;

    public KickScreen(Screen parent, String disconnectReason) {
        super(Text.literal("Disconnected"));
        this.parent = parent;
        this.reason = disconnectReason;
    }

    @Override
    protected void init() {
        super.init();
        
        // Add "Back to Title Screen" button (doesn't actually disconnect)
        this.addDrawableChild(ButtonWidget.builder(
            Text.translatable("gui.toTitle"),
            button -> this.close()
        ).dimensions(this.width / 2 - 100, this.height / 2 + 50, 200, 20).build());

        // Add "Reconnect" button that just closes the screen
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Reconnect"),
            button -> this.close()
        ).dimensions(this.width / 2 - 100, this.height / 2 + 75, 200, 20).build());
    }

    @Override
    public void tick() {
        super.tick();
        ticksOpen++;
        
        // Allow closing after 3 seconds or pressing ESC
        if (ticksOpen > 60) {
            canClose = true;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Dark background
        context.fillGradient(0, 0, this.width, this.height, 0xC0101010, 0xD0101010);

        // Title
        context.drawCenteredTextWithShadow(
            this.textRenderer,
            this.title,
            this.width / 2,
            this.height / 2 - 60,
            0xFFFFFF
        );

        // Disconnect reason
        String[] lines = wrapText(reason, 40);
        int startY = this.height / 2 - 30;
        for (int i = 0; i < lines.length; i++) {
            context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal(lines[i]),
                this.width / 2,
                startY + (i * 12),
                0xAAAAAA
            );
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        if (canClose || ticksOpen > 60) {
            MinecraftClient.getInstance().setScreen(parent);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return canClose;
    }

    @Override
    public boolean shouldPause() {
        return false; // Game continues running
    }

    private String[] wrapText(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return new String[]{text};
        }

        String[] words = text.split(" ");
        StringBuilder current = new StringBuilder();
        java.util.List<String> lines = new java.util.ArrayList<>();

        for (String word : words) {
            if (current.length() + word.length() + 1 > maxLength) {
                if (current.length() > 0) {
                    lines.add(current.toString());
                    current = new StringBuilder();
                }
            }
            if (current.length() > 0) {
                current.append(" ");
            }
            current.append(word);
        }

        if (current.length() > 0) {
            lines.add(current.toString());
        }

        return lines.toArray(new String[0]);
    }

    public static void show(String reason) {
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> {
            client.setScreen(new KickScreen(null, reason));
        });
    }
}
