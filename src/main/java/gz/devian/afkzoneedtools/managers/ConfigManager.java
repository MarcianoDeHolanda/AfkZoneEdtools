package gz.devian.afkzoneedtools.managers;

import gz.devian.afkzoneedtools.AfkZoneEdtools;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages all configuration files for the plugin
 */
public class ConfigManager {
    
    private final AfkZoneEdtools plugin;
    private FileConfiguration config;
    private FileConfiguration zonesConfig;
    private File zonesFile;
    
    private final Map<String, String> messages = new HashMap<>();
    
    public ConfigManager(AfkZoneEdtools plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Load all configuration files
     */
    public void loadConfigs() {
        // Save default config
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        
        // Load zones config
        zonesFile = new File(plugin.getDataFolder(), "zones.yml");
        if (!zonesFile.exists()) {
            plugin.saveResource("zones.yml", false);
        }
        zonesConfig = YamlConfiguration.loadConfiguration(zonesFile);
        
        // Load messages
        loadMessages();
        
        plugin.getLogger().info("Configuration loaded successfully!");
    }
    
    /**
     * Reload all configurations
     */
    public void reloadConfigs() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        zonesConfig = YamlConfiguration.loadConfiguration(zonesFile);
        loadMessages();
    }
    
    /**
     * Save zones configuration
     */
    public void saveZonesConfig() {
        try {
            zonesConfig.save(zonesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save zones.yml: " + e.getMessage());
        }
    }
    
    /**
     * Load all messages from config
     */
    private void loadMessages() {
        messages.clear();
        if (config.contains("messages")) {
            for (String key : config.getConfigurationSection("messages").getKeys(false)) {
                messages.put(key, config.getString("messages." + key));
            }
        }
    }
    
    /**
     * Get a formatted message
     */
    public String getMessage(String key) {
        return colorize(getPrefix() + messages.getOrDefault(key, "&cMessage not found: " + key));
    }
    
    /**
     * Get a formatted message with replacements
     */
    public String getMessage(String key, Map<String, String> replacements) {
        String message = getMessage(key);
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return message;
    }
    
    /**
     * Colorize a string
     */
    public String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
    
    // General Settings
    
    public String getPrefix() {
        return config.getString("general.prefix", "&8[&6AFK Zone&8] &7");
    }
    
    public boolean isDebugEnabled() {
        return config.getBoolean("general.debug", false);
    }
    
    public int getAutoSaveInterval() {
        return config.getInt("general.auto_save_interval", 5);
    }
    
    public int getMaxWorkersPerZone() {
        return config.getInt("general.max_workers_per_zone", 10);
    }
    
    public boolean isParticleEffectsEnabled() {
        return config.getBoolean("general.particle_effects", true);
    }
    
    public boolean isSoundEffectsEnabled() {
        return config.getBoolean("general.sound_effects", true);
    }
    
    // Performance Settings
    
    public boolean isAsyncBlockGeneration() {
        return config.getBoolean("performance.async_block_generation", true);
    }
    
    public int getEntityTickRate() {
        return config.getInt("performance.entity_tick_rate", 20);
    }
    
    public int getBlockRegenerationDelay() {
        return config.getInt("performance.block_regeneration_delay", 60);
    }
    
    public int getWorkerUpdateInterval() {
        return config.getInt("performance.worker_update_interval", 10);
    }
    
    // EdTools Integration Settings
    
    public boolean isCurrencyRewardsEnabled() {
        return config.getBoolean("edtools_integration.enable_currency_rewards", true);
    }
    
    public boolean isBoosterEffectsEnabled() {
        return config.getBoolean("edtools_integration.enable_booster_effects", true);
    }
    
    public boolean isEnchantActivationEnabled() {
        return config.getBoolean("edtools_integration.enable_enchant_activation", true);
    }
    
    public boolean isSellIntegrationEnabled() {
        return config.getBoolean("edtools_integration.enable_sell_integration", true);
    }
    
    public boolean isLuckyBlocksEnabled() {
        return config.getBoolean("edtools_integration.enable_lucky_blocks", true);
    }
    
    public boolean isLevelingEnabled() {
        return config.getBoolean("edtools_integration.enable_leveling", true);
    }
    
    /**
     * Get mineBlockAsPlayer() flags - CRITICAL for full EdTools integration
     */
    public boolean isAffectEnchantsEnabled() {
        return config.getBoolean("edtools_integration.mine_block_flags.affect_enchants", true);
    }
    
    public boolean isAffectSellEnabled() {
        return config.getBoolean("edtools_integration.mine_block_flags.affect_sell", true);
    }
    
    public boolean isAffectBlockCurrenciesEnabled() {
        return config.getBoolean("edtools_integration.mine_block_flags.affect_block_currencies", true);
    }
    
    public boolean isAffectLuckyBlocksEnabled() {
        return config.getBoolean("edtools_integration.mine_block_flags.affect_lucky_blocks", true);
    }
    
    // Visual Effects Settings
    
    public boolean isBlockGlowEnabled() {
        return config.getBoolean("visual_effects.block_glow", true);
    }
    
    public String getBlockGlowColor() {
        return config.getString("visual_effects.block_glow_color", "YELLOW");
    }
    
    public boolean isMiningAnimationEnabled() {
        return config.getBoolean("visual_effects.mining_animation", true);
    }
    
    public boolean isBreakParticlesEnabled() {
        return config.getBoolean("visual_effects.break_particles", true);
    }
    
    public boolean isWorkerParticlesEnabled() {
        return config.getBoolean("visual_effects.worker_particles", true);
    }
    
    public String getWorkerParticleType() {
        return config.getString("visual_effects.worker_particle_type", "VILLAGER_HAPPY");
    }
    
    // Zones Config Getters
    
    public FileConfiguration getZonesConfig() {
        return zonesConfig;
    }
}
