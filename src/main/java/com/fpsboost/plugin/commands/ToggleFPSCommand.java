package com.fpsboost.plugin.commands;

import com.fpsboost.plugin.FPSBoostPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Toggle FPS command for enabling/disabling specific FPS optimizations
 * 
 * @author SlayerGamerYT
 */
public class ToggleFPSCommand implements CommandExecutor, TabCompleter {
    
    private final FPSBoostPlugin plugin;
    
    public ToggleFPSCommand(FPSBoostPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("fpsboost.toggle")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }
        
        if (args.length == 0) {
            showToggleHelp(sender);
            return true;
        }
        
        String feature = args[0].toLowerCase();
        
        switch (feature) {
            case "display":
                toggleFPSDisplay(sender);
                break;
            case "optimizations":
                toggleOptimizations(sender);
                break;
            case "monitoring":
                toggleMonitoring(sender);
                break;
            case "particles":
                toggleParticleOptimization(sender);
                break;
            case "chunks":
                toggleChunkOptimization(sender);
                break;
            case "memory":
                toggleMemoryOptimization(sender);
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Unknown feature: " + feature);
                showToggleHelp(sender);
                break;
        }
        
        return true;
    }
    
    private void showToggleHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Toggle FPS Features ===");
        sender.sendMessage(ChatColor.YELLOW + "/togglefps display" + ChatColor.WHITE + " - Toggle FPS display");
        sender.sendMessage(ChatColor.YELLOW + "/togglefps optimizations" + ChatColor.WHITE + " - Toggle all optimizations");
        sender.sendMessage(ChatColor.YELLOW + "/togglefps monitoring" + ChatColor.WHITE + " - Toggle performance monitoring");
        sender.sendMessage(ChatColor.YELLOW + "/togglefps particles" + ChatColor.WHITE + " - Toggle particle optimizations");
        sender.sendMessage(ChatColor.YELLOW + "/togglefps chunks" + ChatColor.WHITE + " - Toggle chunk optimizations");
        sender.sendMessage(ChatColor.YELLOW + "/togglefps memory" + ChatColor.WHITE + " - Toggle memory optimizations");
    }
    
    private void toggleFPSDisplay(CommandSender sender) {
        boolean currentState = plugin.getConfigManager().getConfig().getBoolean("fps-display.enabled", true);
        boolean newState = !currentState;
        
        plugin.getConfigManager().getConfig().set("fps-display.enabled", newState);
        plugin.getConfigManager().saveConfig();
        
        sender.sendMessage(ChatColor.GREEN + "FPS Display " + (newState ? "enabled" : "disabled") + "!");
        
        if (plugin.getFPSManager() != null) {
            plugin.getFPSManager().reload();
        }
    }
    
    private void toggleOptimizations(CommandSender sender) {
        boolean currentState = plugin.getConfigManager().getConfig().getBoolean("general.enabled", true);
        boolean newState = !currentState;
        
        plugin.getConfigManager().getConfig().set("general.enabled", newState);
        plugin.getConfigManager().saveConfig();
        
        sender.sendMessage(ChatColor.GREEN + "All optimizations " + (newState ? "enabled" : "disabled") + "!");
        
        // Reload the plugin to apply changes
        plugin.reloadPlugin();
    }
    
    private void toggleMonitoring(CommandSender sender) {
        boolean currentState = plugin.getConfigManager().getConfig().getBoolean("performance-monitoring.enabled", true);
        boolean newState = !currentState;
        
        plugin.getConfigManager().getConfig().set("performance-monitoring.enabled", newState);
        plugin.getConfigManager().saveConfig();
        
        sender.sendMessage(ChatColor.GREEN + "Performance monitoring " + (newState ? "enabled" : "disabled") + "!");
        
        if (plugin.getPerformanceMonitor() != null) {
            if (!newState) {
                plugin.getPerformanceMonitor().cleanup();
            }
        }
    }
    
    private void toggleParticleOptimization(CommandSender sender) {
        boolean currentState = plugin.getConfigManager().getConfig().getBoolean("visual-optimizations.particle-optimization", true);
        boolean newState = !currentState;
        
        plugin.getConfigManager().getConfig().set("visual-optimizations.particle-optimization", newState);
        plugin.getConfigManager().saveConfig();
        
        sender.sendMessage(ChatColor.GREEN + "Particle optimizations " + (newState ? "enabled" : "disabled") + "!");
    }
    
    private void toggleChunkOptimization(CommandSender sender) {
        boolean currentState = plugin.getConfigManager().getConfig().getBoolean("intelligent-chunk-management.enabled", true);
        boolean newState = !currentState;
        
        plugin.getConfigManager().getConfig().set("intelligent-chunk-management.enabled", newState);
        plugin.getConfigManager().saveConfig();
        
        sender.sendMessage(ChatColor.GREEN + "Chunk optimizations " + (newState ? "enabled" : "disabled") + "!");
    }
    
    private void toggleMemoryOptimization(CommandSender sender) {
        boolean currentState = plugin.getConfigManager().getConfig().getBoolean("advanced-memory-optimization.enabled", true);
        boolean newState = !currentState;
        
        plugin.getConfigManager().getConfig().set("advanced-memory-optimization.enabled", newState);
        plugin.getConfigManager().saveConfig();
        
        sender.sendMessage(ChatColor.GREEN + "Memory optimizations " + (newState ? "enabled" : "disabled") + "!");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> features = Arrays.asList("display", "optimizations", "monitoring", "particles", "chunks", "memory");
            String partial = args[0].toLowerCase();
            
            for (String feature : features) {
                if (feature.startsWith(partial)) {
                    completions.add(feature);
                }
            }
        }
        
        return completions;
    }
}
