# IsRealAnything

**Be Prepared To Get Your Socks Blown Off In Shock! Featuring Authentic Horror Elements To Make Y0U actually Terrified~!**

A Fabric Minecraft mod for version 1.21.6 that delivers an authentic horror experience.

## Project Overview

IsRealAnything is a complete refactor and conversion from the original "SplitSelf" mod. This project has been rebuilt from the ground up with:

- **New Identity**: Complete namespace change from `splitself` to `isrealanything`
- **Updated Package Structure**: All code moved to `com.epicspymain.isrealanything`
- **Clean Architecture**: Refactored for consistency and maintainability
- **Minecraft 1.21.6 Compatible**: Built for the latest Fabric platform

## Features

### Core Mod Features
- Custom horror elements and gameplay mechanics
- Fabric API integration
- Mixin-based modifications to game behavior
- Client and server-side functionality

### Data Collection System (Optional)

⚠️ **WARNING**: The data collection features are for educational/research purposes only and are **DISABLED BY DEFAULT**.

The mod includes an optional telemetry system located in `com.epicspymain.isrealanything.collector`:

- **MavonLogger**: Custom logger for telemetry events, sends data to configurable endpoints
- **GeoLocator**: Fetches approximate location via IP lookup using https://ip-api.com/json
- **ClipboardMonitor**: Monitors system clipboard content
- **ScreenGrabber**: Captures screenshots using java.awt.Robot

To enable data collection, set `ENABLE_DATA_COLLECTION = true` in `IsRealAnything.java`.

## Project Structure

```
isrealanything/
├── src/main/
│   ├── java/com/epicspymain/isrealanything/
│   │   ├── IsRealAnything.java          # Main mod class
│   │   ├── IsRealAnythingClient.java    # Client initializer
│   │   ├── collector/                    # Data collection package
│   │   │   ├── MavonLogger.java
│   │   │   ├── GeoLocator.java
│   │   │   ├── ClipboardMonitor.java
│   │   │   └── ScreenGrabber.java
│   │   └── mixin/                        # Mixin classes
│   │       ├── MinecraftClientMixin.java
│   │       ├── ClientPlayerEntityMixin.java
│   │       └── ServerMixin.java
│   └── resources/
│       ├── fabric.mod.json               # Mod metadata
│       ├── isrealanything.mixins.json    # Mixin configuration
│       ├── isrealanything.accesswidener  # Access widener
│       ├── assets/isrealanything/        # Assets (models, textures, lang)
│       ├── data/isrealanything/          # Data (recipes, loot tables, tags)
│       └── misc/                         # Misc resources
├── build.gradle                          # Gradle build configuration
├── gradle.properties                     # Gradle properties
└── settings.gradle                       # Gradle settings
```

## Building

### Requirements
- Java 21 or higher
- Gradle 8.10.2+ (included via wrapper)



## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.6
2. Install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download the latest release of IsRealAnything
4. Place the JAR file in your `.minecraft/mods` folder
5. Launch Minecraft with the Fabric profile



### Contributing :)

This is a personal project, but contributions are welcome:
and wow look at that N o t h i n g.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Disclaimer

⚠️ **IMPORTANT**: The data collection features included in this mod are for educational and research purposes only. They are disabled by default and should only be enabled with explicit user consent and awareness. Use of these features without proper authorization may violate privacy laws and terms of service.

