package gz.devian.afkzoneedtools.managers;

import es.edwardbelt.edgens.iapi.EdToolsAPI;
import es.edwardbelt.edgens.iapi.EdToolsZonesAPI;
import es.edwardbelt.edgens.iapi.EdToolsCurrencyAPI;
import es.edwardbelt.edgens.iapi.EdToolsEnchantAPI;
import es.edwardbelt.edgens.iapi.EdToolsBoostersAPI;
import es.edwardbelt.edgens.iapi.EdToolsOmniToolAPI;
import es.edwardbelt.edgens.iapi.EdToolsSellAPI;
import es.edwardbelt.edgens.iapi.EdToolsLevelingAPI;
import es.edwardbelt.edgens.iapi.EdToolsBackpackAPI;
import es.edwardbelt.edgens.iapi.APIPair;
import gz.devian.afkzoneedtools.AfkZoneEdtools;
import gz.devian.afkzoneedtools.models.AfkZone;
import gz.devian.afkzoneedtools.models.AfkWorker;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;

/**
 * Manages integration with EdTools APIs for complete normal zone behavior
 * 
 * CRITICAL: This manager ensures AFK zones function EXACTLY like normal EdTools zones:
 * - Currency rewards based on mined blocks
 * - Booster effects applying to AFK farming
 * - Tool enchantments activating as if farming manually
 * - Selling mechanics for mined items
 * - Lucky block mechanics
 * - Leveling system integration
 */
public class EdToolsIntegrationManager {
    
    private final AfkZoneEdtools plugin;
    
    // EdTools API instances
    private EdToolsAPI edToolsAPI;
    private EdToolsZonesAPI zonesAPI;
    private EdToolsCurrencyAPI currencyAPI;
    private EdToolsEnchantAPI enchantAPI;
    private EdToolsBoostersAPI boostersAPI;
    private EdToolsOmniToolAPI omniToolAPI;
    private EdToolsSellAPI sellAPI;
    private EdToolsLevelingAPI levelingAPI;
    private EdToolsBackpackAPI backpackAPI;
    
