package gz.devian.afkzoneedtools.managers;

import gz.devian.afkzoneedtools.AfkZoneEdtools;
import gz.devian.afkzoneedtools.models.AfkZone;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages visual and sound effects for AFK zones
 */
public class EffectManager {
    
    private final AfkZoneEdtools plugin;
    
    public EffectManager(AfkZoneEdtools plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Play mining effects for a zone
     */
    public void playMiningEffects(Player player, AfkZone zone) {
        if (!plugin.getConfigManager().isParticleEffectsEnabled() &&
            !plugin.getConfigManager().isSoundEffectsEnabled()) {
            return;
        }
        
        Location location = zone.getCenterLocation();
        if (location == null) return;
        
        // Play particles
        if (plugin.getConfigManager().isParticleEffectsEnabled() && zone.isMiningParticles()) {
            playMiningParticles(player, location, zone);
        }
        
        // Play sound
        if (plugin.getConfigManager().isSoundEffectsEnabled() && zone.getSuccessSound() != null) {
            playSound(player, location, zone.getSuccessSound());
        }
    }
    
    /**
     * Play mining particles
     */
    private void playMiningParticles(Player player, Location location, AfkZone zone) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                Particle particleType = Particle.CRIT; // Use CRIT as default for compatibility
                
                // Try to get particle type from config
                try {
                    if (zone.getParticleType() != null) {
                        particleType = Particle.valueOf(zone.getParticleType().toUpperCase());
                    }
                } catch (IllegalArgumentException e) {
                    // Use default CRIT
                    particleType = Particle.CRIT;
                }
                
                // Spawn particles at block location
                Location particleLoc = location.clone().add(0, 1, 0);
                // Use CRIT particle instead of BLOCK for compatibility
                player.spawnParticle(Particle.CRIT, particleLoc, 20, 0.3, 0.3, 0.3, 0.1);
                
            } catch (Exception e) {
                plugin.getLogger().warning("Error playing particles: " + e.getMessage());
            }
        });
    }
    
    /**
     * Play break particles
     */
    public void playBreakParticles(Player player, Location location, AfkZone zone) {
        if (!plugin.getConfigManager().isParticleEffectsEnabled()) return;
        if (!zone.isBreakParticles()) return;
        
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                Location particleLoc = location.clone().add(0, 1, 0);
                player.spawnParticle(Particle.CRIT, particleLoc, 30, 0.4, 0.4, 0.4, 0.1);
                
            } catch (Exception e) {
                plugin.getLogger().warning("Error playing break particles: " + e.getMessage());
            }
        });
    }
    
    /**
     * Play success particles
     */
    public void playSuccessParticles(Player player, Location location) {
        if (!plugin.getConfigManager().isParticleEffectsEnabled()) return;
        
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                player.spawnParticle(Particle.HAPPY_VILLAGER, location.clone().add(0, 1.5, 0), 
                    10, 0.5, 0.5, 0.5, 0.05);
                
            } catch (Exception e) {
                plugin.getLogger().warning("Error playing success particles: " + e.getMessage());
            }
        });
    }
    
    /**
     * Play worker particles
     */
    public void playWorkerParticles(Player player, Location location) {
        if (!plugin.getConfigManager().isParticleEffectsEnabled()) return;
        if (!plugin.getConfigManager().isWorkerParticlesEnabled()) return;
        
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                Particle particleType = Particle.HAPPY_VILLAGER;
                
                try {
                    String configType = plugin.getConfigManager().getWorkerParticleType();
                    if (configType != null) {
                        particleType = Particle.valueOf(configType.toUpperCase());
                    }
                } catch (IllegalArgumentException e) {
                    // Use default
                }
                
                player.spawnParticle(particleType, location, 3, 0.2, 0.5, 0.2, 0.01);
                
            } catch (Exception e) {
                plugin.getLogger().warning("Error playing worker particles: " + e.getMessage());
            }
        });
    }
    
    /**
     * Play sound
     */
    private void playSound(Player player, Location location, String soundName) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                // Use reflection to handle sound compatibility
                Class<?> soundClass = Class.forName("org.bukkit.Sound");
                Object sound = soundClass.getMethod("valueOf", String.class).invoke(null, soundName.toUpperCase());
                player.getClass().getMethod("playSound", Location.class, soundClass, float.class, float.class)
                    .invoke(player, location, sound, 1.0f, 1.0f);
                
            } catch (Exception e) {
                // Fallback to a safe sound
                try {
                    player.playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                } catch (Exception ex) {
                    plugin.getLogger().warning("Error playing sound: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Play currency reward effect
     */
    public void playCurrencyRewardEffect(Player player, double amount, String currency) {
        if (!plugin.getConfigManager().isParticleEffectsEnabled()) return;
        
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                Location loc = player.getLocation().add(0, 2, 0);
                player.spawnParticle(Particle.HEART, loc, 15, 0.3, 0.3, 0.3, 0.1);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 2.0f);
                
                // Send message
                Map<String, String> replacements = new HashMap<>();
                replacements.put("amount", String.valueOf((int) amount));
                replacements.put("currency", currency);
                
                String message = plugin.getConfigManager().getMessage("currency_earned", replacements);
                plugin.getEdLibIntegration().sendActionbar(player, message);
                
            } catch (Exception e) {
                plugin.getLogger().warning("Error playing currency reward effect: " + e.getMessage());
            }
        });
    }
    
    /**
     * Play enchant activation effect
     */
    public void playEnchantActivationEffect(Player player, String enchantName) {
        if (!plugin.getConfigManager().isParticleEffectsEnabled()) return;
        
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                Location loc = player.getLocation().add(0, 1, 0);
                player.spawnParticle(Particle.ENCHANT, loc, 30, 0.5, 0.5, 0.5, 1.0);
                player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.7f, 1.5f);
                
                // Send message
                Map<String, String> replacements = new HashMap<>();
                replacements.put("enchant", enchantName);
                
                String message = plugin.getConfigManager().getMessage("enchant_activated", replacements);
                plugin.getEdLibIntegration().sendActionbar(player, message);
                
            } catch (Exception e) {
                plugin.getLogger().warning("Error playing enchant effect: " + e.getMessage());
            }
        });
    }
    
    /**
     * Play booster active effect
     */
    public void playBoosterEffect(Player player, double multiplier) {
        if (!plugin.getConfigManager().isParticleEffectsEnabled()) return;
        
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                Location loc = player.getLocation().add(0, 1.5, 0);
                player.spawnParticle(Particle.FLAME, loc, 20, 0.3, 0.3, 0.3, 0.05);
                
                // Send message
                Map<String, String> replacements = new HashMap<>();
                replacements.put("multiplier", String.format("%.1f", multiplier));
                
                String message = plugin.getConfigManager().getMessage("booster_active", replacements);
                plugin.getEdLibIntegration().sendActionbar(player, message);
                
            } catch (Exception e) {
                plugin.getLogger().warning("Error playing booster effect: " + e.getMessage());
            }
        });
    }
}
