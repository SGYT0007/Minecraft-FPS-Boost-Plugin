package com.fpsboost.plugin;

import com.fpsboost.plugin.commands.FPSCommand;
import com.fpsboost.plugin.commands.FPSBoostCommand;
import com.fpsboost.plugin.commands.ToggleFPSCommand;
import com.fpsboost.plugin.listeners.PlayerListener;
import com.fpsboost.plugin.listeners.PerformanceListener;
import com.fpsboost.plugin.listeners.EntityListener;
import com.fpsboost.plugin.listeners.ChunkListener;
import com.fpsboost.plugin.managers.*;
import com.fpsboost.plugin.utils.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.logging.Level;

/**
 * Main FPS Boost Plugin class for Minecraft 1.21.7+
 * Provides comprehensive performance optimizations and FPS monitoring
 * 
 * Repository: https://github.com/SGYT0007/Minecraft-FPS-Boost-Plugin/
 * @author SlayerGamerYT
 * @version 1.0.0
 */
public class FPSBoostPlugin extends JavaPlugin {
    
    // Plugin instance
    private static FPSBoostPlugin instance;
    
    // Managers
    private ConfigManager configManager;
    private FPSManager fpsManager;
    private OptimizationManager optimizationManager;
    private AdvancedOptimizationManager advancedOptimizationManager;
    private PerformanceMonitor performanceMonitor;
    private UltraPerformanceMonitor ultraPerformanceMonitor;
    private PlayerDataManager playerDataManager;
    
    // Tasks
    private BukkitTask performanceTask;
    private BukkitTask fpsDisplayTask;
    
    // Plugin state
    private boolean enabled = true;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize plugin
        long startTime = System.currentTimeMillis();
        
        getLogger().info("Loading FPS Boost Plugin v" + getDescription().getVersion());
        getLogger().info("Compatible with Minecraft 1.21.7+ (Paper API)");
        
