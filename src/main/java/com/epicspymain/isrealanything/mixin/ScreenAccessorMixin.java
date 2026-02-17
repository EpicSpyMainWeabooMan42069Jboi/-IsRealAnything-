package com.epicspymain.isrealanything.mixin;

import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Mixin 2: ScreenAccessor - Provides access to screen internals
 * Required for overlay rendering and custom UI elements
 */
@Mixin(Screen.class)
public interface ScreenAccessorMixin {
    
    /**
     * Get screen width
     */
    @Accessor("width")
    int getScreenWidth();
    
    /**
     * Get screen height
     */
    @Accessor("height")
    int getScreenHeight();


    /**
     * Set screen title
     */
    @Accessor("title")
    void setScreenTitle(net.minecraft.text.Text title);
}
