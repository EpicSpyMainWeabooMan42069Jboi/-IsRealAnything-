package com.epicspymain.isrealanything.entity.custom;

import com.epicspymain.isrealanything.sound.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
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


public class TheOtherMEEntity extends HostileEntity implements GeoEntity {
    
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int teleportCooldown = 0;
    private boolean overlookAnimationPlaying = false;

    public TheOtherMEEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 30;
    }
    

    public static DefaultAttributeContainer.Builder createTheOtherMEAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 40.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.FOLLOW_RANGE, 48.0);
    }
    
    /**
     * Initializes AI goals for the entity.
     */
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.9));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 32.0f));
        this.goalSelector.add(4, new LookAroundGoal(this));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.GLITCH; // Glitchy ambient
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.ERRRRRR;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.TED_LEWIS_FUCK_YOU; // Surprise on death
    }

    @Override
    protected float getSoundVolume() {
        return 0.9f;
    }
    
    /**
     * Custom tick behavior - adds teleportation and status effects.
     */
    @Override
    public void tick() {
        super.tick();
        
        // Teleport cooldown
        if (teleportCooldown > 0) {
            teleportCooldown--;
        }
        
        // Aggressive speed boost when targeting player
        if (this.getTarget() != null && this.getTarget() instanceof PlayerEntity) {
            this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED)                .setBaseValue(0.4);
            
            // Attempt to teleport behind player occasionally
            if (teleportCooldown == 0 && this.random.nextFloat() < 0.01f) {
                attemptTeleportBehindTarget();
                teleportCooldown = 200; // 10 seconds
            }
        } else {
            this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED)                .setBaseValue(0.3);
        }
    }
    
    /**
     * Inflicts status effects when attacking.
     */
    @Override
    public boolean tryAttack(net.minecraft.server.world.ServerWorld world, net.minecraft.entity.Entity target) {
        boolean attacked = super.tryAttack(world, target);
        
        if (attacked && target instanceof PlayerEntity player) {
            // Apply blindness and slowness
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60, 0));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 1));
            
            // Play horror sound
            this.getWorld().playSoundFromEntity(null, this, ModSounds.REALITY,
                this.getSoundCategory(), 1.0f, 1.0f);
        }
        
        return attacked;
    }
    
    /**
     * Attempts to teleport behind the target (horror effect).
     */
    private void attemptTeleportBehindTarget() {
        if (this.getTarget() == null) {
            return;
        }
        
        PlayerEntity target = (PlayerEntity) this.getTarget();
        double distance = 3.0;
        
        // Calculate position behind player
        double radians = Math.toRadians(target.getYaw());
        double x = target.getX() - distance * Math.sin(radians);
        double y = target.getY();
        double z = target.getZ() + distance * Math.cos(radians);
        
        // Teleport
        this.teleport(x, y, z, true);

    }
    
    // ========== GeckoLib Animation Implementation ==========
    
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<TheOtherMEEntity>(this, "controller", 5, this::predicate));
    }

    private PlayState predicate(AnimationState<TheOtherMEEntity> state) {
        if (state.isMoving()) {
            state.setAnimation(RawAnimation.begin()
                    .then("animation.the_other_me.walk", Animation.LoopType.LOOP));
        } else {
            state.setAnimation(RawAnimation.begin()
                    .then("animation.the_other_me.idle", Animation.LoopType.LOOP));
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
                .then("animation.the_other_me.overlook_snap", Animation.LoopType.PLAY_ONCE));
        }
        
        // Play ominous sound
        this.playSound(ModSounds.SCREAM, 1.5f, 0.5f);
        
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
