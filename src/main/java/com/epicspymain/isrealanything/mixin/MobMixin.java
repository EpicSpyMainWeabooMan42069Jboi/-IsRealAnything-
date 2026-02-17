package com.epicspymain.isrealanything.mixin;

import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin 5: Mob - Possession system for MyMobPals event
 * Stores and modifies mob behavior during possession
 */
@Mixin(MobEntity.class)
public abstract class MobMixin {
    
    @Unique
    private boolean isRealAnything$possessed = false;
    
    @Unique
    private double isRealAnything$originalSpeed = 0;
    
    @Unique
    private double isRealAnything$originalDamage = 0;
    
    @Unique
    private int isRealAnything$possessionDuration = 0;
    
    /**
     * Tick possession state
     */
    @Inject(
        method = "tick",
        at = @At("HEAD")
    )
    private void tickPossession(CallbackInfo ci) {
        MobEntity mob = (MobEntity) (Object) this;
        
        if (isRealAnything$possessed) {
            isRealAnything$possessionDuration--;
            
            // Check if possession ended
            if (isRealAnything$possessionDuration <= 0) {
                isRealAnything$restore();
            }
            
            // Enhanced possession behavior
            // Mobs look directly at target more aggressively
            if (mob.getTarget() != null) {
                mob.getLookControl().lookAt(mob.getTarget(), 180f, 180f);
            }
        }
    }
    
    /**
     * Possess this mob
     */
    @Unique
    public void isRealAnything$possess(int duration) {
        MobEntity mob = (MobEntity) (Object) this;
        
        isRealAnything$possessed = true;
        isRealAnything$possessionDuration = duration;
        
        // Store original stats (already done in MyMobPalsEvent)
        // This mixin provides enhanced visual/behavioral effects
    }
    
    /**
     * Restore mob to normal
     */
    @Unique
    private void isRealAnything$restore() {
        isRealAnything$possessed = false;
        isRealAnything$possessionDuration = 0;
        
        // Stats restoration handled by MyMobPalsEvent
    }
    
    /**
     * Check if possessed
     */
    @Unique
    public boolean isRealAnything$isPossessed() {
        return isRealAnything$possessed;
    }
    
    /**
     * Set possessed state
     */
    @Unique
    public void isRealAnything$setPossessed(boolean possessed) {
        this.isRealAnything$possessed = possessed;
    }
}
