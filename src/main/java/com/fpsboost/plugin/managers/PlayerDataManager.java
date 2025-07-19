package com.fpsboost.plugin.managers;

import com.fpsboost.plugin.FPSBoostPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Manages individual player data and preferences for FPS optimizations.
 */
public class PlayerDataManager {
    private final FPSBoostPlugin plugin;
    private final Map<UUID, PlayerData> playerDataMap;
    private File playerDataFolder;

    public PlayerDataManager(FPSBoostPlugin plugin) {
        this.plugin = plugin;
        this.playerDataMap = new ConcurrentHashMap<>();
        
        initializePlayerDataFolder();
        loadAllPlayerData();
    }

    /**
     * Player data holder class
     */
    public static class PlayerData {
        private boolean fpsDisplayEnabled;
        private boolean optimizationsEnabled;
        private Map<String, Boolean> individualOptimizations;
        private long joinTime;
        private long totalPlayTime;

        public PlayerData() {
            this.fpsDisplayEnabled = true;
            this.optimizationsEnabled = true;
            this.individualOptimizations = new HashMap<>();
            this.joinTime = System.currentTimeMillis();
            this.totalPlayTime = 0;
            
            // Initialize default optimization preferences
            initializeDefaultOptimizations();
        }

        private void initializeDefaultOptimizations() {
            individualOptimizations.put("dynamic-rendering", true);
            individualOptimizations.put("entity-optimizations", true);
            individualOptimizations.put("particle-optimizations", true);
            individualOptimizations.put("chunk-optimizations", true);
            individualOptimizations.put("visual-effects", true);
        }

        // Getters and setters
        public boolean isFpsDisplayEnabled() { return fpsDisplayEnabled; }
        public void setFpsDisplayEnabled(boolean enabled) { this.fpsDisplayEnabled = enabled; }
        
        public boolean isOptimizationsEnabled() { return optimizationsEnabled; }
        public void setOptimizationsEnabled(boolean enabled) { this.optimizationsEnabled = enabled; }
        
        public Map<String, Boolean> getIndividualOptimizations() { return individualOptimizations; }
        public void setOptimization(String optimization, boolean enabled) { 
            individualOptimizations.put(optimization, enabled); 
        }
        public boolean isOptimizationEnabled(String optimization) { 
            return individualOptimizations.getOrDefault(optimization, true); 
        }
        
        public long getJoinTime() { return joinTime; }
        public void setJoinTime(long joinTime) { this.joinTime = joinTime; }
        
        public long getTotalPlayTime() { return totalPlayTime; }
        public void setTotalPlayTime(long totalPlayTime) { this.totalPlayTime = totalPlayTime; }
        public void addPlayTime(long additionalTime) { this.totalPlayTime += additionalTime; }
    }

