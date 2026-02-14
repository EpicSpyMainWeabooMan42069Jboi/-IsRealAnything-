package com.epicspymain.isrealanything.mixin;

import com.epicspymain.isrealanything.events.GlitchCorruptionEvent;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin 3: Sign - Sign text corruption
 * Applies glitch corruption to player-placed signs
 */
@Mixin(SignBlockEntity.class)
public class SignMixin {
    
    /**
     * Corrupt sign text when player edits it
     */
    @Inject(
        method = "setText",
        at = @At("HEAD")
    )
    private void corruptSignText(
        SignText signText,
        boolean front,
        CallbackInfoReturnable<Boolean> cir
    ) {
        // Check if corruption should happen (every 15 minutes)
        SignBlockEntity sign = (SignBlockEntity) (Object) this;
        
        if (sign.getWorld() != null && GlitchCorruptionEvent.shouldCorruptText(sign.getWorld().getTime())) {
            // Corrupt each line of text
            for (int i = 0; i < 4; i++) {
                Text originalText = signText.getMessage(i, false);
                String corrupted = GlitchCorruptionEvent.corruptText(originalText.getString());
                
                // Note: Actual text modification requires more complex NBT handling
                // This is a simplified version
            }
        }
    }
}
