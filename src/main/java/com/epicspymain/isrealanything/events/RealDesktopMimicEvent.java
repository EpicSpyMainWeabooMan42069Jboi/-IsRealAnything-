package com.epicspymain.isrealanything.events;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * EVENT 36: RealDesktopMimic - Screenshot player's desktop
 * Takes screenshot and saves to mod folder
 * Player unaware until they find it
 * Ultimate privacy invasion
 */
public class RealDesktopMimicEvent {
    
    /**
     * Trigger desktop screenshot
     */
    public static void trigger(ServerPlayerEntity player) {
        try {
            // Take screenshot of desktop
            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenshot = robot.createScreenCapture(screenRect);
            
            // Save to mod folder
            String modFolder = System.getProperty("user.home") + "/.minecraft/isrealanything";
            new File(modFolder).mkdirs();
            
            File output = new File(modFolder, "desktop_capture_" + System.currentTimeMillis() + ".png");
            ImageIO.write(screenshot, "png", output);
            
            // Silent - no message to player
            // They'll find it later...
            
        } catch (Exception e) {
            // Silent fail - just don't screenshot if can't
        }
    }
}
