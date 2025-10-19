package gz.devian.afkzoneedtools.models;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an AFK Zone that functions exactly like a normal EdTools zone
 * with automated block generation and mining
 */
public class AfkZone {
    
    private final String id;
    private String displayName;
    private ZoneType type;
    private Location minCorner;
    private Location maxCorner;
    private Location centerLocation;
    private Material blockMaterial;
    
    // Block Display Settings
    private int customModelData;
    private double blockScale;
    private boolean blockGlow;
    private String glowColor;
    
    // Tool Requirements
    private List<String> allowedTools;
    
    // Harvest Settings
    private long harvestInterval;
    private long regenerationTime;
    
    // Reward Settings
    private String rewardCurrency;
    private double baseReward;
    
    // Worker Settings
    private int maxWorkers;
    private EntityType workerType;
    
    // EdTools Integration Settings (CRITICAL)
    private boolean affectEnchants;
    private boolean affectSell;
    private boolean affectBlockCurrencies;
    private boolean affectLuckyBlocks;
    private boolean useGlobalSession;
    private boolean applyBoosters;
    private boolean grantExperience;
    
    // Effects
    private boolean miningParticles;
    private boolean breakParticles;
    private String successSound;
    private String particleType;
    
    // Status
    private boolean enabled;
    
    // Runtime data (not saved)
    private transient Object blockDisplayEntity;
    private transient List<AfkWorker> activeWorkers;
    
    public AfkZone(String id) {
        this.id = id;
        this.allowedTools = new ArrayList<>();
        this.activeWorkers = new ArrayList<>();
        
        // Default EdTools integration (ALL TRUE for full zone behavior)
        this.affectEnchants = true;
        this.affectSell = true;
        this.affectBlockCurrencies = true;
        this.affectLuckyBlocks = true;
        this.useGlobalSession = true;
        this.applyBoosters = true;
        this.grantExperience = true;
    }
    
    // Getters and Setters
    
    public String getId() {
        return id;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public ZoneType getType() {
        return type;
    }
    
    public void setType(ZoneType type) {
        this.type = type;
    }
    
    public Location getMinCorner() {
        return minCorner;
    }
    
    public void setMinCorner(Location minCorner) {
        this.minCorner = minCorner;
    }
    
    public Location getMaxCorner() {
        return maxCorner;
    }
    
    public void setMaxCorner(Location maxCorner) {
        this.maxCorner = maxCorner;
    }
    
    public Location getCenterLocation() {
        return centerLocation;
    }
    
    public void setCenterLocation(Location centerLocation) {
        this.centerLocation = centerLocation;
    }
    
    public Material getBlockMaterial() {
        return blockMaterial;
    }
    
    public void setBlockMaterial(Material blockMaterial) {
        this.blockMaterial = blockMaterial;
    }
    
    public int getCustomModelData() {
        return customModelData;
    }
    
    public void setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
    }
    
    public double getBlockScale() {
        return blockScale;
    }
    
    public void setBlockScale(double blockScale) {
        this.blockScale = blockScale;
    }
    
    public boolean isBlockGlow() {
        return blockGlow;
    }
    
    public void setBlockGlow(boolean blockGlow) {
        this.blockGlow = blockGlow;
    }
    
    public String getGlowColor() {
        return glowColor;
    }
    
    public void setGlowColor(String glowColor) {
        this.glowColor = glowColor;
    }
    
    public List<String> getAllowedTools() {
        return allowedTools;
    }
    
    public void setAllowedTools(List<String> allowedTools) {
        this.allowedTools = allowedTools;
    }
    
    public long getHarvestInterval() {
        return harvestInterval;
    }
    
    public void setHarvestInterval(long harvestInterval) {
        this.harvestInterval = harvestInterval;
    }
    
    public long getRegenerationTime() {
        return regenerationTime;
    }
    
    public void setRegenerationTime(long regenerationTime) {
        this.regenerationTime = regenerationTime;
    }
    
    public String getRewardCurrency() {
        return rewardCurrency;
    }
    
    public void setRewardCurrency(String rewardCurrency) {
        this.rewardCurrency = rewardCurrency;
    }
    
    public double getBaseReward() {
        return baseReward;
    }
    
    public void setBaseReward(double baseReward) {
        this.baseReward = baseReward;
    }
    
