package com.epicspymain.isrealanything.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;


public class LangToasterOverlay extends ButtonWidget {

    private static final Identifier TOAST_TEXTURE = Identifier.of("isrealanything", "textures/gui/language_toast.png");
    private static final int TOAST_WIDTH = 300;
    private static final int TOAST_HEIGHT = 90;

    private final Screen parent;
    private final Text title;
    private final Text body;
    private final Runnable onAccept;

    private static boolean suppressedUntilRestart = false;

    private int fadeInTicks = 0;
    private int displayTicks = 0;
    private boolean dismissed = false;

    private ButtonWidget acceptButton;
    private ButtonWidget dismissButton;
    private ButtonWidget neverButton;

    public LangToasterOverlay(Screen parent, Text title, Text body, Runnable onAccept, boolean instant) {
        super(0, 0, TOAST_WIDTH, TOAST_HEIGHT, Text.empty(), btn -> {}, DEFAULT_NARRATION_SUPPLIER);

        this.parent = parent;
        this.title = title;
        this.body = body;
        this.onAccept = onAccept;
        this.fadeInTicks = instant ? 60 : 0;

        // Position at top-right corner
        this.setX(parent.width - TOAST_WIDTH - 10);
        this.setY(10);

        createButtons();
    }

    private void createButtons() {
        int buttonY = this.getY() + TOAST_HEIGHT - 30;
        int buttonWidth = 80;

        // "Yes, Please" button (creepy politeness)
        acceptButton = ButtonWidget.builder(
                        Text.literal("Yes, Please"),
                        btn -> {
                            this.onAccept.run();
                            this.dismissed = true;
                        }
                )
                .dimensions(this.getX() + 10, buttonY, buttonWidth, 20)
                .build();

        // "Not Now" button
        dismissButton = ButtonWidget.builder(
                        Text.literal("Not Now"),
                        btn -> this.dismissed = true
                )
                .dimensions(this.getX() + 95, buttonY, buttonWidth, 20)
                .build();

        // "Never" button (triggers fail message)
        neverButton = ButtonWidget.builder(
                        Text.literal("Never"),
                        btn -> {
                            suppressedUntilRestart = true;
                            this.dismissed = true;
                        }
                )
                .dimensions(this.getX() + 180, buttonY, buttonWidth, 20)
                .build();
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        if (dismissed) return;

        // Fade in animation
        if (fadeInTicks < 60) {
            fadeInTicks++;
        }

        displayTicks++;

        float alpha = MathHelper.clamp(fadeInTicks / 60.0f, 0.0f, 1.0f);

        // Render toast background
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Dark semi-transparent background
        int bgAlpha = (int)(alpha * 220);
        context.fill(
                this.getX(),
                this.getY(),
                this.getX() + TOAST_WIDTH,
                this.getY() + TOAST_HEIGHT,
                (bgAlpha << 24) | 0x1a1a1a
        );

        // Border (dark red glow)
        int borderColor = (bgAlpha << 24) | 0x8B0000;
        context.drawBorder(this.getX(), this.getY(), TOAST_WIDTH, TOAST_HEIGHT, borderColor);

        // Render title (larger, bold)
        context.drawText(
                parent.textRenderer,
                title,
                this.getX() + 10,
                this.getY() + 10,
                0xFF0000 | (bgAlpha << 24),
                true
        );

        // Render body (smaller, wrapped)
        int bodyY = this.getY() + 28;
        for (String line : wrapText(body.getString(), TOAST_WIDTH - 20)) {
            context.drawText(
                    parent.textRenderer,
                    line,
                    this.getX() + 10,
                    bodyY,
                    0xFFFFFF | (bgAlpha << 24),
                    false
            );
            bodyY += 10;
        }

        // Render buttons
        if (alpha >= 1.0f) {
            acceptButton.render(context, mouseX, mouseY, delta);
            dismissButton.render(context, mouseX, mouseY, delta);
            neverButton.render(context, mouseX, mouseY, delta);
        }

        RenderSystem.disableBlend();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (dismissed) return false;

        // Check button clicks
        if (acceptButton.mouseClicked(mouseX, mouseY, button)) return true;
        if (dismissButton.mouseClicked(mouseX, mouseY, button)) return true;
        if (neverButton.mouseClicked(mouseX, mouseY, button)) return true;

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (dismissed) return false;

        return mouseX >= this.getX() && mouseX < this.getX() + TOAST_WIDTH &&
                mouseY >= this.getY() && mouseY < this.getY() + TOAST_HEIGHT;
    }

    /**
     * Simple text wrapping
     */
    private String[] wrapText(String text, int maxWidth) {
        String[] words = text.split(" ");
        StringBuilder current = new StringBuilder();
        java.util.List<String> lines = new java.util.ArrayList<>();

        for (String word : words) {
            if (parent.textRenderer.getWidth(current + " " + word) > maxWidth) {
                if (current.length() > 0) {
                    lines.add(current.toString());
                    current = new StringBuilder(word);
                } else {
                    lines.add(word);
                }
            } else {
                if (current.length() > 0) current.append(" ");
                current.append(word);
            }
        }

        if (current.length() > 0) {
            lines.add(current.toString());
        }

        return lines.toArray(new String[0]);
    }

    /**
     * Check if user has suppressed the toast
     */
    public static boolean isSuppressedUntilRestart() {
        return suppressedUntilRestart;
    }

    /**
     * Check if toast is dismissed
     */
    public boolean isDismissed() {
        return dismissed;
    }
}