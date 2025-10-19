package gz.devian.afkzoneedtools.models;

import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Represents an AFK worker entity that automatically mines blocks
 * Functions as the player's automated farming agent
 */
public class AfkWorker {
    
    private final UUID workerId;
    private final UUID playerId;
    private final String playerName;
    private final AfkZone zone;
    
    // Runtime data
    private transient Object entityInstance; // EdLib entity instance
    private transient Object interactionEntity; // EdLib interaction entity
    private transient long lastHarvestTime;
    private transient boolean active;
    private transient int harvestCount;
    
    // Session data
    private transient Object edToolsSession; // EdTools session instance
    private transient String playerToolId; // The actual OmniTool ID the player is using
    
    public AfkWorker(Player player, AfkZone zone) {
        this.workerId = UUID.randomUUID();
        this.playerId = player.getUniqueId();
        this.playerName = player.getName();
        this.zone = zone;
        this.lastHarvestTime = System.currentTimeMillis();
        this.active = true;
        this.harvestCount = 0;
    }
    
    // Getters and Setters
    
    public UUID getWorkerId() {
        return workerId;
    }
    
    public UUID getPlayerId() {
        return playerId;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public AfkZone getZone() {
        return zone;
    }
    
    public Object getEntityInstance() {
        return entityInstance;
    }
    
    public void setEntityInstance(Object entityInstance) {
        this.entityInstance = entityInstance;
    }
    
    public Object getInteractionEntity() {
        return interactionEntity;
    }
    
    public void setInteractionEntity(Object interactionEntity) {
        this.interactionEntity = interactionEntity;
    }
    
    public long getLastHarvestTime() {
        return lastHarvestTime;
    }
    
    public void setLastHarvestTime(long lastHarvestTime) {
        this.lastHarvestTime = lastHarvestTime;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public int getHarvestCount() {
        return harvestCount;
    }
    
    public void incrementHarvestCount() {
        this.harvestCount++;
    }
    
    public Object getEdToolsSession() {
        return edToolsSession;
    }
    
    public void setEdToolsSession(Object edToolsSession) {
        this.edToolsSession = edToolsSession;
    }
    
    public String getPlayerToolId() {
        return playerToolId;
    }
    
    public void setPlayerToolId(String playerToolId) {
        this.playerToolId = playerToolId;
    }
    
    /**
     * Check if enough time has passed for next harvest
     */
    public boolean canHarvest() {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastHarvest = currentTime - lastHarvestTime;
        return timeSinceLastHarvest >= zone.getHarvestInterval();
    }
    
    /**
     * Get time until next harvest in milliseconds
     */
    public long getTimeUntilNextHarvest() {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastHarvest = currentTime - lastHarvestTime;
        long timeUntilNext = zone.getHarvestInterval() - timeSinceLastHarvest;
        return Math.max(0, timeUntilNext);
    }
}
