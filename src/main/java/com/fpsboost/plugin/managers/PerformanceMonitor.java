package com.fpsboost.plugin.managers;

import com.fpsboost.plugin.FPSBoostPlugin;
import org.bukkit.Bukkit;

/**
 * Monitors server performance and triggers automatic optimizations.
 */
public class PerformanceMonitor {
    private final FPSBoostPlugin plugin;
    private double lastTPS = 20.0;
    private long lastMemoryUsage = 0;

    public PerformanceMonitor(FPSBoostPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Check current server performance and apply optimizations if needed
     */
    public void checkPerformance() {
        double currentTPS = getServerTPS();
        long memoryUsage = getMemoryUsage();
        int playerCount = Bukkit.getOnlinePlayers().size();
        
        // Check TPS thresholds
        checkTPSThresholds(currentTPS);
        
        // Check memory thresholds
        checkMemoryThresholds(memoryUsage);
        
        // Check player count thresholds
        checkPlayerThresholds(playerCount);
        
        // Store current values
        lastTPS = currentTPS;
        lastMemoryUsage = memoryUsage;
        
        plugin.debug("Performance check - TPS: " + String.format("%.2f", currentTPS) + 
                    ", Memory: " + (memoryUsage / 1024 / 1024) + "MB" + 
                    ", Players: " + playerCount);
    }

    private double getServerTPS() {
        try {
            return Bukkit.getServer().getTPS()[0];
        } catch (Exception e) {
            return 20.0;
        }
    }

    private long getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    private void checkTPSThresholds(double tps) {
        double warningTPS = plugin.getConfigManager().getConfig().getDouble("thresholds.tps.warning", 18.0);
        double criticalTPS = plugin.getConfigManager().getConfig().getDouble("thresholds.tps.critical", 15.0);
        double emergencyTPS = plugin.getConfigManager().getConfig().getDouble("thresholds.tps.emergency", 10.0);
        
        if (tps <= emergencyTPS) {
            plugin.getOptimizationManager().applyEmergencyOptimizations();
        } else if (tps <= criticalTPS) {
            // Apply critical optimizations
            plugin.log("&cServer TPS critical (" + String.format("%.2f", tps) + ")! Applying critical optimizations...");
        } else if (tps <= warningTPS) {
            // Apply warning level optimizations
            plugin.log("&eServer TPS warning (" + String.format("%.2f", tps) + ")! Applying basic optimizations...");
        }
    }

    private void checkMemoryThresholds(long memoryUsage) {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        double memoryPercent = (double) memoryUsage / maxMemory * 100;
        
        int warningPercent = plugin.getConfigManager().getConfig().getInt("thresholds.memory.warning", 70);
        int criticalPercent = plugin.getConfigManager().getConfig().getInt("thresholds.memory.critical", 85);
        int emergencyPercent = plugin.getConfigManager().getConfig().getInt("thresholds.memory.emergency", 95);
        
        if (memoryPercent >= emergencyPercent) {
            plugin.log("&cMemory usage critical (" + String.format("%.1f", memoryPercent) + "%)! Running garbage collection...");
            System.gc();
        } else if (memoryPercent >= criticalPercent) {
            plugin.log("&cMemory usage critical (" + String.format("%.1f", memoryPercent) + "%)!");
        } else if (memoryPercent >= warningPercent) {
            plugin.log("&eMemory usage warning (" + String.format("%.1f", memoryPercent) + "%)!");
        }
    }

    private void checkPlayerThresholds(int playerCount) {
        int mediumLoad = plugin.getConfigManager().getConfig().getInt("thresholds.players.medium-load", 20);
        int highLoad = plugin.getConfigManager().getConfig().getInt("thresholds.players.high-load", 50);
        int extremeLoad = plugin.getConfigManager().getConfig().getInt("thresholds.players.extreme-load", 100);
        
        if (playerCount >= extremeLoad) {
            plugin.debug("Extreme player load detected (" + playerCount + " players)");
        } else if (playerCount >= highLoad) {
            plugin.debug("High player load detected (" + playerCount + " players)");
        } else if (playerCount >= mediumLoad) {
            plugin.debug("Medium player load detected (" + playerCount + " players)");
        }
    }

    /**
     * Get performance statistics
     */
    public String getPerformanceStats() {
        double currentTPS = getServerTPS();
        long memoryUsage = getMemoryUsage();
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        double memoryPercent = (double) memoryUsage / maxMemory * 100;
        int playerCount = Bukkit.getOnlinePlayers().size();
        
        return String.format(
            "&6=== Server Performance ===\n" +
            "&aTPS: &f%.2f\n" +
            "&aMemory: &f%dMB / %dMB (%.1f%%)\n" +
            "&aPlayers: &f%d\n" +
            "&aUptime: &f%s",
            currentTPS,
            memoryUsage / 1024 / 1024,
            maxMemory / 1024 / 1024,
            memoryPercent,
            playerCount,
            formatUptime(System.currentTimeMillis())
        );
    }

    private String formatUptime(long currentTime) {
        // Simple uptime formatting - in a real implementation you'd track start time
        return "N/A";
    }

    public void reload() {
        plugin.debug("Performance Monitor reloaded");
    }

    /**
     * Get current performance state
     */
    public String getCurrentPerformanceState() {
        double currentTPS = getServerTPS();
        long memoryUsage = getMemoryUsage();
        
        if (currentTPS < 15.0) {
            return "CRITICAL";
        } else if (currentTPS < 18.0) {
            return "WARNING";
        } else {
            return "NORMAL";
        }
    }
    
    public void cleanup() {
        plugin.debug("Cleaning up Performance Monitor...");
    }
}
