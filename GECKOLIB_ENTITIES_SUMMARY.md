# IsRealAnything - GeckoLib & Entities Summary (TASK 6)

## Overview
Complete implementation of GeckoLib animated horror entities for Minecraft 1.21.6 Fabric mod. Features two unique stalking entities with smooth animations, custom AI, and horror-themed behaviors.

---

## PART 1: GeckoLib Setup

### build.gradle Updates
Added GeckoLib 4.8 for 1.21.6:
```gradle
repositories {
    maven {
        name = "GeckoLib"
        url = "https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/"
    }
    maven {
        name = "Modrinth"
        url = "https://api.modrinth.com/maven"
    }
}

dependencies {
    modImplementation "software.bernie.geckolib:geckolib-fabric-1.21.6:4.8"
}
```

### Client Initialization
```java
// IsRealAnythingClient.java
@Override
public void onInitializeClient() {
    GeckoLib.initialize();
    // ... rest of initialization
}
```

---

## PART 2: ModEntities Registry

### ModEntities.java
Registers both horror entities:

```java
public static final EntityType<TheMEEntity> THE_ME
public static final EntityType<TheOtherMEEntity> THE_OTHER_ME
```

**Entity Specifications**:
- Dimensions: 0.6 (width) x 1.95 (height) blocks
- Spawn Group: MONSTER
- Fabric entity type builder

**Registration in IsRealAnything.java**:
```java
ModEntities.registerModEntities();
FabricDefaultAttributeRegistry.register(ModEntities.THE_ME, TheMEEntity.createTheMEAttributes());
FabricDefaultAttributeRegistry.register(ModEntities.THE_OTHER_ME, TheOtherMEEntity.createTheOtherMEAttributes());
```

---

## PART 3: TheME Entity (Primary Horror)

### TheMEEntity.java

**Core Stats**:
- Health: 40.0 HP
- Movement Speed: 0.25 (0.35 when chasing)
- Attack Damage: 8.0
- Follow Range: 48.0 blocks
- Knockback Resistance: 0.5
- Experience: 20 points

**AI Goals**:
1. Swim in water
2. Melee attack players
3. Wander around
4. Look at players (24 block range)
5. Look around randomly
6. Revenge targeting
7. Active player targeting

**Sounds**:
- Ambient: BREATHING (0.8 volume)
- Hurt: ERRRRRR
- Death: SCREAM

**Special Behavior**:
- Speed increases when targeting player (0.25 → 0.35)
- Stalking behavior (follows at distance)
- Horror-themed ambient sounds

### TheMEEntityModel.java
GeckoLib model definition:
- Geometry: `geo/the_me.geo.json`
- Texture: `textures/entity/the_me.png`
- Animations: `animations/the_me.animation.json`

### TheMEEntityRenderer.java
- Shadow radius: 0.5f
- No death rotation
- Standard GeckoLib rendering

### TheMEEntityAnimations.java
Animation definitions:
```java
IDLE = "animation.the_me.idle"
WALK = "animation.the_me.walk"
ATTACK = "animation.the_me.attack"
DEATH = "animation.the_me.death"
STALK = "animation.the_me.stalk"
```

**Animation Controller Logic**:
- Moving → Walk animation (loop)
- Attacking → Attack animation (play once)
- Idle → Idle animation (loop)

### TheMEEntitySpawner.java
Utility methods for spawning:

```java
spawnAt(World, BlockPos)                // Spawn at exact position
spawnNearPlayer(World, BlockPos, double) // Spawn around player
spawnBehindPlayer(World, BlockPos, float, double) // Behind player (horror)
canSpawnAt(World, BlockPos)             // Check valid spawn
spawnIfValid(World, BlockPos)           // Conditional spawn
```

**Spawn Conditions**:
- Light level < 7 (darkness required)
- Air blocks (2 blocks tall space)
- Within world bounds

---

## PART 4: TheOtherME Entity (Aggressive Variant)

### TheOtherMEEntity.java

**Core Stats**:
- Health: 50.0 HP (stronger than TheME)
- Movement Speed: 0.3 (0.4 when chasing)
- Attack Damage: 12.0 (50% more than TheME)
- Follow Range: 64.0 blocks (farther)
- Knockback Resistance: 0.7 (higher)
- Experience: 30 points

**AI Goals**:
1. Swim in water
2. Melee attack players (faster)
3. Wander around (faster pace)
4. Look at players (32 block range)
5. Look around randomly
6. Revenge targeting
7. Active player targeting

