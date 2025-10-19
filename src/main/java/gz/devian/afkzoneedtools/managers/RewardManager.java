package gz.devian.afkzoneedtools.managers;

import gz.devian.afkzoneedtools.AfkZoneEdtools;
import gz.devian.afkzoneedtools.models.AfkZone;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages reward distribution for AFK farming
 * 
 * CRITICAL: Rewards are given through EdTools APIs to ensure:
 * - Currency rewards based on mined blocks (via mineBlockAsPlayer)
 * - Booster effects applying correctly
 * - Leveling system integration
 * - Sell system integration
 */
public class RewardManager {
    
    private final AfkZoneEdtools plugin;
    
    public RewardManager(AfkZoneEdtools plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Process rewards for a successful harvest
     * 
     * Note: Currency rewards are already handled by mineBlockAsPlayer() in EdToolsIntegrationManager
     * This method handles additional reward processing and effects
     */
    public void processHarvestReward(Player player, AfkZone zone) {
        if (!plugin.getConfigManager().isCurrencyRewardsEnabled()) {
            return;
        }
        
        UUID playerId = player.getUniqueId();
        
        // Get booster multiplier if enabled
        double boosterMultiplier = 1.0;
        if (plugin.getConfigManager().isBoosterEffectsEnabled() && zone.isApplyBoosters()) {
            boosterMultiplier = plugin.getEdToolsIntegration().getBoosterMultiplier(
                playerId, 
                zone.getRewardCurrency()
            );
            
            // Show booster effect
            if (boosterMultiplier > 1.0) {
                plugin.getEffectManager().playBoosterEffect(player, boosterMultiplier);
            }
        }
        
        // Calculate final reward (base reward already multiplied by boosters in mineBlockAsPlayer)
        double finalReward = zone.getBaseReward() * boosterMultiplier;
        
        // Show reward effect
        plugin.getEffectManager().playCurrencyRewardEffect(player, finalReward, zone.getRewardCurrency());
        
        // Grant leveling experience if enabled
        if (plugin.getConfigManager().isLevelingEnabled() && zone.isGrantExperience()) {
            grantLevelingExperience(playerId, zone);
        }
    }
    
    /**
     * Grant leveling experience for AFK farming
     */
    private void grantLevelingExperience(UUID playerId, AfkZone zone) {
        try {
            // Add experience based on zone type
            double experienceAmount = calculateExperienceAmount(zone);
            
            // Add to EdTools leveling system
            String levelType = getLevelTypeForZone(zone);
            plugin.getEdToolsIntegration().addLevel(playerId, levelType, experienceAmount);
            
            if (plugin.getConfigManager().isDebugEnabled()) {
                plugin.getLogger().info("Granted " + experienceAmount + " " + levelType + " experience");
            }
            
        } catch (Exception e) {
            plugin.getLogger().warning("Error granting leveling experience: " + e.getMessage());
        }
    }
    
    /**
     * Calculate experience amount based on zone
     */
    private double calculateExperienceAmount(AfkZone zone) {
        // Base experience on reward amount and zone type
        double baseExp = zone.getBaseReward() / 10.0; // 10% of reward as experience
        
        // Adjust based on zone type
        switch (zone.getType()) {
            case MINING:
                return baseExp * 1.2; // 20% bonus for mining
            case FARMING:
                return baseExp * 1.0; // Normal for farming
            case CUSTOM:
                return baseExp * 0.8; // 20% reduction for custom
            default:
                return baseExp;
        }
    }
    
    /**
     * Get level type ID for zone
     */
    private String getLevelTypeForZone(AfkZone zone) {
        switch (zone.getType()) {
            case MINING:
                return "mining-level";
            case FARMING:
                return "farming-level";
            default:
                return "farming-level";
        }
    }
    
    /**
     * Process enchantment activation reward
     */
    public void processEnchantActivation(Player player, String enchantName) {
        if (!plugin.getConfigManager().isEnchantActivationEnabled()) {
            return;
        }
        
        // Show enchant effect
        plugin.getEffectManager().playEnchantActivationEffect(player, enchantName);
        
        if (plugin.getConfigManager().isDebugEnabled()) {
            plugin.getLogger().info("Enchant activated for " + player.getName() + ": " + enchantName);
        }
    }
    
    /**
     * Process sell reward
     */
    public void processSellReward(Player player, String itemId, double amount) {
        if (!plugin.getConfigManager().isSellIntegrationEnabled()) {
            return;
        }
        
        // Sell through EdTools API
        plugin.getEdToolsIntegration().sellItem(player.getUniqueId(), itemId, amount);
        
        if (plugin.getConfigManager().isDebugEnabled()) {
            plugin.getLogger().info("Sold " + amount + "x " + itemId + " for " + player.getName());
        }
    }
    
    /**
     * Process lucky block reward
     */
    public void processLuckyBlockReward(Player player, AfkZone zone) {
        if (!plugin.getConfigManager().isLuckyBlocksEnabled()) {
            return;
        }
        
        // Show lucky block effect
        plugin.getEffectManager().playSuccessParticles(player, zone.getCenterLocation());
        
        if (plugin.getConfigManager().isDebugEnabled()) {
            plugin.getLogger().info("Lucky block activated for " + player.getName());
        }
    }
    
    /**
     * Get total earnings for a player
     */
    public double getTotalEarnings(UUID playerId, String currency) {
        return plugin.getEdToolsIntegration().getCurrency(playerId, currency);
    }
    
    /**
     * Get player's level
     */
    public double getPlayerLevel(UUID playerId, String levelType) {
        return plugin.getEdToolsIntegration().getLevel(playerId, levelType);
    }
}
