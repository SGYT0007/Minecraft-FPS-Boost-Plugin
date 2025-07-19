package com.fpsboost.plugin.managers;

import com.fpsboost.plugin.FPSBoostPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Manages configuration files for the FPS Boost Plugin.
 */
public class ConfigManager {
    private final FPSBoostPlugin plugin;
    private FileConfiguration config;
    private File configFile;

    public ConfigManager(FPSBoostPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Load configuration from file.
     */
    public void loadConfig() {
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), "config.yml");
        }

        config = YamlConfiguration.loadConfiguration(configFile);
        plugin.getLogger().info("Configuration loaded.");
    }

    /**
     * Save the configuration to file.
     */
    public void saveConfig() {
        if (config == null || configFile == null) {
            return;
        }

        try {
            config.save(configFile);
            plugin.getLogger().info("Configuration saved.");
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
        }
    }

    /**
     * Reload the configuration from the file.
     */
    public void reloadConfig() {
        loadConfig();
    }

    /**
     * Get the configuration FileConfiguration object.
     * @return The configuration object.
     */
    public FileConfiguration getConfig() {
        return config;
    }

    /**
     * Check if debug mode is enabled.
     * @return True if debug mode is enabled.
     */
    public boolean isDebugEnabled() {
        return config.getBoolean("general.debug", false);
    }
}