**Sounds**:
- Ambient: STATIC_NOISE (0.9 volume)
- Hurt: EVENT_GLITCH
- Death: LAUGH_DISTANT

**Special Abilities**:
1. **Status Effect Attacks**:
   - Blindness (3 seconds)
   - Slowness II (5 seconds)
   - Applied on hit

2. **Teleportation**:
   - Teleports behind target occasionally
   - 1% chance per tick when targeting
   - 10-second cooldown
   - Horror jumpscare effect
   - Plays glitch sound

**Special Behavior**:
- More aggressive than TheME
- Speed boost when chasing (0.3 → 0.4)
- Teleports to player's back for scares
- Inflicts debilitating status effects

### TheOtherMEEntityModel.java
GeckoLib model definition:
- Geometry: `geo/the_other_me.geo.json`
- Texture: `textures/entity/the_other_me.png`
- Animations: `animations/the_other_me.animation.json`

### TheOtherMEEntityRenderer.java
- Shadow radius: 0.6f (slightly larger)
- No death rotation
- Standard GeckoLib rendering

### TheOtherMEEntityAnimations.java
Animation definitions (6 total):
```java
IDLE = "animation.the_other_me.idle"
WALK = "animation.the_other_me.walk"
ATTACK = "animation.the_other_me.attack"
DEATH = "animation.the_other_me.death"
TELEPORT = "animation.the_other_me.teleport"
GLITCH = "animation.the_other_me.glitch"
```

**Animation Controller Logic**:
- Moving → Walk animation (loop)
- Attacking → Attack animation (play once)
- Idle → Idle animation (menacing loop)

### TheOtherMEEntitySpawner.java
Advanced spawning utilities:

```java
spawnAt(World, BlockPos)                // Basic spawn
spawnNearPlayer(World, BlockPos, double) // Around player
spawnInDarkness(World, BlockPos, int)    // Find dark spots
spawnBehindPlayer(World, BlockPos, float, double) // Jumpscare spawn
canSpawnAt(World, BlockPos)             // Validation
spawnIfValid(World, BlockPos)           // Conditional
spawnGroup(World, BlockPos, int, int)    // Multiple entities
```

**Spawn Conditions**:
- Light level < 8 (slightly less strict)
- Prefers light level < 4 for darkness spawns
- Group spawning support (horror sequences)

---

## Entity Comparison

| Feature | TheME | TheOtherME |
|---------|-------|------------|
| **Health** | 40 HP | 50 HP |
| **Damage** | 8.0 | 12.0 |
| **Speed (base)** | 0.25 | 0.3 |
| **Speed (chase)** | 0.35 | 0.4 |
| **Follow Range** | 48 blocks | 64 blocks |
| **Knockback Resist** | 0.5 | 0.7 |
| **Experience** | 20 | 30 |
| **Ambient Sound** | Breathing | Static |
| **Hurt Sound** | Errrrrr | Glitch |
| **Death Sound** | Scream | Laugh |
| **Special Ability** | Speed boost | Teleport + Status effects |
| **Spawn Light** | < 7 | < 8 |
| **Animations** | 5 | 6 |
| **Difficulty** | Medium | Hard |

---

## Required Resource Files

### Blockbench Models (.geo.json)
**Location**: `assets/isrealanything/geo/`

1. **the_me.geo.json**
   - Humanoid horror entity
   - 0.6 blocks wide, 1.95 tall
   - Stalker appearance

2. **the_other_me.geo.json**
   - More menacing variant
   - Same dimensions
   - Darker, glitchier appearance

**Model Requirements**:
- Export from Blockbench as GeckoLib model
- Proper bone hierarchy
- Named bones for animation
- UV mapping for textures

### Animation Files (.animation.json)
**Location**: `assets/isrealanything/animations/`

1. **the_me.animation.json**
   ```
   - animation.the_me.idle (loop)
   - animation.the_me.walk (loop)
   - animation.the_me.attack (once)
   - animation.the_me.death (once)
   - animation.the_me.stalk (loop)
   ```

2. **the_other_me.animation.json**
   ```
   - animation.the_other_me.idle (loop)
   - animation.the_other_me.walk (loop)
   - animation.the_other_me.attack (once)
   - animation.the_other_me.death (once)
   - animation.the_other_me.teleport (once)
   - animation.the_other_me.glitch (once)
   ```

