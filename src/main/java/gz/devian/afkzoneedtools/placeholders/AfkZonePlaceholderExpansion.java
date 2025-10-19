package gz.devian.afkzoneedtools.placeholders;

import gz.devian.afkzoneedtools.AfkZoneEdtools;
import gz.devian.afkzoneedtools.models.AfkWorker;
import gz.devian.afkzoneedtools.models.AfkZone;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

/**
 * PlaceholderAPI expansion for AfkZoneEdtools
 * Uses reflection to avoid direct dependency on PlaceholderAPI
 */
public class AfkZonePlaceholderExpansion {
    
    private final AfkZoneEdtools plugin;
    
    public AfkZonePlaceholderExpansion(AfkZoneEdtools plugin) {
        this.plugin = plugin;
    }
    
    public boolean register() {
        try {
            plugin.getLogger().info("Initializing PlaceholderAPI expansion...");
            
            // Check if PlaceholderAPI is available
            Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            plugin.getLogger().info("PlaceholderAPI found!");
            
            // Get PlaceholderExpansion class
            Class<?> expansionClass = Class.forName("me.clip.placeholderapi.expansion.PlaceholderExpansion");
            plugin.getLogger().info("PlaceholderExpansion class found!");
            
            // Create expansion using ASM or simple approach
            // Since PlaceholderExpansion is abstract, we need to create a concrete subclass
            Object expansion = createExpansionInstance(expansionClass);
            
            plugin.getLogger().info("PlaceholderAPI expansion created successfully!");
            
            // Register the expansion
            Class<?> placeholderAPI = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            Method registerExpansion = placeholderAPI.getMethod("registerExpansion", expansionClass);
            boolean success = (Boolean) registerExpansion.invoke(null, expansion);
            
            if (success) {
                plugin.getLogger().info("PlaceholderAPI expansion 'afkzone' registered successfully!");
                return true;
            } else {
                plugin.getLogger().warning("Failed to register PlaceholderAPI expansion");
                return false;
            }
            
        } catch (ClassNotFoundException e) {
            plugin.getLogger().info("PlaceholderAPI not found - placeholders will not be available");
            return false;
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to initialize PlaceholderAPI expansion: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private Object createExpansionInstance(Class<?> expansionClass) {
        // Create a simple implementation using anonymous class
        return new Object() {
            public String getIdentifier() { return "afkzone"; }
            public String getAuthor() { return "MarcianoDeHolanda"; }
            public String getVersion() { return plugin.getDescription().getVersion(); }
            public boolean persist() { return true; }
            
            public String onPlaceholderRequest(Player player, String params) {
                return AfkZonePlaceholderExpansion.this.onPlaceholderRequest(player, params);
            }
        };
    }
    
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null) {
            return "";
        }
        
        AfkWorker worker = plugin.getWorkerManager().getWorkerByPlayer(player);
        AfkZone zone = (worker != null) ? worker.getZone() : null;
        
        // %afkzone_active% - Shows if player has active AFK worker
        if (params.equals("active")) {
            return String.valueOf(worker != null);
        }
        
        // %afkzone_zone% - Name of the player's current AFK zone
        if (params.equals("zone")) {
            return zone != null ? zone.getDisplayName() : "N/A";
        }
        
        // %afkzone_zone_id% - ID of the player's current AFK zone
        if (params.equals("zone_id")) {
            return zone != null ? zone.getId() : "N/A";
        }
        
        // %afkzone_harvests% - Total harvests of the current worker
        if (params.equals("harvests")) {
            return worker != null ? String.valueOf(worker.getHarvestCount()) : "0";
        }
        
        // %afkzone_material% - Material being mined
        if (params.equals("material")) {
            return zone != null ? zone.getBlockMaterial().name().toLowerCase().replace("_", " ") : "N/A";
        }
        
        // %afkzone_tool% - Tool being used by the worker
        if (params.equals("tool")) {
            return worker != null && worker.getPlayerToolId() != null ? worker.getPlayerToolId() : "N/A";
        }
        
        // %afkzone_time_left% - Time left until next harvest (in seconds)
        if (params.equals("time_left")) {
            if (worker != null && zone != null) {
                long lastHarvest = worker.getLastHarvestTime();
                long interval = zone.getHarvestInterval(); // in milliseconds
                long nextHarvestTime = lastHarvest + interval;
                long currentTime = System.currentTimeMillis();
                
                if (nextHarvestTime > currentTime) {
                    long timeLeftMillis = nextHarvestTime - currentTime;
                    return String.valueOf(timeLeftMillis / 1000);
                }
            }
            return "0";
        }
        
        // %afkzone_workers_total% - Total active workers on the server
        if (params.equals("workers_total")) {
            return String.valueOf(plugin.getWorkerManager().getAllWorkers().size());
        }
        
        // %afkzone_workers_zone% - Total active workers in the player's current zone
        if (params.equals("workers_zone")) {
            return zone != null ? String.valueOf(plugin.getWorkerManager().getZoneWorkers(zone.getId()).size()) : "0";
        }
        
        // %afkzone_zones_total% - Total loaded AFK zones
        if (params.equals("zones_total")) {
            return String.valueOf(plugin.getZoneManager().getAllZones().size());
        }
        
        // %afkzone_currency% - Currency type for the player's current AFK zone
        if (params.equals("currency")) {
            return zone != null ? zone.getRewardCurrency() : "N/A";
        }
        
        // %afkzone_reward% - Base reward amount for the player's current AFK zone
        if (params.equals("reward")) {
            return worker != null ? String.valueOf(worker.getZone().getBaseReward()) : "0";
        }
        
        // %afkzone_enabled% - Shows if AFK zones are enabled globally
        if (params.equals("enabled")) {
            return plugin.getZoneManager().getAllZones().stream()
                .anyMatch(AfkZone::isEnabled) ? "true" : "false";
        }
        
        return null; // Placeholder was not found
    }
}