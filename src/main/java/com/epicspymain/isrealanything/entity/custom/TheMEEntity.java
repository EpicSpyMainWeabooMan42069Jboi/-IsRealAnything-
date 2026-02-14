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
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * TheME Entity - Primary horror entity for IsRealAnything mod.
 * An eerie, stalking entity that follows and watches the player.
 * Uses GeckoLib for smooth animations.
 */
public class TheMEEntity extends HostileEntity implements GeoEntity {
    
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    
    public TheMEEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 20;
    }
    
    /**
     * Sets default entity attributes.
     */
    public static DefaultAttributeContainer.Builder createTheMEAttributes() {
        return HostileEntity.createHostileAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0)
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0)
            .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5);
    }
    
    /**
     * Initializes AI goals for the entity.
     */
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 24.0f));
        this.goalSelector.add(4, new LookAroundGoal(this));
        
        this.targetSelector.add(1, new RevengeTargetGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }
    
    /**
     * Ambient sound for the entity.
     */
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.BREATHING;
    }
    
    /**
     * Sound when entity takes damage.
     */
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.ERRRRRR;
    }
    
    /**
     * Sound when entity dies.
     */
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SCREAM;
    }
    
    /**
     * Controls ambient sound chance.
     */
    @Override
    protected float getSoundVolume() {
        return 0.8f;
    }
    
    /**
     * Makes the entity slightly faster when chasing.
     */
    @Override
    public void tick() {
        super.tick();
        
        // Increase speed when targeting a player
        if (this.getTarget() != null && this.getTarget() instanceof PlayerEntity) {
            this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                .setBaseValue(0.35);
        } else {
            this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                .setBaseValue(0.25);
        }
    }
    
    // ========== GeckoLib Animation Implementation ==========
    
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
    }
    
    private PlayState predicate(AnimationState<TheMEEntity> state) {
        // Walking animation
        if (state.isMoving()) {
            state.getController().setAnimation(RawAnimation.begin()
                .then("animation.the_me.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        
        // Attacking animation
        if (this.isAttacking()) {
            state.getController().setAnimation(RawAnimation.begin()
                .then("animation.the_me.attack", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        
        // Idle animation
        state.getController().setAnimation(RawAnimation.begin()
            .then("animation.the_me.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }
    
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
