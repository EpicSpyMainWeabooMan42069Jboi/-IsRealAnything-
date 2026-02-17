package com.epicspymain.isrealanything.mixin;

import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Mixin 4: Window - Window manipulation for shrink/resize events
 * Controls window position and size changes
 */
@Mixin(Window.class)
public abstract class WindowMixin {
    
    @Shadow
    private int windowedX;
    
    @Shadow
    private int windowedY;
    
    @Shadow
    private int windowedWidth;
    
    @Shadow
    private int windowedHeight;
    
    @Shadow
    public abstract void setWindowedSize(int width, int height);
    

    public void isRealAnything$shrinkWindow(float percentage) {
        int newWidth = (int) (windowedWidth * percentage);
        int newHeight = (int) (windowedHeight * percentage);
        
        setWindowedSize(newWidth, newHeight);
    }
    
    /**
     * Restore original window size
     */
    public void isRealAnything$restoreWindow() {

        setWindowedSize(854, 470);
    }
    
    /**
     * Shake window effect
     */
    public void isRealAnything$shakeWindow(int intensity) {
        // Window position shake (requires native access)
        // Placeholder for future implementation
    }
}
