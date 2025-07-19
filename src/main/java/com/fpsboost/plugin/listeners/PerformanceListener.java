package com.fpsboost.plugin.listeners;

import com.fpsboost.plugin.FPSBoostPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

/**
 * Performance-related event listener
 * 
 * @author SlayerGamerYT
 */
public class PerformanceListener implements Listener {
    
    private final FPSBoostPlugin plugin;
    
    public PerformanceListener(FPSBoostPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        // Track chunk loading for optimization purposes
        plugin.debug("Chunk loaded at " + event.getChunk().getX() + ", " + event.getChunk().getZ());
    }
    
    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        // Track chunk unloading for optimization purposes
        plugin.debug("Chunk unloaded at " + event.getChunk().getX() + ", " + event.getChunk().getZ());
    }
}