        try {
            // Initialize configuration
            initializeConfig();
            
            // Initialize managers
            initializeManagers();
            
            // Register commands
            registerCommands();
            
            // Register listeners
            registerListeners();
            
            // Start performance monitoring
            startPerformanceMonitoring();
            
            // Check for plugin conflicts
            checkPluginCompatibility();
            
            long loadTime = System.currentTimeMillis() - startTime;
            getLogger().info("FPS Boost Plugin loaded successfully in " + loadTime + "ms");
            getLogger().info("Performance optimizations are now active!");
            
            // Display startup message
            if (configManager.isDebugEnabled()) {
                getLogger().info("Debug mode is enabled - additional logging active");
            }
            
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to enable FPS Boost Plugin", e);
            getServer().getPluginManager().disablePlugin(this);
        }
    }
    
    @Override
    public void onDisable() {
        getLogger().info("Disabling FPS Boost Plugin...");
        
        try {
            // Stop all scheduled tasks
            stopTasks();
            
            // Save player data
            if (playerDataManager != null) {
                playerDataManager.saveAllPlayerData();
            }
            
            // Cleanup managers
            cleanup();
            
            getLogger().info("FPS Boost Plugin disabled successfully");
            
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error during plugin shutdown", e);
        } finally {
            instance = null;
        }
    }
    
    /**
     * Initialize configuration files
     */
    private void initializeConfig() {
        // Save default config if it doesn't exist
        saveDefaultConfig();
        
        // Initialize config manager
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        
        getLogger().info("Configuration loaded successfully");
    }
    
    /**
     * Initialize all plugin managers
     */
    private void initializeManagers() {
        // Initialize player data manager
        playerDataManager = new PlayerDataManager(this);
        
        // Initialize FPS manager
        fpsManager = new FPSManager(this);
        
        // Initialize optimization manager
        optimizationManager = new OptimizationManager(this);
        
        // Initialize performance monitor
        performanceMonitor = new PerformanceMonitor(this);
        
        getLogger().info("All managers initialized successfully");
    }
    
    /**
     * Register plugin commands
     */
    private void registerCommands() {
        // Register FPS command
        getCommand("fps").setExecutor(new FPSCommand(this));
        getCommand("fps").setTabCompleter(new FPSCommand(this));
        
        // Register main plugin command
        getCommand("fpsboost").setExecutor(new FPSBoostCommand(this));
        getCommand("fpsboost").setTabCompleter(new FPSBoostCommand(this));
        
        // Register toggle command
        getCommand("togglefps").setExecutor(new ToggleFPSCommand(this));
        getCommand("togglefps").setTabCompleter(new ToggleFPSCommand(this));
        
        getLogger().info("Commands registered successfully");
    }
    
    /**
     * Register event listeners
     */
    private void registerListeners() {
        // Player listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        
        // Performance listeners
        getServer().getPluginManager().registerEvents(new PerformanceListener(this), this);
        
        // Entity listeners for optimizations
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
        
        // Chunk listeners for chunk optimizations
        getServer().getPluginManager().registerEvents(new ChunkListener(this), this);
        
        getLogger().info("Event listeners registered successfully");
    }
    
    /**
     * Start performance monitoring tasks
     */
    private void startPerformanceMonitoring() {
        if (!configManager.getConfig().getBoolean("general.enabled", true)) {
            return;
        }
        
        // Start performance monitoring task
        long checkInterval = configManager.getConfig().getLong("auto-scaling.check-interval", 30) * 20L; // Convert to ticks
        performanceTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            performanceMonitor.checkPerformance();
        }, 0L, checkInterval);
        
        // Start FPS display task if enabled
        if (configManager.getConfig().getBoolean("fps-display.enabled", true)) {
            long displayInterval = configManager.getConfig().getLong("fps-display.update-interval", 1000) / 50L; // Convert to ticks
            fpsDisplayTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
                fpsManager.updateFPSDisplay();
            }, 0L, displayInterval);
        }
        
        getLogger().info("Performance monitoring started");
    }
    
    /**
     * Check for plugin compatibility issues
     */
    private void checkPluginCompatibility() {
        if (!configManager.getConfig().getBoolean("compatibility.check-conflicts", true)) {
            return;
        }
        
        CompatibilityChecker compatibilityChecker = new CompatibilityChecker(this);
        compatibilityChecker.checkCompatibility();
    }
    
    /**
     * Stop all scheduled tasks
     */
    private void stopTasks() {
        if (performanceTask != null && !performanceTask.isCancelled()) {
            performanceTask.cancel();
        }
        
        if (fpsDisplayTask != null && !fpsDisplayTask.isCancelled()) {
            fpsDisplayTask.cancel();
        }
        
        // Cancel all tasks belonging to this plugin
        Bukkit.getScheduler().cancelTasks(this);
    }
    
    /**
     * Cleanup all managers and resources
     */
    private void cleanup() {
        if (optimizationManager != null) {
            optimizationManager.cleanup();
        }
        
        if (fpsManager != null) {
            fpsManager.cleanup();
        }
        
        if (performanceMonitor != null) {
            performanceMonitor.cleanup();
        }
        
        if (playerDataManager != null) {
            playerDataManager.cleanup();
        }
    }
    
    /**
     * Reload the plugin configuration and restart systems
     */
    public void reloadPlugin() {
        getLogger().info("Reloading FPS Boost Plugin...");
        
        try {
            // Stop current tasks
            stopTasks();
            
            // Reload configuration
            reloadConfig();
            configManager.loadConfig();
            
            // Restart managers
            if (optimizationManager != null) {
                optimizationManager.reload();
            }
            
            if (fpsManager != null) {
                fpsManager.reload();
            }
            
            if (performanceMonitor != null) {
                performanceMonitor.reload();
            }
            
            // Restart performance monitoring
            startPerformanceMonitoring();
            
            getLogger().info("Plugin reloaded successfully!");
            
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error reloading plugin", e);
        }
    }
    
    /**
     * Toggle plugin enabled state
     */
    public void togglePlugin() {
        enabled = !enabled;
        
        if (enabled) {
            startPerformanceMonitoring();
            getLogger().info("FPS Boost Plugin enabled");
        } else {
            stopTasks();
            getLogger().info("FPS Boost Plugin disabled");
        }
    }
    
    // Getters for managers and utilities
    public static FPSBoostPlugin getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public FPSManager getFPSManager() {
        return fpsManager;
    }
    
    public OptimizationManager getOptimizationManager() {
        return optimizationManager;
    }
    
    public PerformanceMonitor getPerformanceMonitor() {
        return performanceMonitor;
    }
    
    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
    
    public boolean isFPSBoostEnabled() {
        return enabled;
    }
    
    /**
     * Utility method to send colored messages to console
     */
    public void log(String message) {
        getLogger().info(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message)));
    }
    
    /**
     * Utility method to send debug messages
     */
    public void debug(String message) {
        if (configManager != null && configManager.isDebugEnabled()) {
            getLogger().info("[DEBUG] " + ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message)));
        }
    }
}