    public int getMaxWorkers() {
        return maxWorkers;
    }
    
    public void setMaxWorkers(int maxWorkers) {
        this.maxWorkers = maxWorkers;
    }
    
    public EntityType getWorkerType() {
        return workerType;
    }
    
    public void setWorkerType(EntityType workerType) {
        this.workerType = workerType;
    }
    
    // EdTools Integration Getters/Setters (CRITICAL)
    
    public boolean isAffectEnchants() {
        return affectEnchants;
    }
    
    public void setAffectEnchants(boolean affectEnchants) {
        this.affectEnchants = affectEnchants;
    }
    
    public boolean isAffectSell() {
        return affectSell;
    }
    
    public void setAffectSell(boolean affectSell) {
        this.affectSell = affectSell;
    }
    
    public boolean isAffectBlockCurrencies() {
        return affectBlockCurrencies;
    }
    
    public void setAffectBlockCurrencies(boolean affectBlockCurrencies) {
        this.affectBlockCurrencies = affectBlockCurrencies;
    }
    
    public boolean isAffectLuckyBlocks() {
        return affectLuckyBlocks;
    }
    
    public void setAffectLuckyBlocks(boolean affectLuckyBlocks) {
        this.affectLuckyBlocks = affectLuckyBlocks;
    }
    
    public boolean isUseGlobalSession() {
        return useGlobalSession;
    }
    
    public void setUseGlobalSession(boolean useGlobalSession) {
        this.useGlobalSession = useGlobalSession;
    }
    
    public boolean isApplyBoosters() {
        return applyBoosters;
    }
    
    public void setApplyBoosters(boolean applyBoosters) {
        this.applyBoosters = applyBoosters;
    }
    
    public boolean isGrantExperience() {
        return grantExperience;
    }
    
    public void setGrantExperience(boolean grantExperience) {
        this.grantExperience = grantExperience;
    }
    
    // Effects Getters/Setters
    
    public boolean isMiningParticles() {
        return miningParticles;
    }
    
    public void setMiningParticles(boolean miningParticles) {
        this.miningParticles = miningParticles;
    }
    
    public boolean isBreakParticles() {
        return breakParticles;
    }
    
    public void setBreakParticles(boolean breakParticles) {
        this.breakParticles = breakParticles;
    }
    
    public String getSuccessSound() {
        return successSound;
    }
    
    public void setSuccessSound(String successSound) {
        this.successSound = successSound;
    }
    
    public String getParticleType() {
        return particleType;
    }
    
    public void setParticleType(String particleType) {
        this.particleType = particleType;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    // Runtime Data
    
    public Object getBlockDisplayEntity() {
        return blockDisplayEntity;
    }
    
    public void setBlockDisplayEntity(Object blockDisplayEntity) {
        this.blockDisplayEntity = blockDisplayEntity;
    }
    
    public List<AfkWorker> getActiveWorkers() {
        return activeWorkers;
    }
    
    public void addWorker(AfkWorker worker) {
        activeWorkers.add(worker);
    }
    
    public void removeWorker(AfkWorker worker) {
        activeWorkers.remove(worker);
    }
    
    /**
     * Check if a tool is allowed in this zone
     */
    public boolean isToolAllowed(String toolId) {
        return allowedTools.isEmpty() || allowedTools.contains(toolId);
    }
    
    /**
     * Check if a location is within this zone's boundaries
     */
    public boolean containsLocation(Location location) {
        if (minCorner == null || maxCorner == null || location == null) {
            return false;
        }
        
        if (!location.getWorld().equals(minCorner.getWorld()) || 
            !location.getWorld().equals(maxCorner.getWorld())) {
            return false;
        }
        
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        
        return x >= Math.min(minCorner.getX(), maxCorner.getX()) &&
               x <= Math.max(minCorner.getX(), maxCorner.getX()) &&
               y >= Math.min(minCorner.getY(), maxCorner.getY()) &&
               y <= Math.max(minCorner.getY(), maxCorner.getY()) &&
               z >= Math.min(minCorner.getZ(), maxCorner.getZ()) &&
               z <= Math.max(minCorner.getZ(), maxCorner.getZ());
    }
    
    /**
     * Zone types
     */
    public enum ZoneType {
        MINING,
        FARMING,
        CUSTOM
    }
}
