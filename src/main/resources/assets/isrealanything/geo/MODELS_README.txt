# GeckoLib Entity Models

Place your Blockbench .geo.json model files here:

## Required Models:
1. the_me.geo.json - Model for TheME entity
2. the_other_me.geo.json - Model for TheOtherME entity

## Model Specifications:
- Export from Blockbench as GeckoLib Animated Model
- Format: .geo.json (GeckoLib geometry format)
- Recommended dimensions: 0.6 wide x 1.95 tall (similar to player)
- Must include proper bone structure for animations

## Creating Models in Blockbench:
1. Open Blockbench (https://www.blockbench.net/)
2. Create New > GeckoLib Animated Model
3. Model your horror entity
4. Export as GeckoLib Model (.geo.json)
5. Place the exported file in this directory

## Model Structure:
- Root bone (for positioning)
- Head bone (for look-at behavior)
- Body bones (for animations)
- Limb bones (arms, legs for movement)

## Tips:
- Keep polygon count reasonable for performance
- Use proper bone hierarchy
- Test animations in Blockbench before exporting
- Name bones consistently with animation files