    public EdToolsIntegrationManager(AfkZoneEdtools plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Initialize EdTools integration
     */
    public boolean initialize() {
        try {
            plugin.getLogger().info("Initializing EdTools integration...");
            
            // Try to get EdToolsAPI using reflection to handle class loading gracefully
            Class<?> edToolsAPIClass = Class.forName("es.edwardbelt.edgens.iapi.EdToolsAPI");
            Object edToolsAPIInstance = edToolsAPIClass.getMethod("getInstance").invoke(null);
            
            if (edToolsAPIInstance == null) {
                plugin.getLogger().severe("Failed to get EdToolsAPI instance!");
                return false;
            }
            
            // Cast to the actual type
            edToolsAPI = (EdToolsAPI) edToolsAPIInstance;
            
            // Get all sub-APIs
            zonesAPI = edToolsAPI.getZonesAPI();
            currencyAPI = edToolsAPI.getCurrencyAPI();
            enchantAPI = edToolsAPI.getEnchantAPI();
            boostersAPI = edToolsAPI.getBoostersAPI();
            omniToolAPI = edToolsAPI.getOmniToolAPI();
            sellAPI = edToolsAPI.getSellAPI();
            // levelingAPI = edToolsAPI.getLevelingAPI(); // Method not available in current API version
            backpackAPI = edToolsAPI.getBackpackAPI();
            
            plugin.getLogger().info("EdTools integration initialized successfully!");
            return true;
            
        } catch (ClassNotFoundException e) {
            plugin.getLogger().severe("EdTools API classes not found! Make sure EdTools is properly installed.");
            return false;
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to initialize EdTools integration: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * CRITICAL: Mine block as player with FULL EdTools integration
     * This is the core method that makes AFK zones function exactly like normal zones
     * 
     * @param worker The AFK worker performing the mining
     * @param player The player owning the worker
     * @param zone The AFK zone
     */
    public void mineBlockAsAfkWorker(AfkWorker worker, Player player, AfkZone zone) {
        if (zonesAPI == null) {
            plugin.getLogger().warning("EdToolsIntegration: zonesAPI is null!");
            return;
        }
        
        // Verify player is in a valid session
        if (!zonesAPI.isPlayerInSession(player)) {
            plugin.getLogger().warning("EdToolsIntegration: Player " + player.getName() + " is not in a valid zone session!");
            return;
        }
        
        String playerZoneId = zonesAPI.getPlayerZoneId(player);
        plugin.getLogger().info("EdToolsIntegration: Player " + player.getName() + " is in zone: " + playerZoneId);
        
        // Verify the player is actually in the correct zone
        if (playerZoneId == null || !playerZoneId.equals(zone.getId())) {
            plugin.getLogger().warning("EdToolsIntegration: Player " + player.getName() + " is in zone '" + playerZoneId + "' but expected '" + zone.getId() + "'");
            plugin.getLogger().warning("EdToolsIntegration: This may cause mineBlockAsPlayer to return NULL");
        }
        
        // Get player's actual OmniTool
        String toolId = worker.getPlayerToolId();
        if (toolId == null) {
            ItemStack tool = omniToolAPI.getOmniToolFromPlayer(player);
            if (tool != null) {
                toolId = omniToolAPI.getOmniToolId(tool);
                worker.setPlayerToolId(toolId);
                plugin.getLogger().info("EdToolsIntegration: Found OmniTool for " + player.getName() + ": " + toolId);
            } else {
                plugin.getLogger().warning("EdToolsIntegration: No OmniTool found in hand for " + player.getName());
                // Try to get any OmniTool from inventory
                for (ItemStack item : player.getInventory().getContents()) {
                    if (item != null && omniToolAPI.isItemOmniTool(item)) {
                        toolId = omniToolAPI.getOmniToolId(item);
                        worker.setPlayerToolId(toolId);
                        plugin.getLogger().info("EdToolsIntegration: Found OmniTool in inventory for " + player.getName() + ": " + toolId);
                        break;
                    }
                }
            }
        }
        
        if (toolId == null) {
            plugin.getLogger().warning("Player " + player.getName() + " has no valid OmniTool!");
            return;
        }
        
        // Block position - use the actual block position from EdTools
        // Get the first loaded block position from EdTools
        Vector blockPosition = null;
        try {
            Map<Vector, Material> loadedBlocks = zonesAPI.getPlayersLoadedBlocks(player);
            if (!loadedBlocks.isEmpty()) {
                blockPosition = loadedBlocks.keySet().iterator().next();
                plugin.getLogger().info("EdToolsIntegration: Found block position: " + blockPosition);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("EdToolsIntegration: Could not get block position: " + e.getMessage());
        }
        
        // Fallback to zone center if no block position found
        if (blockPosition == null) {
            blockPosition = zone.getCenterLocation().toVector();
            plugin.getLogger().info("EdToolsIntegration: Using zone center as fallback: " + blockPosition);
        }
        
        final Vector position = blockPosition;
        final String finalToolId = toolId;
        
        // CRITICAL: Mine block with ALL EdTools flags enabled for complete integration
        plugin.getLogger().info("EdToolsIntegration: Starting mineBlockAsPlayer for " + player.getName());
        plugin.getLogger().info("EdToolsIntegration: Position: " + position);
        plugin.getLogger().info("EdToolsIntegration: Tool ID: " + finalToolId);
        plugin.getLogger().info("EdToolsIntegration: Flags - Enchants: false (fixed per EdTools docs)" + 
            ", Sell: " + zone.isAffectSell() + ", Currencies: " + zone.isAffectBlockCurrencies() + 
            ", LuckyBlocks: " + zone.isAffectLuckyBlocks());
        
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                plugin.getLogger().info("EdToolsIntegration: Calling mineBlockAsPlayer...");
                plugin.getLogger().info("EdToolsIntegration: Player location: " + player.getLocation());
                plugin.getLogger().info("EdToolsIntegration: Mining position: " + position);
                plugin.getLogger().info("EdToolsIntegration: Player zone ID: " + zonesAPI.getPlayerZoneId(player));
                plugin.getLogger().info("EdToolsIntegration: Player session type: " + zonesAPI.getPlayerZoneSessionType(player));
                
                // Check if player is in the correct zone
                String expectedZoneId = zone.getId();
                String actualZoneId = zonesAPI.getPlayerZoneId(player);
                plugin.getLogger().info("EdToolsIntegration: Expected zone: " + expectedZoneId + ", Actual zone: " + actualZoneId);
                
                // Check player's loaded blocks
                try {
                    Map<Vector, Material> loadedBlocks = zonesAPI.getPlayersLoadedBlocks(player);
                    plugin.getLogger().info("EdToolsIntegration: Player loaded blocks count: " + loadedBlocks.size());
                    if (!loadedBlocks.isEmpty()) {
                        plugin.getLogger().info("EdToolsIntegration: Sample loaded block: " + loadedBlocks.entrySet().iterator().next());
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("EdToolsIntegration: Could not get loaded blocks: " + e.getMessage());
                }
                
                // IMPORTANT: According to EdTools documentation, affectEnchants should be false by default
                // and the method should be run asynchronously (which we already do)
                APIPair<Material, String> result = zonesAPI.mineBlockAsPlayer(
                    player,
                    position,
                    finalToolId,
                    false,                          // affectEnchants - should be false by default per EdTools docs
                    zone.isAffectSell(),            // Trigger selling mechanics
                    zone.isAffectBlockCurrencies(), // Give currency rewards
                    zone.isAffectLuckyBlocks()      // Trigger lucky block mechanics
                );
                
                plugin.getLogger().info("EdToolsIntegration: mineBlockAsPlayer result: " + (result != null ? "SUCCESS" : "NULL"));
                
                if (result != null) {
                    worker.incrementHarvestCount();
                    
                    plugin.getLogger().info("Worker " + worker.getWorkerId() + 
                        " mined block for " + player.getName() + 
                        " (Material: " + result.getValue0() + 
                        ", Sold: " + result.getValue1() + ")");
                } else {
                    plugin.getLogger().warning("mineBlockAsPlayer returned NULL for " + player.getName());
                }
                
            } catch (Exception e) {
                plugin.getLogger().severe("Error mining block as AFK worker: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Join player to AFK zone session
     * IMPORTANT: This assumes the zone already exists in EdTools
     */
    public void joinZoneSession(Player player, AfkZone zone) {
        if (zonesAPI == null) {
            plugin.getLogger().warning("EdToolsIntegration: zonesAPI is null in joinZoneSession!");
            return;
        }
        
        try {
            plugin.getLogger().info("EdToolsIntegration: Attempting to join player " + player.getName() + " to existing EdTools zone: " + zone.getId());
            
            // First, verify that the zone exists in EdTools by checking if we can get player blocks
            try {
                Map<Vector, Material> loadedBlocks = zonesAPI.getPlayersLoadedBlocks(player);
                plugin.getLogger().info("EdToolsIntegration: Player loaded blocks: " + loadedBlocks.size());
            } catch (Exception e) {
                plugin.getLogger().warning("EdToolsIntegration: Could not get loaded blocks - zone may not exist in EdTools");
            }
            
            // Try to join the session - this will fail if the zone doesn't exist in EdTools
            if (zone.isUseGlobalSession()) {
                zonesAPI.joinGlobalSession(player, zone.getId());
                plugin.getLogger().info("EdToolsIntegration: Joined global session for " + player.getName());
            } else {
                zonesAPI.joinAloneSession(player, zone.getId());
                plugin.getLogger().info("EdToolsIntegration: Joined alone session for " + player.getName());
            }
            
            // Set block type for zone
            String blockType = zone.getBlockMaterial().name().toLowerCase();
            zonesAPI.setPlayerBlocksTypeZone(player, zone.getId(), blockType);
            plugin.getLogger().info("EdToolsIntegration: Set block type to " + blockType + " for " + player.getName());
            
            // Verify the session was created
            boolean inSession = zonesAPI.isPlayerInSession(player);
            String playerZoneId = zonesAPI.getPlayerZoneId(player);
            plugin.getLogger().info("EdToolsIntegration: Player " + player.getName() + " in session: " + inSession + ", zone: " + playerZoneId);
            
            if (!inSession) {
                plugin.getLogger().warning("EdToolsIntegration: Session creation failed! Zone '" + zone.getId() + "' may not exist in EdTools.");
                plugin.getLogger().warning("EdToolsIntegration: Please create the zone in EdTools first before using this plugin.");
                return;
            }
            
            plugin.getLogger().info("Player " + player.getName() + " joined AFK zone session: " + zone.getId());
            
        } catch (Exception e) {
            plugin.getLogger().severe("Error joining zone session: " + e.getMessage());
            plugin.getLogger().severe("This usually means the zone '" + zone.getId() + "' doesn't exist in EdTools.");
            plugin.getLogger().severe("Please create the zone in EdTools first, then try again.");
            e.printStackTrace();
        }
    }
    
    /**
     * Leave player from zone session
     */
    public void leaveZoneSession(Player player) {
        if (zonesAPI == null) return;
        
        try {
            if (zonesAPI.isPlayerInSession(player)) {
                zonesAPI.leaveSession(player);
                
                if (plugin.getConfigManager().isDebugEnabled()) {
                    plugin.getLogger().info("Player " + player.getName() + " left AFK zone session");
                }
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Error leaving zone session: " + e.getMessage());
        }
    }
    
    /**
     * Get booster multiplier for player's currency
     */
    public double getBoosterMultiplier(UUID playerId, String currency) {
        if (boostersAPI == null) return 1.0;
        
        try {
            return boostersAPI.getBoosterValueByEconomy(playerId, currency);
        } catch (Exception e) {
            return 1.0;
        }
    }
    
    /**
     * Get enchant booster multiplier
     */
    public double getEnchantBoosterMultiplier(UUID playerId) {
        if (boostersAPI == null) return 1.0;
        
        try {
            return boostersAPI.getBoosterValueGlobalEnchants(playerId);
        } catch (Exception e) {
            return 1.0;
        }
    }
    
    /**
     * Add currency to player (with booster support)
     */
    public void addCurrency(UUID playerId, String currency, double amount, boolean affectBoosters) {
        if (currencyAPI == null) return;
        
        try {
            currencyAPI.addCurrency(playerId, currency, amount, affectBoosters);
        } catch (Exception e) {
            plugin.getLogger().severe("Error adding currency: " + e.getMessage());
        }
    }
    
    /**
     * Get player's currency amount
     */
    public double getCurrency(UUID playerId, String currency) {
        if (currencyAPI == null) return 0;
        
        try {
            return currencyAPI.getCurrency(playerId, currency);
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Get player's enchant level
     */
    public double getEnchantLevel(UUID playerId, String enchant) {
        if (enchantAPI == null) return 0;
        
        try {
            return enchantAPI.getEnchantLevel(playerId, enchant);
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Add enchant level to player
     */
    public void addEnchantLevel(UUID playerId, String enchant, double level) {
        if (enchantAPI == null) return;
        
        try {
            enchantAPI.addEnchantLevel(playerId, enchant, level);
        } catch (Exception e) {
            plugin.getLogger().severe("Error adding enchant level: " + e.getMessage());
        }
    }
    
    /**
     * Check if item is an OmniTool
     */
    public boolean isOmniTool(ItemStack item) {
        if (omniToolAPI == null) return false;
        
        try {
            return omniToolAPI.isItemOmniTool(item);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if item is a specific OmniTool type
     */
    public boolean isOmniTool(ItemStack item, String toolId) {
        if (omniToolAPI == null || item == null || toolId == null) {
            return false;
        }
        
        try {
            if (!omniToolAPI.isItemOmniTool(item)) {
                return false;
            }
            
            String itemToolId = omniToolAPI.getOmniToolId(item);
            return toolId.equals(itemToolId);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get OmniTool ID from item
     */
    public String getOmniToolId(ItemStack item) {
        if (omniToolAPI == null) return null;
        
        try {
            return omniToolAPI.getOmniToolId(item);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Get player's OmniTool from hand
     */
    public ItemStack getPlayerOmniTool(Player player) {
        if (omniToolAPI == null) return null;
        
        try {
            return omniToolAPI.getOmniToolFromPlayer(player);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Sell item for player
     */
    public void sellItem(UUID playerId, String itemId, double amount) {
        if (sellAPI == null) return;
        
        try {
            sellAPI.sellItem(playerId, itemId, amount);
        } catch (Exception e) {
            plugin.getLogger().severe("Error selling item: " + e.getMessage());
        }
    }
    
    /**
     * Add to sell summary
     */
    public void addSellSummary(UUID playerId, String currencyId, double amount) {
        if (sellAPI == null) return;
        
        try {
            sellAPI.addSellSummary(playerId, currencyId, amount);
        } catch (Exception e) {
            plugin.getLogger().severe("Error adding sell summary: " + e.getMessage());
        }
    }
    
    /**
     * Add level to player
     */
    public void addLevel(UUID playerId, String levelId, double level) {
        if (levelingAPI == null) return;
        
        try {
            levelingAPI.addLevel(playerId, levelId, level);
        } catch (Exception e) {
            plugin.getLogger().severe("Error adding level: " + e.getMessage());
        }
    }
    
    /**
     * Get player's level
     */
    public double getLevel(UUID playerId, String levelId) {
        if (levelingAPI == null) return 0;
        
        try {
            return levelingAPI.getLevel(playerId, levelId);
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Get backpack items
     */
    public Map<String, Double> getBackpackItems(UUID playerId) {
        if (backpackAPI == null) return null;
        
        try {
            return backpackAPI.getBackpackItems(playerId);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Sell backpack items
     */
    public void sellBackpackItems(Player player) {
        if (backpackAPI == null) return;
        
        try {
            backpackAPI.sellBackpackItems(player);
        } catch (Exception e) {
            plugin.getLogger().severe("Error selling backpack items: " + e.getMessage());
        }
    }
    
    // API Getters
    
    public EdToolsZonesAPI getZonesAPI() {
        return zonesAPI;
    }
    
    public EdToolsCurrencyAPI getCurrencyAPI() {
        return currencyAPI;
    }
    
    public EdToolsEnchantAPI getEnchantAPI() {
        return enchantAPI;
    }
    
    public EdToolsBoostersAPI getBoostersAPI() {
        return boostersAPI;
    }
    
    public EdToolsOmniToolAPI getOmniToolAPI() {
        return omniToolAPI;
    }
    
    public EdToolsSellAPI getSellAPI() {
        return sellAPI;
    }
    
    public EdToolsLevelingAPI getLevelingAPI() {
        return levelingAPI;
    }
    
    public EdToolsBackpackAPI getBackpackAPI() {
        return backpackAPI;
    }
}
