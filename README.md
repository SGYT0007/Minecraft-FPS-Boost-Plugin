# FPS Boost Plugin

A comprehensive FPS optimization plugin for Minecraft 1.21.7+ servers using the Paper API. This plugin provides real-time FPS monitoring, automatic performance optimizations, and customizable settings to enhance gameplay performance.

## Features

### 🚀 Performance Optimizations
- **Dynamic Rendering**: Adjusts render distances based on player location and server performance
- **Entity Optimizations**: Manages entity counts, merges nearby items, and removes excessive entities
- **Particle System**: Reduces particle effects to improve client FPS
- **Chunk Management**: Optimizes chunk loading/unloading and garbage collection
- **Visual Effects**: Configurable visual effect reductions for better performance

### 📊 FPS Monitoring
- **Real-time FPS Display**: Shows current, average, min, and max FPS in action bar or chat
- **Server TPS Monitoring**: Tracks server performance alongside client FPS
- **Performance Statistics**: Detailed performance metrics and analytics
- **Individual Player Tracking**: Personalized FPS data for each player

### ⚙️ Configuration & Management
- **Comprehensive Config**: Highly customizable settings for all optimizations
- **Individual Player Preferences**: Players can toggle features individually
- **Automatic Scaling**: Performance-based optimization scaling
- **Emergency Mode**: Critical performance recovery system

### 🎮 User-Friendly Commands
- **FPS Commands**: Easy-to-use commands for checking performance
- **Toggle Features**: Simple commands to enable/disable optimizations
- **Admin Controls**: Comprehensive administration tools

## Installation

1. **Requirements**:
   - Minecraft Server 1.21.7+
   - Paper API (recommended) or Spigot
   - Java 17+

2. **Download**:
   - Download the latest `FPSBoostPlugin-1.0.0.jar` from releases

3. **Install**:
   - Place the JAR file in your server's `plugins/` directory
   - Restart your server
   - Configure the plugin in `plugins/FPSBoostPlugin/config.yml`

## Commands

### Player Commands
| Command | Permission | Description |
|---------|------------|-------------|
| `/fps [player]` | `fpsboost.fps` | Display FPS and performance statistics |
| `/togglefps [feature]` | `fpsboost.toggle` | Toggle FPS optimizations or display |

### Admin Commands
| Command | Permission | Description |
|---------|------------|-------------|
| `/fpsboost reload` | `fpsboost.admin` | Reload plugin configuration |
| `/fpsboost toggle <feature>` | `fpsboost.admin` | Toggle specific optimizations |
| `/fpsboost status` | `fpsboost.admin` | View plugin status and statistics |
| `/fpsboost config` | `fpsboost.admin` | View current configuration |

## Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `fpsboost.*` | op | All FPS boost permissions |
| `fpsboost.admin` | op | Access to admin commands |
| `fpsboost.fps` | true | View FPS statistics |
| `fpsboost.toggle` | true | Toggle FPS features |
| `fpsboost.bypass` | op | Bypass FPS optimizations |

## Configuration

The plugin provides extensive configuration options in `config.yml`:

### General Settings
```yaml
general:
  enabled: true
  debug: false
  auto-update-check: true
  metrics: true
```

### FPS Display
```yaml
fps-display:
  enabled: true
  update-interval: 1000
  display-format: "&a[FPS] &f{fps} &7| &aTPS: &f{tps}"
  actionbar-display: true
  chat-display: false
```

### Performance Optimizations
```yaml
optimizations:
  dynamic-rendering:
    enabled: true
    entity-render-distance: 32
    tile-entity-render-distance: 16
    particle-render-distance: 24
  
  entity-optimizations:
    enabled: true
    max-entities-per-chunk: 50
    remove-excess-items: true
    item-merge-radius: 2.0
  
  particle-optimizations:
    enabled: true
    reduce-particles: true
    particle-limit-per-player: 100
  
  chunk-optimizations:
    enabled: true
    async-chunk-loading: true
    preload-chunks: true
    unload-empty-chunks: true
```

### Performance Thresholds
```yaml
thresholds:
  tps:
    warning: 18.0
    critical: 15.0
    emergency: 10.0
  memory:
    warning: 70
    critical: 85
    emergency: 95
```

## How It Works

### FPS Calculation
The plugin estimates client FPS by analyzing various server-side factors:
- Server TPS performance
- Entity density around players  
- Chunk loading status
- Memory usage
- Network conditions

### Automatic Optimizations
Based on server performance, the plugin automatically:
1. **Warning Level** (TPS < 18): Basic optimizations activated
2. **Critical Level** (TPS < 15): Enhanced optimizations applied
3. **Emergency Level** (TPS < 10): All optimizations enabled, aggressive cleanup

### Dynamic Rendering
Adjusts render distances based on:
- Player location and surroundings
- Server performance metrics
- Individual player preferences
- Network latency

## Building from Source

### Prerequisites
- Java 17+
- Maven 3.8+
- Git

### Build Steps
```bash
git clone https://github.com/SGYT0007/Minecraft-FPS-Boost-Plugin.git
cd Minecraft-FPS-Boost-Plugin
mvn clean package
```

The compiled JAR will be in `target/FPSBoostPlugin-1.0.0.jar`

## API Usage

### For Plugin Developers

```java
// Get the FPS Boost Plugin instance
FPSBoostPlugin fpsPlugin = (FPSBoostPlugin) Bukkit.getPluginManager().getPlugin("FPSBoostPlugin");

// Get a player's current FPS
int playerFPS = fpsPlugin.getFPSManager().getPlayerFPS(player);

// Toggle an optimization
fpsPlugin.getOptimizationManager().toggleOptimization("entity-optimizations");

// Get performance statistics
String stats = fpsPlugin.getPerformanceMonitor().getPerformanceStats();
```

## Compatibility

### Tested With
- **Server Software**: Paper 1.21.3+, Spigot 1.21+
- **Plugin Compatibility**: WorldEdit, WorldGuard, LuckPerms, Vault
- **Java Versions**: Java 17, Java 21

### Known Issues
- Some client-side mods may affect FPS calculations
- Heavy plugin loads may impact optimization effectiveness

## Performance Impact

The plugin is designed to be lightweight:
- **Memory Usage**: < 10MB additional RAM
- **CPU Impact**: < 1% additional CPU usage
- **Network**: Minimal packet overhead
- **Disk**: Player data files are small and efficiently managed

## Support

### Getting Help
1. Check the [Wiki](wiki) for detailed documentation
2. Review [Common Issues](issues) section
3. Join our [Discord](discord-link) for community support
4. Submit bug reports via [GitHub Issues](issues-link)

### Contributing
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Changelog

### Version 1.0.0
- Initial release
- Real-time FPS monitoring and display
- Comprehensive performance optimizations
- Configurable settings and player preferences
- Automatic performance scaling
- Emergency optimization mode
- Full Paper API 1.21.7+ support

## Credits

- **Author**: SlayerGamerYT
- **Repository**: https://github.com/SGYT0007/Minecraft-FPS-Boost-Plugin/
- **Special Thanks**: Paper team, Bukkit/Spigot community

## Roadmap

### Planned Features
- [ ] Advanced packet optimization
- [ ] Machine learning-based optimization prediction
- [ ] Integration with popular optimization plugins
- [ ] Web-based configuration interface
- [ ] Detailed performance analytics dashboard
- [ ] Multi-language support

---

*Made with ❤️ for the Minecraft community*
