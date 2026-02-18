package com.epicspymain.isrealanything.entity.client;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

/**
 * Animation controller helper for TheME Entity.
 * Defines animation states and transitions.
 */
public class TheMEEntityAnimations {
    
    // Animation names (must match Blockbench animation names)
    public static final String IDLE = "the_me.animation";
    public static final String WALK = "animation.the_me.walk";
    public static final String DEATH = "animation.the_me.death";
    public static final String STALK = "animation.the_me.stalk";
    

    public static RawAnimation createIdleAnimation() {
        return RawAnimation.begin().thenLoop(IDLE);
    }
    
    /**
     * Creates walk animation.
     */
    public static RawAnimation createWalkAnimation() {
        return RawAnimation.begin().thenLoop(WALK);
    }
    

    public static RawAnimation createDeathAnimation() {
        return RawAnimation.begin().thenPlay(DEATH);
    }
    
    /**
     * Creates stalking animation (slower, creepier).
     */
    public static RawAnimation createStalkAnimation() {
        return RawAnimation.begin().thenLoop(STALK);
    }
}
