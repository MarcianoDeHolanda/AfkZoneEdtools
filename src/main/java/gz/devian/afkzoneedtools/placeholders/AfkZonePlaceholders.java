package gz.devian.afkzoneedtools.placeholders;

import gz.devian.afkzoneedtools.AfkZoneEdtools;
import gz.devian.afkzoneedtools.models.AfkWorker;
import gz.devian.afkzoneedtools.models.AfkZone;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

/**
 * PlaceholderAPI integration for AfkZoneEdtools
 * Uses reflection to avoid direct dependency on PlaceholderAPI
 */
public class AfkZonePlaceholders {
    
    private final AfkZoneEdtools plugin;
    private Object expansion;
    
    public AfkZonePlaceholders(AfkZoneEdtools plugin) {
        this.plugin = plugin;
        initializePlaceholderAPI();
    }
    
    private void initializePlaceholderAPI() {
        try {
            plugin.getLogger().info("Initializing PlaceholderAPI expansion...");
            
            // Check if PlaceholderAPI is available
            Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            plugin.getLogger().info("PlaceholderAPI found!");
            
            // Create expansion using reflection
            Class<?> expansionClass = Class.forName("me.clip.placeholderapi.expansion.PlaceholderExpansion");
            plugin.getLogger().info("PlaceholderExpansion class found!");
            
            // Create a proper expansion instance
            expansion = java.lang.reflect.Proxy.newProxyInstance(
                expansionClass.getClassLoader(),
                new Class<?>[]{expansionClass},
                (proxy, method, args) -> {
                    String methodName = method.getName();
                    
                    if ("getIdentifier".equals(methodName)) {
                        return "afkzone";
                    } else if ("getAuthor".equals(methodName)) {
                        return "MarcianoDeHolanda";
                    } else if ("getVersion".equals(methodName)) {
                        return plugin.getDescription().getVersion();
                    } else if ("persist".equals(methodName)) {
                        return true;
                    } else if ("onPlaceholderRequest".equals(methodName)) {
                        Player player = (Player) args[0];
                        String params = (String) args[1];
                        return onPlaceholderRequest(player, params);
                    }
                    
                    return method.getDefaultValue();
                }
            );
            
            plugin.getLogger().info("PlaceholderAPI expansion created successfully!");
            
        } catch (ClassNotFoundException e) {
            plugin.getLogger().info("PlaceholderAPI not found - placeholders will not be available");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to initialize PlaceholderAPI expansion: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void register() {
        try {
            if (expansion != null) {
                plugin.getLogger().info("Registering PlaceholderAPI expansion...");
                
                // Get PlaceholderAPI class
                Class<?> placeholderAPI = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
                Method registerExpansion = placeholderAPI.getMethod("registerExpansion", expansion.getClass().getInterfaces()[0]);
                
                // Register the expansion
                boolean success = (Boolean) registerExpansion.invoke(null, expansion);
                
                if (success) {
                    plugin.getLogger().info("PlaceholderAPI expansion 'afkzone' registered successfully!");
                } else {
                    plugin.getLogger().warning("Failed to register PlaceholderAPI expansion");
                }
            } else {
                plugin.getLogger().warning("Expansion is null, cannot register");
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to register PlaceholderAPI expansion: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null) {
            return "";
        }
        
        // %afkzone_active% - Shows if player has active AFK worker
        if (params.equals("active")) {
            AfkWorker worker = plugin.getWorkerManager().getWorkerByPlayer(player);
            return worker != null ? "true" : "false";
        }
        
        // %afkzone_zone% - Shows current AFK zone name
        if (params.equals("zone")) {
            AfkWorker worker = plugin.getWorkerManager().getWorkerByPlayer(player);
            return worker != null ? worker.getZone().getDisplayName() : "None";
        }
        
        // %afkzone_zone_id% - Shows current AFK zone ID
        if (params.equals("zone_id")) {
            AfkWorker worker = plugin.getWorkerManager().getWorkerByPlayer(player);
            return worker != null ? worker.getZone().getId() : "None";
        }
        
        // %afkzone_harvests% - Shows total harvests by current worker
        if (params.equals("harvests")) {
            AfkWorker worker = plugin.getWorkerManager().getWorkerByPlayer(player);
            return worker != null ? String.valueOf(worker.getHarvestCount()) : "0";
        }
        
        // %afkzone_material% - Shows material being mined
        if (params.equals("material")) {
            AfkWorker worker = plugin.getWorkerManager().getWorkerByPlayer(player);
            return worker != null ? worker.getZone().getBlockMaterial().name().toLowerCase().replace("_", " ") : "None";
        }
        
        // %afkzone_time_left% - Shows time until next harvest (in seconds)
        if (params.equals("time_left")) {
            AfkWorker worker = plugin.getWorkerManager().getWorkerByPlayer(player);
            if (worker != null) {
                long timeLeft = (worker.getLastHarvestTime() + worker.getZone().getHarvestInterval()) - System.currentTimeMillis();
                return timeLeft > 0 ? String.valueOf(timeLeft / 1000) : "0";
            }
            return "0";
        }
        
        // %afkzone_workers_total% - Shows total active workers
        if (params.equals("workers_total")) {
            return String.valueOf(plugin.getWorkerManager().getAllWorkers().size());
        }
        
        // %afkzone_workers_zone% - Shows workers in current player's zone
        if (params.equals("workers_zone")) {
            AfkWorker worker = plugin.getWorkerManager().getWorkerByPlayer(player);
            if (worker != null) {
                return String.valueOf(plugin.getWorkerManager().getZoneWorkers(worker.getZone().getId()).size());
            }
            return "0";
        }
        
        // %afkzone_zones_total% - Shows total loaded zones
        if (params.equals("zones_total")) {
            return String.valueOf(plugin.getZoneManager().getAllZones().size());
        }
        
        // %afkzone_tool% - Shows current tool being used
        if (params.equals("tool")) {
            AfkWorker worker = plugin.getWorkerManager().getWorkerByPlayer(player);
            return worker != null && worker.getPlayerToolId() != null ? worker.getPlayerToolId() : "None";
        }
        
        // %afkzone_currency% - Shows currency type for current zone
        if (params.equals("currency")) {
            AfkWorker worker = plugin.getWorkerManager().getWorkerByPlayer(player);
            return worker != null ? worker.getZone().getRewardCurrency() : "None";
        }
        
        // %afkzone_reward% - Shows base reward amount
        if (params.equals("reward")) {
            AfkWorker worker = plugin.getWorkerManager().getWorkerByPlayer(player);
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