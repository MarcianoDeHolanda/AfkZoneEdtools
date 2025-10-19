package gz.devian.afkzoneedtools.managers;

import es.edwardbelt.edlib.iapi.entity.EdEntity;
import gz.devian.afkzoneedtools.AfkZoneEdtools;
import gz.devian.afkzoneedtools.models.AfkZone;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.joml.Matrix4f;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages block display entities for AFK zones using EdLib
 */
public class BlockManager {
    
    private final AfkZoneEdtools plugin;
    private final Map<String, EdEntity> blockDisplays = new ConcurrentHashMap<>();
    
    public BlockManager(AfkZoneEdtools plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Initialize block displays for all zones
     */
    public void initializeAllZones() {
        for (AfkZone zone : plugin.getZoneManager().getAllZones()) {
            if (zone.isEnabled()) {
                createBlockDisplay(zone);
            }
        }
    }
    
    /**
     * Create a block display for a zone
     */
    public void createBlockDisplay(AfkZone zone) {
        if (zone.getCenterLocation() == null) {
            plugin.getLogger().warning("Zone " + zone.getId() + " has no center location!");
            return;
        }
        
        Location location = zone.getCenterLocation().clone().add(0, 1, 0); // Slightly above center
        Material material = zone.getBlockMaterial();
        double scale = zone.getBlockScale();
        boolean glow = zone.isBlockGlow();
        String glowColor = zone.getGlowColor();
        
        plugin.getLogger().info("Creating block display for zone: " + zone.getId());
        plugin.getLogger().info("Location: " + location.toString());
        plugin.getLogger().info("Material: " + material.toString());
        plugin.getLogger().info("Scale: " + scale);
        
        plugin.getEdLibIntegration().createBlockDisplay(location, material, scale, glow, glowColor, blockDisplay -> {
            blockDisplays.put(zone.getId(), blockDisplay);
            zone.setBlockDisplayEntity(blockDisplay);
            
            plugin.getLogger().info("Successfully created block display for zone: " + zone.getId());
            plugin.getLogger().info("Block display entity: " + (blockDisplay != null ? "Created" : "NULL"));
        });
    }
    
    /**
     * Update block display transformation (for mining animation)
     */
    public void playMiningAnimation(AfkZone zone) {
        EdEntity blockDisplay = blockDisplays.get(zone.getId());
        if (blockDisplay == null) return;
        
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                // Shrink animation
                Matrix4f shrinkTransform = new Matrix4f()
                    .identity()
                    .scale(0.1f);
                
                plugin.getEdLibIntegration().updateBlockDisplayTransformation(
                    blockDisplay, 
                    shrinkTransform, 
                    10, // 10 ticks duration
                    0   // No delay
                );
                
                // Schedule regeneration
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    regenerateBlock(zone);
                }, zone.getRegenerationTime() / 50); // Convert ms to ticks
                
            } catch (Exception e) {
                plugin.getLogger().severe("Error playing mining animation: " + e.getMessage());
            }
        });
    }
    
    /**
     * Regenerate block display after mining
     */
    public void regenerateBlock(AfkZone zone) {
        EdEntity blockDisplay = blockDisplays.get(zone.getId());
        if (blockDisplay == null) return;
        
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                // Grow animation
                Matrix4f growTransform = new Matrix4f()
                    .identity()
                    .scale((float) zone.getBlockScale());
                
                plugin.getEdLibIntegration().updateBlockDisplayTransformation(
                    blockDisplay,
                    growTransform,
                    20, // 20 ticks duration (1 second)
                    0   // No delay
                );
                
                if (plugin.getConfigManager().isDebugEnabled()) {
                    plugin.getLogger().info("Regenerated block for zone: " + zone.getId());
                }
                
            } catch (Exception e) {
                plugin.getLogger().severe("Error regenerating block: " + e.getMessage());
            }
        });
    }
    
    /**
     * Remove block display for a zone
     */
    public void removeBlockDisplay(AfkZone zone) {
        EdEntity blockDisplay = blockDisplays.remove(zone.getId());
        if (blockDisplay != null) {
            plugin.getEdLibIntegration().removeEntity(blockDisplay);
        }
    }
    
    /**
     * Get block display for a zone
     */
    public EdEntity getBlockDisplay(String zoneId) {
        return blockDisplays.get(zoneId);
    }
    
    /**
     * Cleanup all block displays
     */
    public void cleanup() {
        for (Map.Entry<String, EdEntity> entry : blockDisplays.entrySet()) {
            try {
                plugin.getEdLibIntegration().removeEntity(entry.getValue());
            } catch (Exception e) {
                plugin.getLogger().warning("Error removing block display: " + e.getMessage());
            }
        }
        blockDisplays.clear();
    }
}
