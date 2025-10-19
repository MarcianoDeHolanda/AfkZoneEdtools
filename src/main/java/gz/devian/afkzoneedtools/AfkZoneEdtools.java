package gz.devian.afkzoneedtools;

import gz.devian.afkzoneedtools.commands.AfkZoneCommand;
import gz.devian.afkzoneedtools.listeners.AfkZoneListener;
import gz.devian.afkzoneedtools.listeners.PlayerListener;
import gz.devian.afkzoneedtools.managers.*;
import gz.devian.afkzoneedtools.placeholders.AfkZonePlaceholderExpansion;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * AfkZoneEdtools - Advanced AFK zone addon for EdTools farming/mining ecosystem
 * 
 * This plugin creates automated farming zones that function EXACTLY like normal EdTools zones:
 * - Gives currency based on mined blocks
 * - Affected by EdTools boosters
 * - Activates tool enchantments as if farming manually
 * - Triggers selling mechanics
 * - Supports lucky block mechanics
 * - Contributes to EdTools leveling system
 * 
 * Uses EdLib's block display system for visual block representation and automated mining.
 * 
 * @author devian.gz
 * @version 1.0.0
 */
public class AfkZoneEdtools extends JavaPlugin {
    
    private static AfkZoneEdtools instance;
    
    // Core Managers
    private ConfigManager configManager;
    private ZoneManager zoneManager;
    private BlockManager blockManager;
    private WorkerManager workerManager;
    private EdToolsIntegrationManager edToolsIntegrationManager;
    private EdLibIntegrationManager edLibIntegrationManager;
    private RewardManager rewardManager;
    private EffectManager effectManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // ASCII Art Banner
        getLogger().info("========================================");
        getLogger().info("    ___  ________   ______");
        getLogger().info("   / _ |/ __/ / /  /_  __/__  ___  ___");
        getLogger().info("  / __ / _// //_/   / / / _ \\/ _ \\/ -_)");
        getLogger().info(" /_/ |_/_/ /___/   /_/  \\___/\\___/\\__/");
        getLogger().info("         EdTools Edition");
        getLogger().info("========================================");
        
        // Check dependencies
        if (!checkDependencies()) {
            getLogger().severe("Missing required dependencies! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Initialize managers
        getLogger().info("Initializing managers...");
        initializeManagers();
        
        // Register commands
        getLogger().info("Registering commands...");
        registerCommands();
        
        // Register listeners
        getLogger().info("Registering listeners...");
        registerListeners();
        
        // Register PlaceholderAPI if available
        getLogger().info("Checking for PlaceholderAPI...");
        registerPlaceholders();
        
        // Load zones
        getLogger().info("Loading AFK zones...");
        zoneManager.loadZones();
        
        // Initialize block displays for all zones
        getLogger().info("Initializing block displays...");
        blockManager.initializeAllZones();
        
        // Start worker management task
        getLogger().info("Starting worker management...");
        workerManager.startWorkerTask();
        
        // Start auto-save task
        startAutoSave();
        
        getLogger().info("AfkZoneEdtools v" + getDescription().getVersion() + " enabled successfully!");
        getLogger().info("Integrated with EdTools and EdLib for complete farming/mining automation!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("Disabling AfkZoneEdtools...");
        
        // Stop all workers
        if (workerManager != null) {
            getLogger().info("Stopping all AFK workers...");
            workerManager.shutdown();
        }
        
        // Save all zones
        if (zoneManager != null) {
            getLogger().info("Saving AFK zones...");
            zoneManager.saveZones();
        }
        
        // Cleanup block displays
        if (blockManager != null) {
            getLogger().info("Cleaning up block displays...");
            blockManager.cleanup();
        }
        
        getLogger().info("AfkZoneEdtools disabled successfully!");
    }
    
    /**
     * Check if required dependencies are present
     */
    private boolean checkDependencies() {
        boolean hasEdTools = getServer().getPluginManager().getPlugin("EdTools") != null;
        
        if (!hasEdTools) {
            getLogger().severe("EdTools plugin not found! This plugin requires EdTools to function.");
            return false;
        }
        
        getLogger().info("EdTools found! EdLib functionality is included in EdTools.");
        return true;
    }
    
    /**
     * Initialize all managers in proper order
     */
    private void initializeManagers() {
        try {
            // Configuration manager (first)
            configManager = new ConfigManager(this);
            configManager.loadConfigs();
            
            // Effect manager
            effectManager = new EffectManager(this);
            
            // EdTools integration manager
            edToolsIntegrationManager = new EdToolsIntegrationManager(this);
            if (!edToolsIntegrationManager.initialize()) {
                getLogger().severe("Failed to initialize EdTools integration!");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
            
            // EdLib integration manager
            edLibIntegrationManager = new EdLibIntegrationManager(this);
            if (!edLibIntegrationManager.initialize()) {
                getLogger().severe("Failed to initialize EdLib integration!");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
            
            // Zone manager
            zoneManager = new ZoneManager(this);
            
            // Block manager (requires EdLib)
            blockManager = new BlockManager(this);
            
            // Reward manager (requires EdTools)
            rewardManager = new RewardManager(this);
            
            // Worker manager (requires all previous managers)
            workerManager = new WorkerManager(this);
            
            getLogger().info("All managers initialized successfully!");
            
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to initialize managers!", e);
            getServer().getPluginManager().disablePlugin(this);
        }
    }
    
    /**
     * Register plugin commands
     */
    private void registerCommands() {
        getCommand("afkzone").setExecutor(new AfkZoneCommand(this));
    }
    
    /**
     * Register event listeners
     */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new AfkZoneListener(this), this);
    }
    
    /**
     * Register PlaceholderAPI expansion if available
     */
    private void registerPlaceholders() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            AfkZonePlaceholderExpansion expansion = new AfkZonePlaceholderExpansion(this);
            boolean success = expansion.register();
            if (success) {
                getLogger().info("PlaceholderAPI expansion registered successfully!");
            } else {
                getLogger().warning("Failed to register PlaceholderAPI expansion");
            }
        } else {
            getLogger().info("PlaceholderAPI not found - placeholders will not be available");
        }
    }
    
    /**
     * Start auto-save task
     */
    private void startAutoSave() {
        int interval = configManager.getAutoSaveInterval() * 60 * 20; // Convert minutes to ticks
        
        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            if (zoneManager != null) {
                zoneManager.saveZones();
                if (configManager.isDebugEnabled()) {
                    getLogger().info("Auto-saved AFK zones");
                }
            }
        }, interval, interval);
    }
    
    // Getters for managers
    
    public static AfkZoneEdtools getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public ZoneManager getZoneManager() {
        return zoneManager;
    }
    
    public BlockManager getBlockManager() {
        return blockManager;
    }
    
    public WorkerManager getWorkerManager() {
        return workerManager;
    }
    
    public EdToolsIntegrationManager getEdToolsIntegration() {
        return edToolsIntegrationManager;
    }
    
    public EdLibIntegrationManager getEdLibIntegration() {
        return edLibIntegrationManager;
    }
    
    public RewardManager getRewardManager() {
        return rewardManager;
    }
    
    public EffectManager getEffectManager() {
        return effectManager;
    }
}
