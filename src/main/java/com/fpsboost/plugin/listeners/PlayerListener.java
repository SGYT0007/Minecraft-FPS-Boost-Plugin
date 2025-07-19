package com.fpsboost.plugin.listeners;

import com.fpsboost.plugin.FPSBoostPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Player event listener for FPS optimizations
 * 
 * @author SlayerGamerYT
 */
public class PlayerListener implements Listener {
    
    private final FPSBoostPlugin plugin;
    
    public PlayerListener(FPSBoostPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Initialize player data if needed
        if (plugin.getPlayerDataManager() != null) {
            plugin.getPlayerDataManager().loadPlayerData(event.getPlayer().getUniqueId());
        }
        
        // Show FPS info on join if enabled
        if (plugin.getConfigManager().getConfig().getBoolean("fps-display.show-on-join", true)) {
            // Delayed to ensure player is fully loaded
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (plugin.getFPSManager() != null) {
                    plugin.getFPSManager().setFPSDisplay(event.getPlayer(), true);
                }
            }, 20L); // 1 second delay
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Save player data on quit
        if (plugin.getPlayerDataManager() != null) {
            plugin.getPlayerDataManager().savePlayerData(event.getPlayer().getUniqueId());
        }
    }
}
