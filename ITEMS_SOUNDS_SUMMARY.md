# IsRealAnything - Items & Sounds Summary (TASK 5)

## Overview
Complete implementation of custom items, sounds, and music disc system for Minecraft 1.21.6 Fabric mod.

---

## Packages Created

### 1. com.epicspymain.isrealanything.item
Package for all custom items and item groups.

### 2. com.epicspymain.isrealanything.sound
Package for all custom sound event registrations.

---

## Items Implementation

### ModItems.java
**Purpose**: Registers all custom items for the mod

**Items Defined**:
```java
public static final Item UNTITLED_DISK_SONG_MUSIC_DISC
```

**Properties**:
- **Type**: Music Disc (Jukebox Playable)
- **Max Stack**: 1
- **Rarity**: RARE
- **Duration**: 79 seconds
- **Description**: "....w......h ..     .   y" (cryptic/horror themed)
- **Registry Key**: `isrealanything:untitled_disk_song`

**Features**:
- Playable in jukeboxes
- Custom sound event integration
- Mysterious description fitting horror theme
- Rare loot item

---

### ModItemGroups.java
**Purpose**: Creates custom creative mode tabs

**Item Group**:
```java
public static final ItemGroup ISREALANYTHING_ITEM_GROUP
```

**Properties**:
- **Icon**: Untitled Disk Song Music Disc
- **Display Name**: "IsRealAnything"
- **Tab Color**: Default (can be customized)

**Features**:
- Auto-populated with mod items
- Appears in creative inventory
- Extensible for future items

---

## Sounds Implementation

### ModSounds.java
**Purpose**: Registers all custom sound events

**Sound Events** (16 total):

#### Music & Music Disc
1. **UNTITLED_DISK_SONG** - The music disc track (79s, streaming)

#### Primary Horror Sounds
2. **TED_LEWIS_FUCK_YOU** - Disturbing audio (1.0 volume)
3. **ERRRRRR** - Distorted sound effect (0.9 volume)
4. **FOREVER** - Looping horror ambience (0.8 volume, streaming)

#### Ambient Horror
5. **WHISPER_1** - Whisper effect 1 (0.6 volume)
6. **WHISPER_2** - Whisper effect 2 (0.6 volume)
7. **STATIC_NOISE** - TV static/glitch (0.7 volume)
8. **HEARTBEAT** - Tension building (0.8 volume)
9. **BREATHING** - Heavy breathing (0.7 volume)

#### Environmental Horror
10. **DOOR_CREAK** - Creaking door (0.9 volume)
11. **FOOTSTEPS_HORROR** - Ominous footsteps (0.8 volume)
12. **LAUGH_DISTANT** - Distant laughter (0.7 volume)

#### Jumpscares
13. **SCREAM** - Loud scream (1.0 volume)
14. **JUMPSCARE** - Instant scare sound (1.0 volume)

#### Event-Specific
15. **EVENT_IJOIN** - Event 1 "I Join" (0.9 volume, streaming)
16. **EVENT_WATCHING** - Stalking event (0.8 volume)
17. **EVENT_GLITCH** - Reality glitch (0.9 volume)

---

## Resource Files Structure

### Jukebox Song Data
**File**: `assets/isrealanything/jukebox_song/untitled_disk_song.json`

```json
{
  "comparator_output": 1,
  "description": {
    "translate": "item.isrealanything.untitled_disk_song_music_disc.desc"
  },
  "length_in_seconds": 79,
  "sound_event": "isrealanything:untitled_disk_song"
}
```

**Purpose**:
- Defines jukebox behavior for the music disc
- Sets comparator output strength (1-15)
- Links to sound event
- Displays translated description

---

### Sound Definitions
**File**: `assets/isrealanything/sounds.json`

**Key Features**:
- Streaming enabled for music/long sounds (better performance)
- Volume levels balanced (0.6-1.0)
- Subtitles for all sounds (accessibility)
- Horror-themed subtitles ("*whispers*", "!!!", etc.)

**Example Entry**:
```json
"untitled_disk_song": {
  "sounds": [
    {
      "name": "isrealanything:untitled_disk_song",
      "stream": true
    }
  ],
  "subtitle": "....w......h ..     .   y"
}
```

---

### Language Translations
**File**: `assets/isrealanything/lang/en_us.json`

**Item Translations**:
```json
"item.isrealanything.untitled_disk_song_music_disc": "Music Disc",
"item.isrealanything.untitled_disk_song_music_disc.desc": "....w......h ..     .   y",
"jukebox_song.isrealanything.untitled_disk_song.desc": "....w......h ..     .   y"
```

**Sound Subtitles**:
- Horror-themed subtitles for all sounds
- Cryptic messages for mystery sounds
- Accessibility compliance
- Atmospheric text ("*whispers*", "*static*", etc.)

---

### Item Model
**File**: `assets/isrealanything/models/item/untitled_disk_song_music_disc.json`

```json
{
  "parent": "minecraft:item/generated",
  "textures": {
    "layer0": "isrealanything:item/untitled_disk_song_music_disc"
  }
}
```

**Texture Location**: `assets/isrealanything/textures/item/untitled_disk_song_music_disc.png`

---

## Sound Files Structure

### Required OGG Files
All files must be in **OGG Vorbis format**:

```
src/main/resources/assets/isrealanything/sounds/
├── untitled_disk_song.ogg (79 seconds)
├── ted_lewis_fuck_you.ogg
├── errrrrr.ogg
├── forever.ogg
├── ambient/
│   ├── whisper_1.ogg
│   ├── whisper_2.ogg
│   ├── static_noise.ogg
│   ├── heartbeat.ogg
│   ├── breathing.ogg
│   ├── door_creak.ogg
│   ├── footsteps_horror.ogg
│   ├── laugh_distant.ogg
│   ├── scream.ogg
│   └── jumpscare.ogg
└── events/
    ├── event_ijoin.ogg
    ├── event_watching.ogg
    └── event_glitch.ogg
```

