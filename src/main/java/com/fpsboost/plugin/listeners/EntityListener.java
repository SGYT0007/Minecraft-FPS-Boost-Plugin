package com.fpsboost.plugin.listeners;

import com.fpsboost.plugin.FPSBoostPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

/**
 * Entity event listener for monitoring purposes (no interference with normal behavior)
 * 
 * @author SlayerGamerYT
 */
public class EntityListener implements Listener {
    
    private final FPSBoostPlugin plugin;
    
    public EntityListener(FPSBoostPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        // Monitor entity spawning for performance statistics only
        // No interference with normal entity behavior
        plugin.debug("Entity spawned: " + event.getEntityType() + " at " + 
                    event.getLocation().getWorld().getName());
    }
}
