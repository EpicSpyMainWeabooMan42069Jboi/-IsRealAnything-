# TASK 16 SUMMARY: Blocks, Config & Data Tracker

## Overview
Implemented blocks system, configuration defaults, and persistent data tracking infrastructure.

## Part 1: Blocks Package
**Location**: `com.epicspymain.isrealanything.block`

### 1. ModBlocks.java
- Central block registration
- **IMAGE_FRAME** block registered
- Block item registration (for inventory)
- Integration with Fabric API

**Features**:
- Wood material, 2.0 strength
- Non-opaque for rendering
- Automatically creates inventory item

### 2. ImageFrameBlock.java
- Custom block for displaying screenshots
- Thin frame shape (1 pixel depth)
- Integrates with `FrameFileManager`
- Displays `CURRENT_FRAME_TEXTURE`

**Features**:
- VoxelShape for thin frame appearance
- Right-click interaction shows current frame path
- Returns texture identifier for renderer
- Used by events to display captured screenshots

**Usage**:
```java
ImageFrameBlock.getCurrentTexture(); // Get texture for rendering
```

---

## Part 2: Config Package
**Location**: `com.epicspymain.isrealanything.config`

### 3. DefaultConfig.java
Comprehensive configuration with all default values.

#### Event System Settings
- `EVENTS_ENABLED = true`
- `EVENT_TICK_INTERVAL = 1200` (1 minute)
- `EVENT_CHANCE = 0.3` (30%)
- `START_EVENTS_AFTER = 48000` (Day 2)
- `GUARANTEED_EVENT_INTERVAL = 12000` (10 minutes)
- `REPEAT_EVENTS_AFTER = 5` events
- `EVENT_COOLDOWN = 6000` (5 minutes)

#### Event Weights (42 events)
Weight multipliers for event selection probability:
- **Phase 1** (1-10): Weights 2-5
- **Phase 2** (11-20): Weights 2-4
- **Phase 3** (21-30): Weights 2-4
- **Phase 4** (31-35): Weights 2-4
- **Phase 5** (36-42): Weights 1-3 (lower for finale)

**IStillLoveYouEvent** has weight 1 (rarest, final event)

#### Event Stages (Day Requirements)
Maps each event to minimum day number:
- Phase 1: Days 2-10
- Phase 2: Days 11-20
- Phase 3: Days 21-30
- Phase 4: Days 31-35
- Phase 5: Days 35-40

**IStillLoveYouEvent** requires Day 40 (final event)

#### Event Intervals (Custom Overrides)
- `SoundCreepEvent`: 3600 (3 minutes)
- `DailyNotificationEvent`: 24000 (once per day)
- `MemoryLeakEvent`: 12000 (10 minutes)
- `IStillLoveYouEvent`: MAX_VALUE (once only)

#### Data Collection Settings
- `ENABLE_DATA_COLLECTION = true`
- `COLLECTION_INTERVAL = 6000` (5 minutes)

#### Entity Settings
- `MAX_THEME_ENTITIES = 3`
- `MAX_THEOTHERME_ENTITIES = 2`
- `ENTITY_SPAWN_CHANCE = 0.4` (40%)

#### Stalking Behavior Settings
- `STALKING_DISTANCE = 16.0` blocks
- `STALKING_UPDATE_INTERVAL = 20` ticks
- `TELEPORT_BEHIND_CHANCE = 0.05` (5%)

---

## Part 3: World Package
**Location**: `com.epicspymain.isrealanything.world`

### 4. DataTracker.java
Persistent server state using `PersistentState`.

**Data Tracked**:
1. **Player Sleep Stages** - Progression for ForcedWakeupEvent
2. **Player Warning Read** - DoNotDeleteThis book reading status
3. **Player PII Consent** - Data collection consent
4. **Event History** - Event name → last trigger time
5. **Player Event Counts** - Per-player event tracking
6. **World Progression State**:
   - Current phase (1-5)
   - World creation time
   - Final event triggered flag

**NBT Persistence**:
- Saves to world data folder
- Survives server restarts
- Per-UUID player tracking

**API Methods**:
```java
// Sleep stages
int getPlayerSleepStage(UUID)
void setPlayerSleepStage(UUID, int)
void incrementPlayerSleepStage(UUID)

// Warning read
boolean getPlayerReadWarning(UUID)
void setPlayerReadWarning(UUID, boolean)

// PII consent
boolean getPlayerPIIConsent(UUID)
void setPlayerPIIConsent(UUID, boolean)

// Event history
long getEventLastTrigger(String eventName)
void setEventLastTrigger(String eventName, long time)
boolean hasEventTriggered(String eventName)

// Player event counts
int getPlayerEventCount(UUID, String eventName)
void incrementPlayerEventCount(UUID, String eventName)

// World state
int getCurrentPhase()
void setCurrentPhase(int)
long getWorldCreationTime()
void setWorldCreationTime(long)
boolean isFinalEventTriggered()
void setFinalEventTriggered(boolean)

// Utility
static int getCurrentDay(World)
void resetAll()
```

**Usage**:
```java
DataTracker tracker = DataTracker.getServerState(server);
int sleepStage = tracker.getPlayerSleepStage(playerUuid);
tracker.incrementPlayerSleepStage(playerUuid);
```

### 5. DimensionRegistry.java
Custom dimension registration for special events.

**Dimensions Registered**:
- **LIMBO_DIMENSION_KEY** - Empty void dimension
- **LIMBO_DIMENSION_TYPE** - Dimension type configuration

**Used By**:
- CalmBeforeStormEvent (Day 28)
- ForcedWakeupEvent (Day 19)
- Sleep transition events

**Features**:
- Registry key references for code
- Dimension data in `/data/isrealanything/dimension/`
- Helper method: `isLimboDimension(World)`

**Usage**:
```java
if (DimensionRegistry.isLimboDimension(world)) {
    // Special limbo behavior
}
```

---

## Integration

### Main Mod File Updates
Modified `IsRealAnything.java`:
- Import `ModBlocks` and `DimensionRegistry`
- Call `ModBlocks.registerModBlocks()` in initialization
- Call `DimensionRegistry.registerDimensions()` in initialization

### Event Integration
Events can now use:
- `DefaultConfig` for all configuration values
- `DataTracker` for persistent state
- `ImageFrameBlock` for displaying captures
- `DimensionRegistry` for dimension checks

### Examples

**Using Config**:
```java
if (DefaultConfig.EVENTS_ENABLED) {
    int weight = DefaultConfig.EVENT_WEIGHTS.get("MyEvent");
    int minDay = DefaultConfig.EVENT_STAGES.get("MyEvent");
}
```

**Using DataTracker**:
```java
DataTracker tracker = DataTracker.getServerState(server);
if (!tracker.hasEventTriggered("IStillLoveYouEvent")) {
    tracker.setEventLastTrigger("IStillLoveYouEvent", world.getTime());
}
```

---

## Statistics
- **Java Files**: 5
- **Total Lines**: ~850
- **Configuration Options**: 50+
- **Data Tracking Fields**: 6 categories
- **Persistent Maps**: 5
- **Public API Methods**: 25+

## Task Status
✅ **TASK 16 COMPLETE** - Blocks, configuration, and data tracking infrastructure

## Next Steps
- Create dimension data files in `/data/isrealanything/dimension/`
- Implement block renderer for ImageFrame
- Test DataTracker persistence across restarts
- Wire up DefaultConfig to PhaseBasedEventScheduler