**Animation Guidelines**:
- Idle: 2-4 seconds, unsettling sway
- Walk: 1-2 seconds, unnatural gait
- Attack: 0.5-1 second, violent
- Death: 1.5-3 seconds, dramatic
- Stalk: Slow, deliberate
- Teleport: 0.3-0.5 seconds, distortion
- Glitch: 0.2-0.4 seconds, reality break

### Textures (PNG)
**Location**: `assets/isrealanything/textures/entity/`

1. **the_me.png**
   - Dark, shadowy appearance
   - Glowing eyes
   - Resolution: 64x64 or 128x128
   - Matches Blockbench UV map

2. **the_other_me.png**
   - Darker than TheME
   - Glitch patterns
   - Static noise overlay
   - Brighter eyes (cyan/red)
   - Distorted features

---

## Usage Examples

### Spawning TheME
```java
import com.epicspymain.isrealanything.entity.client.TheMEEntitySpawner;

// Spawn at position
TheMEEntity entity = TheMEEntitySpawner.spawnAt(world, pos);

// Spawn near player
TheMEEntity stalker = TheMEEntitySpawner.spawnNearPlayer(world, playerPos, 20.0);

// Spawn behind player (jumpscare)
TheMEEntity jumpscare = TheMEEntitySpawner.spawnBehindPlayer(world, playerPos, playerYaw, 3.0);
```

### Spawning TheOtherME
```java
import com.epicspymain.isrealanything.entity.client.TheOtherMEEntitySpawner;

// Spawn in dark area
TheOtherMEEntity shadow = TheOtherMEEntitySpawner.spawnInDarkness(world, playerPos, 32);

// Spawn group (horror sequence)
int count = TheOtherMEEntitySpawner.spawnGroup(world, centerPos, 3, 10);

// Jumpscare spawn
TheOtherMEEntity scare = TheOtherMEEntitySpawner.spawnBehindPlayer(world, playerPos, playerYaw, 2.0);
```

### Commands
```
/summon isrealanything:the_me ~ ~ ~
/summon isrealanything:the_other_me ~ ~ ~
```

---

## Testing Checklist

- [ ] GeckoLib initializes without errors
- [ ] Entities register successfully
- [ ] Spawn commands work
- [ ] Entity attributes load correctly
- [ ] AI goals function (chase, attack, wander)
- [ ] Sounds play correctly
- [ ] TheME speed increases when chasing
- [ ] TheOtherME teleports behind player
- [ ] Status effects apply on hit
- [ ] Entities spawn only in darkness
- [ ] Animations load (requires model files)
- [ ] Textures render (requires PNG files)
- [ ] No console errors
- [ ] Entity names appear in death messages

---

## Integration with Events

### Event 1 (IJoin - Day 2)
```java
// Spawn TheME when event triggers
TheMEEntitySpawner.spawnBehindPlayer(world, playerPos, playerYaw, 5.0);
```

### Random Horror Events
```java
// Spawn TheOtherME in darkness
if (world.getLightLevel(pos) < 4) {
    TheOtherMEEntitySpawner.spawnInDarkness(world, playerPos, 48);
}
```

### Chase Sequences
```java
// Spawn group of TheOtherME
TheOtherMEEntitySpawner.spawnGroup(world, playerPos, 3, 20);
```

---

## Performance Notes

- Entities use GeckoLib for smooth animations (GPU accelerated)
- Spawning limited by light level checks
- AI goals optimized for performance
- Teleportation has cooldown (10 seconds)
- No constant particle effects (performance friendly)

---

## Known Limitations

1. **Models Not Included**: Must create in Blockbench
2. **Animations Not Included**: Must animate in Blockbench
3. **Textures Not Included**: Must create custom textures
4. **No Natural Spawning**: Manual spawning required (by design)
5. **Teleport Range Limited**: TheOtherME teleports 3 blocks max

---

## Future Enhancements

- [ ] Add particle effects for teleportation
- [ ] Implement phase-through-walls ability
- [ ] Add sound distance calculation
- [ ] Implement fear/sanity system
- [ ] Add variant textures
- [ ] Implement natural spawning rules
- [ ] Add boss variants
- [ ] Create spawn eggs

---

**Files Created**: 14 Java classes + 3 resource guides = 17 files

**Status**: ✅ Complete - Entities Ready for Model/Texture Assets

**Compatibility**: Fabric 1.21.6 + GeckoLib 4.8 + Java 21
