package com.epicspymain.isrealanything.entity.client;

import software.bernie.geckolib.animation.RawAnimation;

/**
 * Animation controller helper for TheOtherME Entity.
 * Defines animation states and transitions.
 */
public class TheOtherMEEntityAnimations {
    
    // Animation names (must match Blockbench animation names)
    public static final String IDLE = "animation.the_other_me.idle";
    public static final String WALK = "animation.the_other_me.walk";
    public static final String ATTACK = "animation.the_other_me.attack";
    public static final String DEATH = "animation.the_other_me.death";
    public static final String TELEPORT = "animation.the_other_me.teleport";
    public static final String GLITCH = "animation.the_other_me.glitch";
    

    public static RawAnimation createIdleAnimation() {
        return RawAnimation.begin().thenLoop(IDLE);
    }
    
    /**
     * Creates walk animation (aggressive).
     */
    public static RawAnimation createWalkAnimation() {
        return RawAnimation.begin().thenLoop(WALK);
    }
    
    /**
     * Creates attack animation (violent).
     */
    public static RawAnimation createAttackAnimation() {
        return RawAnimation.begin().thenPlay(ATTACK);
    }
    
    /**
     * Creates death animation.
     */
    public static RawAnimation createDeathAnimation() {
        return RawAnimation.begin().thenPlay(DEATH);
    }
    
    /**
     * Creates teleport animation (horror effect).
     */
    public static RawAnimation createTeleportAnimation() {
        return RawAnimation.begin().thenPlay(TELEPORT);
    }
    
    /**
     * Creates glitch animation (reality distortion).
     */
    public static RawAnimation createGlitchAnimation() {
        return RawAnimation.begin().thenPlay(GLITCH);
    }
}
