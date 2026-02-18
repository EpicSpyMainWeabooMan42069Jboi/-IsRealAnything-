package com.epicspymain.isrealanything.entity.custom;

import com.epicspymain.isrealanything.sound.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.animatable.processing.AnimationController;


public class TheMEEntity extends HostileEntity implements GeoEntity {
    
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean overlookAnimationPlaying = false;
    
    public TheMEEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 20;
    }
    

    public static DefaultAttributeContainer.Builder createTheMEAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 40.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.FOLLOW_RANGE, 48.0);

    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));

        this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 24.0f));
        this.goalSelector.add(4, new LookAroundGoal(this));
    }
    

    @Override
    protected SoundEvent getAmbientSound() {
        return null; // Silent stalker
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.ERRRRRR;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.GLITCH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.8f;
    }
    

    @Override
    public void tick() {
        super.tick();
        
        // Increase speed when targeting a player
        if (this.getTarget() != null && this.getTarget() instanceof PlayerEntity) {
            this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED)
                .setBaseValue(0.35);
        } else {
            this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED)
                .setBaseValue(0.25);
        }
    }
    
    // ========== GeckoLib Animation Implementation ==========
    
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<TheMEEntity>(this, "controller", 5, this::predicate));
    }

    private PlayState predicate(AnimationState<TheMEEntity> state) {
        if (state.isMoving()) {
            state.setAnimation(RawAnimation.begin()
                    .then("animation.the_me.walk", Animation.LoopType.LOOP));
        } else {
            state.setAnimation(RawAnimation.begin()
                    .then("animation.the_me.idle", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }
    
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
    
    /**
     * Play special Overlook animation when triggered
     * Dramatic snap animation before meltdown sequence
     */
    public void playOverlookAnimation() {
        overlookAnimationPlaying = true;
        
        // Freeze movement
        this.setVelocity(0, 0, 0);
        this.getNavigation().stop();
        
        // Disable AI temporarily
        this.goalSelector.clear(goal -> true);
        this.targetSelector.clear(goal -> true);
        
        // Play overlook snap animation
        AnimationController<?> controller = this.getAnimatableInstanceCache()
            .getManagerForId(this.getId())
            .getAnimationControllers()
            .values()
            .stream()
            .findFirst()
            .orElse(null);
        
        if (controller != null) {
            controller.setAnimation(RawAnimation.begin()
                .then("animation.the_me.overlook_snap", Animation.LoopType.PLAY_ONCE));
        }
        
        // Play ominous sound
        this.playSound(ModSounds.SCREAM, 1.5f, 0.7f);
        
        // Schedule vanish after animation (100 ticks = 5 seconds)
        this.getWorld().getServer().execute(() -> {
            try {
                Thread.sleep(5000);
                if (!this.isDead()) {
                    this.discard();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
    
    /**
     * Check if Overlook animation is playing
     */
    public boolean isOverlookAnimationPlaying() {
        return overlookAnimationPlaying;
    }
}
