package gz.devian.afkzoneedtools.managers;

import gz.devian.afkzoneedtools.AfkZoneEdtools;
import gz.devian.afkzoneedtools.models.AfkZone;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all AFK zones - loading, saving, and accessing zone configurations
 */
public class ZoneManager {
    
    private final AfkZoneEdtools plugin;
    private final Map<String, AfkZone> zones = new ConcurrentHashMap<>();
    
    public ZoneManager(AfkZoneEdtools plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Load all zones from configuration
     */
    public void loadZones() {
        zones.clear();
        
        FileConfiguration config = plugin.getConfigManager().getZonesConfig();
        ConfigurationSection zonesSection = config.getConfigurationSection("zones");
        
        if (zonesSection == null) {
            plugin.getLogger().warning("No zones found in zones.yml!");
            return;
        }
        
        for (String zoneId : zonesSection.getKeys(false)) {
            try {
                AfkZone zone = loadZone(zoneId, zonesSection.getConfigurationSection(zoneId));
                if (zone != null && zone.isEnabled()) {
                    zones.put(zoneId, zone);
                    plugin.getLogger().info("Loaded AFK zone: " + zoneId);
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to load zone " + zoneId + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        plugin.getLogger().info("Loaded " + zones.size() + " AFK zones");
    }
    
    /**
     * Load a single zone from configuration
     */
    private AfkZone loadZone(String zoneId, ConfigurationSection section) {
        if (section == null) return null;
        
        AfkZone zone = new AfkZone(zoneId);
        
        // Basic settings
        zone.setDisplayName(section.getString("display_name", zoneId));
        zone.setType(AfkZone.ZoneType.valueOf(section.getString("type", "CUSTOM").toUpperCase()));
        
        // Zone boundaries
        zone.setMinCorner(parseLocation(section.getString("min_corner")));
        zone.setMaxCorner(parseLocation(section.getString("max_corner")));
        zone.setCenterLocation(parseLocation(section.getString("center_location")));
        zone.setBlockMaterial(Material.valueOf(section.getString("block_material", "STONE").toUpperCase()));
        
        // Block display settings
        if (section.contains("block_display")) {
            ConfigurationSection displaySection = section.getConfigurationSection("block_display");
            zone.setCustomModelData(displaySection.getInt("custom_model_data", 0));
            zone.setBlockScale(displaySection.getDouble("scale", 1.0));
            zone.setBlockGlow(displaySection.getBoolean("glow", true));
            zone.setGlowColor(displaySection.getString("glow_color", "YELLOW"));
        }
        
        // Allowed tools
        zone.setAllowedTools(section.getStringList("allowed_tools"));
        
        // Harvest settings
        zone.setHarvestInterval(section.getLong("harvest_interval", 5000));
        zone.setRegenerationTime(section.getLong("regeneration_time", 3000));
        
        // Reward settings
        zone.setRewardCurrency(section.getString("reward_currency", "farm-coins"));
        zone.setBaseReward(section.getDouble("base_reward", 100));
        
        // Worker settings
        zone.setMaxWorkers(section.getInt("max_workers", 5));
        try {
            zone.setWorkerType(EntityType.valueOf(section.getString("worker_type", "VILLAGER").toUpperCase()));
        } catch (IllegalArgumentException e) {
            zone.setWorkerType(EntityType.VILLAGER);
        }
        
        // EdTools integration settings (CRITICAL)
        if (section.contains("edtools_integration")) {
            ConfigurationSection integrationSection = section.getConfigurationSection("edtools_integration");
            zone.setAffectEnchants(integrationSection.getBoolean("affect_enchants", true));
            zone.setAffectSell(integrationSection.getBoolean("affect_sell", true));
            zone.setAffectBlockCurrencies(integrationSection.getBoolean("affect_block_currencies", true));
            zone.setAffectLuckyBlocks(integrationSection.getBoolean("affect_lucky_blocks", true));
            zone.setUseGlobalSession(integrationSection.getBoolean("use_global_session", true));
            zone.setApplyBoosters(integrationSection.getBoolean("apply_boosters", true));
            zone.setGrantExperience(integrationSection.getBoolean("grant_experience", true));
        }
        
        // Effects
        if (section.contains("effects")) {
            ConfigurationSection effectsSection = section.getConfigurationSection("effects");
            zone.setMiningParticles(effectsSection.getBoolean("mining_particles", true));
            zone.setBreakParticles(effectsSection.getBoolean("break_particles", true));
            zone.setSuccessSound(effectsSection.getString("success_sound", "BLOCK_STONE_BREAK"));
            zone.setParticleType(effectsSection.getString("particle_type", "BLOCK_CRACK"));
        }
        
        // Status
        zone.setEnabled(section.getBoolean("enabled", true));
        
        return zone;
    }
    
    /**
     * Save all zones to configuration
     */
    public void saveZones() {
        FileConfiguration config = plugin.getConfigManager().getZonesConfig();
        
        for (AfkZone zone : zones.values()) {
            saveZone(zone, config);
        }
        
        plugin.getConfigManager().saveZonesConfig();
    }
    
    /**
     * Save a single zone to configuration
     */
    private void saveZone(AfkZone zone, FileConfiguration config) {
        String path = "zones." + zone.getId();
        
        config.set(path + ".display_name", zone.getDisplayName());
        config.set(path + ".type", zone.getType().name());
        config.set(path + ".center_location", locationToString(zone.getCenterLocation()));
        config.set(path + ".block_material", zone.getBlockMaterial().name());
        
        // Block display
        config.set(path + ".block_display.custom_model_data", zone.getCustomModelData());
        config.set(path + ".block_display.scale", zone.getBlockScale());
        config.set(path + ".block_display.glow", zone.isBlockGlow());
        config.set(path + ".block_display.glow_color", zone.getGlowColor());
        
        // Tools
        config.set(path + ".allowed_tools", zone.getAllowedTools());
        
        // Harvest
        config.set(path + ".harvest_interval", zone.getHarvestInterval());
        config.set(path + ".regeneration_time", zone.getRegenerationTime());
        
        // Rewards
        config.set(path + ".reward_currency", zone.getRewardCurrency());
        config.set(path + ".base_reward", zone.getBaseReward());
        
        // Workers
        config.set(path + ".max_workers", zone.getMaxWorkers());
        config.set(path + ".worker_type", zone.getWorkerType().name());
        
        // EdTools integration
        config.set(path + ".edtools_integration.affect_enchants", zone.isAffectEnchants());
        config.set(path + ".edtools_integration.affect_sell", zone.isAffectSell());
        config.set(path + ".edtools_integration.affect_block_currencies", zone.isAffectBlockCurrencies());
        config.set(path + ".edtools_integration.affect_lucky_blocks", zone.isAffectLuckyBlocks());
        config.set(path + ".edtools_integration.use_global_session", zone.isUseGlobalSession());
        config.set(path + ".edtools_integration.apply_boosters", zone.isApplyBoosters());
        config.set(path + ".edtools_integration.grant_experience", zone.isGrantExperience());
        
        // Effects
        config.set(path + ".effects.mining_particles", zone.isMiningParticles());
        config.set(path + ".effects.break_particles", zone.isBreakParticles());
        config.set(path + ".effects.success_sound", zone.getSuccessSound());
        config.set(path + ".effects.particle_type", zone.getParticleType());
        
        // Status
        config.set(path + ".enabled", zone.isEnabled());
    }
    
    /**
     * Parse location from string format: "world:x:y:z"
     */
    private Location parseLocation(String locationString) {
        if (locationString == null) return null;
        
        try {
            String[] parts = locationString.split(":");
            if (parts.length != 4) return null;
            
            return new Location(
                Bukkit.getWorld(parts[0]),
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2]),
                Double.parseDouble(parts[3])
            );
        } catch (Exception e) {
            plugin.getLogger().warning("Invalid location format: " + locationString);
            return null;
        }
    }
    
    /**
     * Convert location to string format: "world:x:y:z"
     */
    private String locationToString(Location location) {
        if (location == null || location.getWorld() == null) return null;
        
        return location.getWorld().getName() + ":" +
               location.getX() + ":" +
               location.getY() + ":" +
               location.getZ();
    }
    
    /**
     * Get a zone by ID
     */
    public AfkZone getZone(String zoneId) {
        return zones.get(zoneId);
    }
    
    /**
     * Get all zones
     */
    public Collection<AfkZone> getAllZones() {
        return zones.values();
    }
    
    /**
     * Get zone by location
     */
    public AfkZone getZoneByLocation(Location location) {
        for (AfkZone zone : zones.values()) {
            if (zone.getCenterLocation() != null && 
                zone.getCenterLocation().getWorld().equals(location.getWorld()) &&
                zone.getCenterLocation().distance(location) < 50) { // Within 50 blocks
                return zone;
            }
        }
        return null;
    }
    
    /**
     * Add or update a zone
     */
    public void addZone(AfkZone zone) {
        zones.put(zone.getId(), zone);
    }
    
    /**
     * Remove a zone
     */
    public void removeZone(String zoneId) {
        zones.remove(zoneId);
    }
    
    /**
     * Check if a zone exists
     */
    public boolean zoneExists(String zoneId) {
        return zones.containsKey(zoneId);
    }
    
    /**
     * Get zone by center location
     */
    public AfkZone getZoneByCenterLocation(Location location) {
        if (location == null) {
            return null;
        }
        
        for (AfkZone zone : zones.values()) {
            if (zone.getCenterLocation() != null && 
                zone.getCenterLocation().getWorld().equals(location.getWorld()) &&
                zone.getCenterLocation().getBlockX() == location.getBlockX() &&
                zone.getCenterLocation().getBlockY() == location.getBlockY() &&
                zone.getCenterLocation().getBlockZ() == location.getBlockZ()) {
                return zone;
            }
        }
        
        return null;
    }
}
