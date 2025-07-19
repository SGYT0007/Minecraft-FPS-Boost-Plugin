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
 * Main FPSBoost command for plugin administration
 * 
 * @author SlayerGamerYT
 */
public class FPSBoostCommand implements CommandExecutor, TabCompleter {
    
    private final FPSBoostPlugin plugin;
    
    public FPSBoostCommand(FPSBoostPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("fpsboost.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }
        
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "reload":
                reloadPlugin(sender);
                break;
            case "toggle":
                togglePlugin(sender);
                break;
            case "status":
                showStatus(sender);
                break;
            case "config":
                showConfig(sender);
                break;
            case "stats":
                showStats(sender);
                break;
            case "emergency":
                triggerEmergencyOptimizations(sender);
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Unknown subcommand: " + subCommand);
                showHelp(sender);
                break;
        }
        
        return true;
    }
    
    private void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== FPS Boost Plugin Commands ===");
        sender.sendMessage(ChatColor.YELLOW + "/fpsboost reload" + ChatColor.WHITE + " - Reload plugin configuration");
        sender.sendMessage(ChatColor.YELLOW + "/fpsboost toggle" + ChatColor.WHITE + " - Toggle plugin on/off");
        sender.sendMessage(ChatColor.YELLOW + "/fpsboost status" + ChatColor.WHITE + " - Show plugin status");
        sender.sendMessage(ChatColor.YELLOW + "/fpsboost config" + ChatColor.WHITE + " - Show current configuration");
        sender.sendMessage(ChatColor.YELLOW + "/fpsboost stats" + ChatColor.WHITE + " - Show performance statistics");
        sender.sendMessage(ChatColor.YELLOW + "/fpsboost emergency" + ChatColor.WHITE + " - Trigger emergency optimizations");
    }
    
    private void reloadPlugin(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Reloading FPS Boost Plugin...");
        try {
            plugin.reloadPlugin();
            sender.sendMessage(ChatColor.GREEN + "Plugin reloaded successfully!");
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Error reloading plugin: " + e.getMessage());
        }
    }
    
    private void togglePlugin(CommandSender sender) {
        plugin.togglePlugin();
        boolean enabled = plugin.isEnabled();
        sender.sendMessage(ChatColor.GREEN + "FPS Boost Plugin " + 
                          (enabled ? "enabled" : "disabled") + "!");
    }
    
    private void showStatus(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== FPS Boost Plugin Status ===");
        sender.sendMessage(ChatColor.YELLOW + "Plugin Status: " + 
                          (plugin.isEnabled() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
        sender.sendMessage(ChatColor.YELLOW + "Version: " + ChatColor.WHITE + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.YELLOW + "Author: " + ChatColor.WHITE + plugin.getDescription().getAuthors().toString());
        
        // Show performance state if available
        if (plugin.getPerformanceMonitor() != null) {
            sender.sendMessage(ChatColor.YELLOW + "Performance State: " + ChatColor.WHITE + 
                             plugin.getPerformanceMonitor().getCurrentPerformanceState());
        }
    }
    
    private void showConfig(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Current Configuration ===");
        
        boolean generalEnabled = plugin.getConfigManager().getConfig().getBoolean("general.enabled", true);
        boolean debugEnabled = plugin.getConfigManager().getConfig().getBoolean("general.debug", false);
        boolean fpsDisplayEnabled = plugin.getConfigManager().getConfig().getBoolean("fps-display.enabled", true);
        
        sender.sendMessage(ChatColor.YELLOW + "General Enabled: " + (generalEnabled ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));
        sender.sendMessage(ChatColor.YELLOW + "Debug Mode: " + (debugEnabled ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));
        sender.sendMessage(ChatColor.YELLOW + "FPS Display: " + (fpsDisplayEnabled ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));
        
        sender.sendMessage(ChatColor.GRAY + "Use /fpsboost reload after making changes to config.yml");
    }
    
    private void showStats(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Performance Statistics ===");
        
        // Get current TPS
        double tps = getCurrentTPS();
        sender.sendMessage(ChatColor.YELLOW + "Current TPS: " + ChatColor.WHITE + String.format("%.2f", tps));
        
        // Memory info
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        long maxMemory = runtime.maxMemory() / 1024 / 1024;
        double memoryPercent = (double) usedMemory / maxMemory * 100;
        
        sender.sendMessage(ChatColor.YELLOW + "Memory Usage: " + ChatColor.WHITE + 
                          usedMemory + "MB / " + maxMemory + "MB (" + String.format("%.1f%%", memoryPercent) + ")");
        
        // Thread info
        sender.sendMessage(ChatColor.YELLOW + "Active Threads: " + ChatColor.WHITE + Thread.activeCount());
        
        // Online players
        sender.sendMessage(ChatColor.YELLOW + "Online Players: " + ChatColor.WHITE + plugin.getServer().getOnlinePlayers().size());
        
        // Entity count
        int totalEntities = plugin.getServer().getWorlds().stream()
                           .mapToInt(world -> world.getEntities().size())
                           .sum();
        sender.sendMessage(ChatColor.YELLOW + "Total Entities: " + ChatColor.WHITE + totalEntities);
    }
    
    private void triggerEmergencyOptimizations(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Triggering emergency optimizations...");
        
        if (plugin.getOptimizationManager() != null) {
            plugin.getOptimizationManager().applyEmergencyOptimizations();
            sender.sendMessage(ChatColor.GREEN + "Emergency optimizations applied!");
        } else {
            sender.sendMessage(ChatColor.RED + "Optimization manager not available!");
        }
    }
    
    private double getCurrentTPS() {
        try {
            Object server = plugin.getServer();
            double[] tps = (double[]) server.getClass().getMethod("getTPS").invoke(server);
            return Math.min(tps[0], 20.0);
        } catch (Exception e) {
            return 20.0;
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("reload", "toggle", "status", "config", "stats", "emergency");
            String partial = args[0].toLowerCase();
            
            for (String subCmd : subCommands) {
                if (subCmd.startsWith(partial)) {
                    completions.add(subCmd);
                }
            }
        }
        
        return completions;
    }
}
