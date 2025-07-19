package com.fpsboost.plugin.commands;

import com.fpsboost.plugin.FPSBoostPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * FPS Command for displaying current FPS and performance statistics
 * 
 * @author SlayerGamerYT
 */
public class FPSCommand implements CommandExecutor, TabCompleter {
    
    private final FPSBoostPlugin plugin;
    
    public FPSCommand(FPSBoostPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("fpsboost.fps")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }
        
        if (args.length == 0) {
            // Show FPS info for command sender (if player) or server stats
            if (sender instanceof Player) {
                Player player = (Player) sender;
                showFPSInfo(player);
            } else {
                showServerStats(sender);
            }
        } else if (args.length == 1) {
            // Show FPS info for specified player
            if (!sender.hasPermission("fpsboost.fps.others")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to check other players' FPS!");
                return true;
            }
            
            Player targetPlayer = plugin.getServer().getPlayer(args[0]);
            if (targetPlayer == null) {
                sender.sendMessage(ChatColor.RED + "Player not found!");
                return true;
            }
            
            showFPSInfo(targetPlayer);
            sender.sendMessage(ChatColor.GREEN + "Displayed FPS info to " + targetPlayer.getName());
        }
        
        return true;
    }
    
    private void showFPSInfo(Player player) {
        // Get current server TPS
        double tps = getCurrentTPS();
        
        // Send FPS info to player
        player.sendMessage(ChatColor.GOLD + "=== FPS & Performance Info ===");
        player.sendMessage(ChatColor.YELLOW + "Server TPS: " + ChatColor.WHITE + String.format("%.2f", tps));
        player.sendMessage(ChatColor.YELLOW + "Your Ping: " + ChatColor.WHITE + player.getPing() + "ms");
        player.sendMessage(ChatColor.YELLOW + "Server Performance: " + getPerformanceColor(tps) + getPerformanceStatus(tps));
        
        if (plugin.getConfigManager().getConfig().getBoolean("fps-display.enabled", true)) {
            player.sendMessage(ChatColor.GREEN + "FPS display is currently enabled");
        } else {
            player.sendMessage(ChatColor.RED + "FPS display is currently disabled");
        }
    }
    
    private void showServerStats(CommandSender sender) {
        double tps = getCurrentTPS();
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        long maxMemory = runtime.maxMemory() / 1024 / 1024;
        
        sender.sendMessage(ChatColor.GOLD + "=== Server Performance Stats ===");
        sender.sendMessage(ChatColor.YELLOW + "Server TPS: " + ChatColor.WHITE + String.format("%.2f", tps));
        sender.sendMessage(ChatColor.YELLOW + "Memory Usage: " + ChatColor.WHITE + usedMemory + "MB / " + maxMemory + "MB");
        sender.sendMessage(ChatColor.YELLOW + "Performance: " + getPerformanceColor(tps) + getPerformanceStatus(tps));
        sender.sendMessage(ChatColor.YELLOW + "Online Players: " + ChatColor.WHITE + plugin.getServer().getOnlinePlayers().size());
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
    
    private ChatColor getPerformanceColor(double tps) {
        if (tps >= 19.5) return ChatColor.GREEN;
        if (tps >= 18.0) return ChatColor.YELLOW;
        if (tps >= 15.0) return ChatColor.GOLD;
        return ChatColor.RED;
    }
    
    private String getPerformanceStatus(double tps) {
        if (tps >= 19.5) return "Excellent";
        if (tps >= 18.0) return "Good";
        if (tps >= 15.0) return "Fair";
        return "Poor";
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1 && sender.hasPermission("fpsboost.fps.others")) {
            String partial = args[0].toLowerCase();
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(partial)) {
                    completions.add(player.getName());
                }
            }
        }
        
        return completions;
    }
}