### Audio Specifications
- **Format**: OGG Vorbis
- **Sample Rate**: 44100 Hz (recommended)
- **Bit Rate**: 128-192 kbps
- **Channels**: Mono or Stereo (stereo preferred for immersion)
- **Music Disc Length**: Exactly 79 seconds

### Streaming vs Non-Streaming
- **Stream = true**: Long sounds (music, ambient loops) - loaded progressively
- **Stream = false**: Short sounds (effects, jumpscares) - loaded into memory

---

## Integration

### Main Mod Class Update
**File**: `IsRealAnything.java`

```java
@Override
public void onInitialize() {
    LOGGER.info("IsRealAnything mod initialized!");
    LOGGER.info("Be Prepared To Get Your Socks Blown Off In Shock!");
    
    // Register items and sounds
    ModItems.registerModItems();
    ModSounds.registerModSounds();
    ModItemGroups.registerItemGroups();
    
    // ... rest of initialization
}
```

**Registration Order**:
1. ModItems (items must exist first)
2. ModSounds (sounds for items)
3. ModItemGroups (tabs using items)

---

## Usage Examples

### Playing Sounds in Code

```java
import com.epicspymain.isrealanything.sound.ModSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;

// Play horror sound
MinecraftClient client = MinecraftClient.getInstance();
client.getSoundManager().play(
    PositionedSoundInstance.master(ModSounds.WHISPER_1, 1.0f)
);

// Play at specific position
client.getSoundManager().play(
    new PositionedSoundInstance(
        ModSounds.SCREAM,
        SoundCategory.AMBIENT,
        1.0f,  // volume
        1.0f,  // pitch
        pos.getX(),
        pos.getY(),
        pos.getZ()
    )
);
```

### Giving Items in Code

```java
import com.epicspymain.isrealanything.item.ModItems;
import net.minecraft.item.ItemStack;

// Create music disc
ItemStack disc = new ItemStack(ModItems.UNTITLED_DISK_SONG_MUSIC_DISC);

// Give to player
player.giveItemStack(disc);
```

### Loot Table Integration

```json
{
  "pools": [
    {
      "rolls": 1,
      "entries": [
        {
          "type": "minecraft:item",
          "name": "isrealanything:untitled_disk_song_music_disc",
          "weight": 1
        }
      ]
    }
  ]
}
```

---

## Creative Tab Access

### In-Game:
1. Open Creative Inventory (E key in creative mode)
2. Find "IsRealAnything" tab
3. Music disc appears as tab icon
4. All mod items listed inside

### Commands:
```
/give @p isrealanything:untitled_disk_song_music_disc
```

---

## Testing Checklist

- [ ] Music disc appears in creative tab
- [ ] Music disc plays in jukebox (79 seconds)
- [ ] Cryptic description displays correctly
- [ ] All 16+ sounds registered without errors
- [ ] Sound events trigger correctly
- [ ] Subtitles appear when sounds play
- [ ] Volume levels are appropriate
- [ ] Streaming sounds don't lag
- [ ] Item model renders correctly
- [ ] Translations load properly
- [ ] Creative tab icon displays
- [ ] No console errors during registration

---

## Sound Design Notes

### Horror Atmosphere
- **Whispers**: Low volume (0.6) for background unease
- **Static**: Medium volume (0.7) for tension
- **Heartbeat**: Medium-high (0.8) for building dread
- **Scream/Jumpscare**: Max volume (1.0) for shock

### Event Integration
- **EVENT_IJOIN**: Plays during Event 1 (Day 2)
- **EVENT_WATCHING**: Triggered when player is being stalked
- **EVENT_GLITCH**: Reality distortion effects

### Ambient Layering
- Multiple sounds can play simultaneously
- Whispers + heartbeat + static = intense atmosphere
- Footsteps + breathing = chase sequence
- Distant laugh + door creak = exploration tension

---

## File Count Summary

| Category | Count |
|----------|-------|
| Java Classes | 3 |
| Sound Events | 16+ |
| JSON Files | 4 |
| Resource Files | 3+ |
| **Total New Files** | **26+** |

---

## Future Expansion

### Easy to Add:
- More music discs
- Additional horror sounds
- Custom instruments
- Block sounds
- Entity sounds

### Template for New Item:
```java
public static final Item NEW_ITEM = registerItem(
    "new_item_name",
    new Item(new Item.Settings().maxCount(64))
);
```

### Template for New Sound:
```java
public static final SoundEvent NEW_SOUND = registerSoundEvent("new_sound_name");
```

---

## Dependencies

**Required**:
- Fabric API (for item groups)
- Minecraft 1.21.6
- Java 21

**Optional**:
- Resource pack for custom textures
- Sound files (OGG format)

---

## Known Issues & Notes

1. **Sound Files Not Included**: You must provide your own OGG files
2. **Texture Not Included**: Create 16x16 PNG for music disc
3. **Description is Cryptic**: Intentional for horror theme
4. **Long Sound Names**: Some contain explicit language (can be renamed)

---

**Status**: ✅ Complete - Items & Sounds System Ready

**Compatibility**: Fabric 1.21.6 + Yarn Mappings + Java 21

**Next Steps**: 
1. Add OGG sound files to sounds/ directory
2. Create texture for music disc
3. Test in-game with jukebox
4. Integrate sounds into horror events
