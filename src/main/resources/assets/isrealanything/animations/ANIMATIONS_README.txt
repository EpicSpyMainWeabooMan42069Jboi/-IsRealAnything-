# GeckoLib Entity Animations

Place your Blockbench .animation.json files here:

## Required Animation Files:
1. the_me.animation.json - Animations for TheME entity
2. the_other_me.animation.json - Animations for TheOtherME entity

## Required Animations (TheME):
- animation.the_me.idle - Idle standing animation (looping)
- animation.the_me.walk - Walking animation (looping)
- animation.the_me.attack - Attack animation (play once)
- animation.the_me.death - Death animation (play once)
- animation.the_me.stalk - Stalking animation (looping, creepy)

## Required Animations (TheOtherME):
- animation.the_other_me.idle - Menacing idle (looping)
- animation.the_other_me.walk - Aggressive walk (looping)
- animation.the_other_me.attack - Violent attack (play once)
- animation.the_other_me.death - Death animation (play once)
- animation.the_other_me.teleport - Teleport effect (play once)
- animation.the_other_me.glitch - Reality distortion (play once)

## Animation Specifications:
- Export from Blockbench as GeckoLib Animation
- Format: .animation.json
- Use smooth interpolation for creepy movements
- Keep animations fluid but unsettling

## Creating Animations in Blockbench:
1. Open your GeckoLib model in Blockbench
2. Create animations using the Animation panel
3. Set keyframes for bone rotations/positions
4. Preview animations
5. Export Animation as .animation.json
6. Place in this directory

## Animation Tips:
- Idle: Slight breathing/swaying for unnatural effect
- Walk: Unnatural gait, jerky or too smooth
- Attack: Fast, aggressive movements
- Stalk: Slow, deliberate, menacing
- Glitch: Rapid position changes, distortion
- Death: Dramatic collapse or fade effect

## Timing Recommendations:
- Idle: 2-4 seconds
- Walk: Match movement speed (1-2 seconds)
- Attack: 0.5-1 second (quick and violent)
- Death: 1.5-3 seconds
- Teleport: 0.3-0.5 seconds
- Glitch: 0.2-0.4 seconds

## Animation Names MUST Match:
The animation names in the JSON must exactly match the names used in:
- TheMEEntityAnimations.java
- TheOtherMEEntityAnimations.java
