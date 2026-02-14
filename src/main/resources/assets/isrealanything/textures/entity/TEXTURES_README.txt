# Entity Textures

Place your entity texture files here:

## Required Textures:
1. the_me.png - Texture for TheME entity
2. the_other_me.png - Texture for TheOtherME entity

## Texture Specifications:
- Format: PNG with transparency support
- Resolution: Depends on model UV mapping (typically 64x64 or 128x128)
- Must match UV mapping from Blockbench model
- Can include transparency for ghostly effects

## Creating Textures:
1. Export UV template from Blockbench:
   - File > Export > Export UV Mapping
2. Paint texture in image editor (GIMP, Photoshop, Paint.NET, etc.)
3. Follow UV layout from template
4. Save as PNG
5. Place in this directory

## Horror Texture Tips:
- Dark colors for ominous appearance
- Red/black for menacing effect
- Glowing eyes (use emissive textures if supported)
- Corrupted/glitchy textures for TheOtherME
- Missing textures/voids for unsettling effect
- Asymmetrical features for uncanny valley

## Recommended Color Schemes:
**TheME:**
- Dark grays and blacks
- Deep reds or purples
- White or glowing eyes
- Shadowy, indistinct features

**TheOtherME:**
- Darker than TheME
- Glitch-like patterns
- Static noise overlay
- Distorted features
- Brighter glowing eyes (cyan or red)

## Advanced Effects:
- Emissive textures for glowing parts
- Partial transparency for ghostly appearance
- Animated textures (requires custom implementation)
- Damage overlays