    /**
     * Initialize player data folder
     */
    private void initializePlayerDataFolder() {
        playerDataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
        }
    }

    /**
     * Load all existing player data files
     */
    private void loadAllPlayerData() {
        if (!playerDataFolder.exists()) {
            return;
        }

        File[] dataFiles = playerDataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (dataFiles == null) {
            return;
        }

        int loadedCount = 0;
        for (File file : dataFiles) {
            try {
                String fileName = file.getName();
                String uuidString = fileName.substring(0, fileName.length() - 4); // Remove .yml
                UUID playerId = UUID.fromString(uuidString);
                
                loadPlayerData(playerId);
                loadedCount++;
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Failed to load player data file: " + file.getName(), e);
            }
        }

        if (loadedCount > 0) {
            plugin.debug("Loaded " + loadedCount + " player data files");
        }
    }

    /**
     * Load player data from file
     */
    public PlayerData loadPlayerData(UUID playerId) {
        File playerFile = new File(playerDataFolder, playerId.toString() + ".yml");
        PlayerData playerData = new PlayerData();

        if (playerFile.exists()) {
            try {
                FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
                
                // Load basic settings
                playerData.setFpsDisplayEnabled(config.getBoolean("fps-display-enabled", true));
                playerData.setOptimizationsEnabled(config.getBoolean("optimizations-enabled", true));
                playerData.setTotalPlayTime(config.getLong("total-play-time", 0));
                
                // Load individual optimizations
                if (config.contains("individual-optimizations")) {
                    for (String key : config.getConfigurationSection("individual-optimizations").getKeys(false)) {
                        playerData.setOptimization(key, config.getBoolean("individual-optimizations." + key));
                    }
                }
                
                plugin.debug("Loaded player data for: " + playerId);
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Failed to load player data for " + playerId, e);
            }
        }

        playerDataMap.put(playerId, playerData);
        return playerData;
    }

    /**
     * Save player data to file
     */
    public void savePlayerData(UUID playerId) {
        PlayerData playerData = playerDataMap.get(playerId);
        if (playerData == null) {
            return;
        }

        File playerFile = new File(playerDataFolder, playerId.toString() + ".yml");
        FileConfiguration config = new YamlConfiguration();

        try {
            // Save basic settings
            config.set("fps-display-enabled", playerData.isFpsDisplayEnabled());
            config.set("optimizations-enabled", playerData.isOptimizationsEnabled());
            config.set("total-play-time", playerData.getTotalPlayTime());
            config.set("last-save", System.currentTimeMillis());

            // Save individual optimizations
            for (Map.Entry<String, Boolean> entry : playerData.getIndividualOptimizations().entrySet()) {
                config.set("individual-optimizations." + entry.getKey(), entry.getValue());
            }

            config.save(playerFile);
            plugin.debug("Saved player data for: " + playerId);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save player data for " + playerId, e);
        }
    }

    /**
     * Get player data (load if not cached)
     */
    public PlayerData getPlayerData(UUID playerId) {
        PlayerData data = playerDataMap.get(playerId);
        if (data == null) {
            data = loadPlayerData(playerId);
        }
        return data;
    }

    /**
     * Get player data by Player object
     */
    public PlayerData getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId());
    }

    /**
     * Handle player join
     */
    public void handlePlayerJoin(Player player) {
        UUID playerId = player.getUniqueId();
        PlayerData playerData = getPlayerData(playerId);
        playerData.setJoinTime(System.currentTimeMillis());

        // Apply player's FPS display preference
        if (plugin.getFPSManager() != null) {
            plugin.getFPSManager().setFPSDisplay(player, playerData.isFpsDisplayEnabled());
        }

        // Show FPS on join if enabled
        if (plugin.getConfigManager().getConfig().getBoolean("fps-display.show-on-join", true) && 
            playerData.isFpsDisplayEnabled()) {
            
            // Schedule a delayed task to show FPS after player fully loads
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline() && plugin.getFPSManager() != null) {
                    String stats = plugin.getFPSManager().getPerformanceStats(player);
                    player.sendMessage(plugin.getConfigManager().getConfig().getString("messages.prefix", "&8[&6FPS&8] ") + 
                                     "&aWelcome! " + stats.replace("\n", " "));
                }
            }, 40L); // 2 second delay
        }

        plugin.debug("Player joined: " + player.getName() + " (FPS Display: " + playerData.isFpsDisplayEnabled() + ")");
    }

    /**
     * Handle player quit
     */
    public void handlePlayerQuit(Player player) {
        UUID playerId = player.getUniqueId();
        PlayerData playerData = playerDataMap.get(playerId);
        
        if (playerData != null) {
            // Update play time
            long sessionTime = System.currentTimeMillis() - playerData.getJoinTime();
            playerData.addPlayTime(sessionTime);
            
            // Save player data
            savePlayerData(playerId);
            
            plugin.debug("Player quit: " + player.getName() + 
                        " (Session: " + (sessionTime / 1000) + "s, Total: " + (playerData.getTotalPlayTime() / 1000) + "s)");
        }

        // Clean up FPS manager data
        if (plugin.getFPSManager() != null) {
            plugin.getFPSManager().removePlayerData(playerId);
        }
    }

    /**
     * Toggle FPS display for a player
     */
    public boolean toggleFPSDisplay(Player player) {
        PlayerData playerData = getPlayerData(player);
        boolean newState = !playerData.isFpsDisplayEnabled();
        playerData.setFpsDisplayEnabled(newState);
        
        // Apply to FPS manager
        if (plugin.getFPSManager() != null) {
            plugin.getFPSManager().setFPSDisplay(player, newState);
        }
        
        // Save immediately
        savePlayerData(player.getUniqueId());
        
        return newState;
    }

    /**
     * Toggle optimizations for a player
     */
    public boolean toggleOptimizations(Player player) {
        PlayerData playerData = getPlayerData(player);
        boolean newState = !playerData.isOptimizationsEnabled();
        playerData.setOptimizationsEnabled(newState);
        
        // Save immediately
        savePlayerData(player.getUniqueId());
        
        return newState;
    }

    /**
     * Toggle a specific optimization for a player
     */
    public boolean toggleOptimization(Player player, String optimization) {
        PlayerData playerData = getPlayerData(player);
        boolean currentState = playerData.isOptimizationEnabled(optimization);
        boolean newState = !currentState;
        
        playerData.setOptimization(optimization, newState);
        
        // Save immediately
        savePlayerData(player.getUniqueId());
        
        return newState;
    }

    /**
     * Get player preferences summary
     */
    public String getPlayerPreferences(Player player) {
        PlayerData playerData = getPlayerData(player);
        
        StringBuilder sb = new StringBuilder();
        sb.append("&6=== Player Preferences ===\n");
        sb.append("&aFPS Display: &f").append(playerData.isFpsDisplayEnabled() ? "Enabled" : "Disabled").append("\n");
        sb.append("&aOptimizations: &f").append(playerData.isOptimizationsEnabled() ? "Enabled" : "Disabled").append("\n");
        sb.append("&aTotal Play Time: &f").append(formatPlayTime(playerData.getTotalPlayTime())).append("\n");
        
        sb.append("&aIndividual Optimizations:\n");
        for (Map.Entry<String, Boolean> entry : playerData.getIndividualOptimizations().entrySet()) {
            String status = entry.getValue() ? "&aEnabled" : "&cDisabled";
            sb.append("  &7- &f").append(entry.getKey()).append(": ").append(status).append("\n");
        }
        
        return sb.toString();
    }

    /**
     * Format play time in human readable format
     */
    private String formatPlayTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        if (hours > 0) {
            return hours + "h " + (minutes % 60) + "m";
        } else if (minutes > 0) {
            return minutes + "m " + (seconds % 60) + "s";
        } else {
            return seconds + "s";
        }
    }

    /**
     * Save all player data
     */
    public void saveAllPlayerData() {
        int savedCount = 0;
        for (UUID playerId : playerDataMap.keySet()) {
            try {
                savePlayerData(playerId);
                savedCount++;
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Failed to save player data for " + playerId, e);
            }
        }
        
        if (savedCount > 0) {
            plugin.debug("Saved " + savedCount + " player data files");
        }
    }

    /**
     * Remove player data from memory (not from file)
     */
    public void removePlayerData(UUID playerId) {
        playerDataMap.remove(playerId);
    }

    /**
     * Get total number of tracked players
     */
    public int getTrackedPlayerCount() {
        return playerDataMap.size();
    }

    /**
     * Cleanup player data manager
     */
    public void cleanup() {
        saveAllPlayerData();
        playerDataMap.clear();
        plugin.debug("Player Data Manager cleaned up successfully");
    }
}
