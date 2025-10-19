package gz.devian.afkzoneedtools.managers;

import es.edwardbelt.edlib.iapi.EdLibAPI;
import es.edwardbelt.edlib.iapi.entity.EdEntity;
import es.edwardbelt.edlib.iapi.EdColor;
import gz.devian.afkzoneedtools.AfkZoneEdtools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.joml.Matrix4f;

/**
 * Manages integration with EdLib API for entity and block display systems
 */
public class EdLibIntegrationManager {
    
    private final AfkZoneEdtools plugin;
    private EdLibAPI edLibAPI;
    
    public EdLibIntegrationManager(AfkZoneEdtools plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Initialize EdLib integration
     */
    public boolean initialize() {
        try {
            plugin.getLogger().info("Initializing EdLib integration...");
            
            edLibAPI = EdLibAPI.getInstance();
            if (edLibAPI == null) {
                plugin.getLogger().severe("Failed to get EdLibAPI instance!");
                return false;
            }
            
            plugin.getLogger().info("EdLib integration initialized successfully!");
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to initialize EdLib integration: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Create a block display entity asynchronously
     */
    public void createBlockDisplay(Location location, Material material, double scale, boolean glow, String glowColor, BlockDisplayCallback callback) {
        plugin.getLogger().info("EdLibIntegration: Starting block display creation...");
        plugin.getLogger().info("EdLibIntegration: Location: " + location);
        plugin.getLogger().info("EdLibIntegration: Material: " + material);
        plugin.getLogger().info("EdLibIntegration: Scale: " + scale);
        plugin.getLogger().info("EdLibIntegration: EdLibAPI instance: " + (edLibAPI != null ? "Available" : "NULL"));
        
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                plugin.getLogger().info("EdLibIntegration: Creating transformation matrix...");
                Matrix4f transformation = new Matrix4f()
                    .identity()
                    .scale((float) scale);
                
                plugin.getLogger().info("EdLibIntegration: Creating block display entity...");
                EdEntity blockDisplay = edLibAPI.createBlockDisplay(location, transformation, material);
                
                if (blockDisplay == null) {
                    plugin.getLogger().severe("EdLibIntegration: Block display entity is NULL!");
                    return;
                }
                
                plugin.getLogger().info("EdLibIntegration: Block display entity created successfully");
                
                if (glow && glowColor != null) {
                    try {
                        EdColor color = EdColor.valueOf(glowColor.toUpperCase());
                        blockDisplay.setGlowing(color);
                        plugin.getLogger().info("EdLibIntegration: Applied glow color: " + glowColor);
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid glow color: " + glowColor);
                    }
                }
                
                plugin.getLogger().info("EdLibIntegration: Spawning block display...");
                blockDisplay.spawn();
                
                // Also spawn for all online players to ensure visibility
                // Only spawn for players who are actually connected (not null connection)
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (onlinePlayer != null && onlinePlayer.isOnline()) {
                        try {
                            blockDisplay.spawnForPlayer(onlinePlayer);
                        } catch (Exception e) {
                            plugin.getLogger().warning("Failed to spawn block display for player " + onlinePlayer.getName() + ": " + e.getMessage());
                        }
                    }
                }
                
                plugin.getLogger().info("EdLibIntegration: Block display spawned successfully for all players");
                
                if (callback != null) {
                    callback.onCreated(blockDisplay);
                }
                
            } catch (Exception e) {
                plugin.getLogger().severe("Error creating block display: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Create an entity asynchronously
     */
    public void createEntity(EntityType type, Location location, EntityCallback callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                EdEntity entity = edLibAPI.createEntity(type, location);
                entity.spawn();
                
                if (callback != null) {
                    callback.onCreated(entity);
                }
                
            } catch (Exception e) {
                plugin.getLogger().severe("Error creating entity: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Create an interaction entity asynchronously
     */
    public void createInteractionEntity(Location location, float width, float height, EntityCallback callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                EdEntity entity = edLibAPI.createInteractionEntity(location, width, height);
                entity.spawn();
                
                if (callback != null) {
                    callback.onCreated(entity);
                }
                
            } catch (Exception e) {
                plugin.getLogger().severe("Error creating interaction entity: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Create a packet-based entity for a specific player
     */
    public void createEntityForPlayer(EntityType type, Location location, Player player, EntityCallback callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                EdEntity entity = edLibAPI.createEntity(type, location);
                entity.spawnForPlayer(player);
                
                if (callback != null) {
                    callback.onCreated(entity);
                }
                
            } catch (Exception e) {
                plugin.getLogger().severe("Error creating entity for player: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Send actionbar to player
     */
    public void sendActionbar(Player player, String message) {
        if (edLibAPI != null) {
            edLibAPI.sendActionbar(player, message);
        }
    }
    
    /**
     * Update block display transformation with interpolation
     */
    public void updateBlockDisplayTransformation(EdEntity entity, Matrix4f transformation, int duration, int delay) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                entity.setTransformationWithInterpolation(transformation, duration, delay);
            } catch (Exception e) {
                plugin.getLogger().severe("Error updating block display: " + e.getMessage());
            }
        });
    }
    
    /**
     * Remove entity asynchronously
     */
    public void removeEntity(EdEntity entity) {
        if (entity == null) return;
        
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                entity.remove();
            } catch (Exception e) {
                plugin.getLogger().severe("Error removing entity: " + e.getMessage());
            }
        });
    }
    
    public EdLibAPI getEdLibAPI() {
        return edLibAPI;
    }
    
    /**
     * Callback interface for block display creation
     */
    @FunctionalInterface
    public interface BlockDisplayCallback {
        void onCreated(EdEntity blockDisplay);
    }
    
    /**
     * Callback interface for entity creation
     */
    @FunctionalInterface
    public interface EntityCallback {
        void onCreated(EdEntity entity);
    }
}
