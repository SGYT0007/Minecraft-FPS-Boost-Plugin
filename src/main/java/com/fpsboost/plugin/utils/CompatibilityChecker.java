package com.fpsboost.plugin.utils;

import com.fpsboost.plugin.FPSBoostPlugin;
import org.bukkit.plugin.Plugin;

/**
 * Utility class for checking plugin compatibility.
 */
public class CompatibilityChecker {
    private final FPSBoostPlugin plugin;

    public CompatibilityChecker(FPSBoostPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Check compatibility with known plugins.
     */
    public void checkCompatibility() {
        String[] compatiblePlugins = plugin.getConfigManager().getConfig().getStringList("compatibility.compatible-plugins").toArray(new String[0]);
        String[] conflictingPlugins = plugin.getConfigManager().getConfig().getStringList("compatibility.conflicting-plugins").toArray(new String[0]);

        for (String otherPlugin : compatiblePlugins) {
            if (isPluginEnabled(otherPlugin)) {
                plugin.log("[32mCompatible with " + otherPlugin + "[0m");
            }
        }

        for (String otherPlugin : conflictingPlugins) {
            if (isPluginEnabled(otherPlugin)) {
                plugin.log("[31mConflict detected with " + otherPlugin + "[0m");
            }
        }
    }

    /**
     * Check if another plugin is enabled.
     * @param pluginName The name of the other plugin.
     * @return True if the plugin is enabled, false otherwise.
     */
    private boolean isPluginEnabled(String pluginName) {
        Plugin otherPlugin = plugin.getServer().getPluginManager().getPlugin(pluginName);
        return otherPlugin != null && otherPlugin.isEnabled();
    }
}
