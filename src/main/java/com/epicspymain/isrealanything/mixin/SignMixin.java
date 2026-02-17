package com.epicspymain.isrealanything.mixin;

import com.epicspymain.isrealanything.event.GlitchCorruptionEvent;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(SignBlockEntity.class)
public class SignMixin {

    @Inject(
            method = "setText",
            at = @At("HEAD"),
            cancellable = true
    )
    private void corruptSignText(
            SignText signText,
            boolean front,
            CallbackInfoReturnable<Boolean> cir
    ) {
        SignBlockEntity sign = (SignBlockEntity)(Object) this;

        if (sign.getWorld() != null && GlitchCorruptionEvent.shouldCorruptText(sign.getWorld().getTime())) {
            SignText corrupted = signText;

            for (int i = 0; i < 4; i++) {
                String original = signText.getMessage(i, false).getString();
                String corruptedText = GlitchCorruptionEvent.corruptText(original);
                corrupted = corrupted.withMessage(i, Text.literal(corruptedText));
            }

            // Replace with corrupted version
            ((SignBlockEntity)(Object) this).setText(corrupted, front);
            cir.setReturnValue(true);
            cir.cancel();
        }
    }