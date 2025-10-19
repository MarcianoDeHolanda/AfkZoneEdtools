package gz.devian.afkzoneedtools.managers;

import es.edwardbelt.edlib.iapi.entity.EdEntity;
import es.edwardbelt.edlib.iapi.entity.goal.impl.EdGoalDelay;
import es.edwardbelt.edlib.iapi.entity.goal.impl.EdGoalMove;
import gz.devian.afkzoneedtools.AfkZoneEdtools;
import gz.devian.afkzoneedtools.models.AfkWorker;
import gz.devian.afkzoneedtools.models.AfkZone;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages AFK workers that automatically mine blocks
 * 
 * CRITICAL: Workers use EdLib's goal system for movement and automation
 * They call mineBlockAsPlayer() with ALL EdTools flags enabled for complete integration
 */
public class WorkerManager {
    
    private final AfkZoneEdtools plugin;
    private final Map<UUID, AfkWorker> workers = new ConcurrentHashMap<>();
    private final Map<String, List<UUID>> zoneWorkers = new ConcurrentHashMap<>();
    
    private int workerTaskId = -1;
    
    public WorkerManager(AfkZoneEdtools plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Start worker management task
     */
    public void startWorkerTask() {
        int updateInterval = plugin.getConfigManager().getWorkerUpdateInterval();
        
        workerTaskId = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (AfkWorker worker : workers.values()) {
                if (worker.isActive()) {
                    processWorker(worker);
                }
            }
        }, 20L, updateInterval).getTaskId();
        
        plugin.getLogger().info("Started worker management task");
    }
    
    /**
     * Create and spawn a worker for a player in a zone
     */
    public AfkWorker createWorker(Player player, AfkZone zone) {
        // Check if player already has a worker in this zone
        UUID existingWorkerId = getPlayerWorkerInZone(player.getUniqueId(), zone.getId());
        if (existingWorkerId != null) {
            return workers.get(existingWorkerId);
        }
        
        // Check max workers
        List<UUID> zoneWorkerList = zoneWorkers.computeIfAbsent(zone.getId(), k -> new ArrayList<>());
        if (zoneWorkerList.size() >= zone.getMaxWorkers()) {
            return null;
        }
        
        // Create worker
        AfkWorker worker = new AfkWorker(player, zone);
        
        // Join EdTools session (it will handle if already in session)
        plugin.getEdToolsIntegration().joinZoneSession(player, zone);
        
        // Get player's OmniTool
        String toolId = plugin.getEdToolsIntegration().getOmniToolId(
            plugin.getEdToolsIntegration().getPlayerOmniTool(player)
        );
        worker.setPlayerToolId(toolId);
        
        // Spawn worker entity
        Location workerLocation = zone.getCenterLocation().clone();
        plugin.getEdLibIntegration().createEntity(zone.getWorkerType(), workerLocation, entity -> {
            worker.setEntityInstance(entity);
            
            // Setup worker appearance
            entity.setDisplayName("§6" + player.getName() + "'s Worker");
            entity.setInvisible(); // Make invisible for cleaner look
            
            // Add to tracking
            workers.put(worker.getWorkerId(), worker);
            zoneWorkerList.add(worker.getWorkerId());
            zone.addWorker(worker);
            
            if (plugin.getConfigManager().isDebugEnabled()) {
                plugin.getLogger().info("Created worker " + worker.getWorkerId() + " for " + player.getName());
            }
        });
        
        return worker;
    }
    
    /**
     * Process worker actions
     */
    private void processWorker(AfkWorker worker) {
        if (!worker.canHarvest()) {
            return;
        }
        
        Player player = Bukkit.getPlayer(worker.getPlayerId());
        if (player == null || !player.isOnline()) {
            removeWorker(worker.getWorkerId());
            return;
        }
        
        AfkZone zone = worker.getZone();
        if (zone == null || !zone.isEnabled()) {
            return;
        }
        
        plugin.getLogger().info("Worker " + worker.getWorkerId() + " processing harvest for " + player.getName());
        plugin.getLogger().info("Zone: " + zone.getId() + ", Location: " + zone.getCenterLocation());
        
        // CRITICAL: Mine block using EdTools integration with ALL flags enabled
        plugin.getEdToolsIntegration().mineBlockAsAfkWorker(worker, player, zone);
        
        // Play mining animation
        plugin.getBlockManager().playMiningAnimation(zone);
        
        // Play effects
        plugin.getEffectManager().playMiningEffects(player, zone);
        
        // Update last harvest time
        worker.setLastHarvestTime(System.currentTimeMillis());
        
        plugin.getLogger().info("Worker " + worker.getWorkerId() + " completed harvest processing");
    }
    
    /**
     * Remove a worker
     */
    public void removeWorker(UUID workerId) {
        AfkWorker worker = workers.remove(workerId);
        if (worker == null) return;
        
        // Remove from zone tracking
        List<UUID> zoneWorkerList = zoneWorkers.get(worker.getZone().getId());
        if (zoneWorkerList != null) {
            zoneWorkerList.remove(workerId);
        }
        
        // Remove from zone
        worker.getZone().removeWorker(worker);
        
        // Remove entity
        if (worker.getEntityInstance() != null) {
            plugin.getEdLibIntegration().removeEntity((EdEntity) worker.getEntityInstance());
        }
        
        // Note: We intentionally do NOT leave the EdTools session here
        // This allows players to return and reactivate AFK mining without rejoining EdTools
        // The EdTools session will remain active until the player manually leaves or the server restarts
        
        worker.setActive(false);
        
        if (plugin.getConfigManager().isDebugEnabled()) {
            plugin.getLogger().info("Removed worker " + workerId);
        }
    }
    
    /**
     * Remove all workers for a player
     */
    public void removePlayerWorkers(UUID playerId) {
        List<UUID> toRemove = new ArrayList<>();
        
        for (AfkWorker worker : workers.values()) {
            if (worker.getPlayerId().equals(playerId)) {
                toRemove.add(worker.getWorkerId());
            }
        }
        
        toRemove.forEach(this::removeWorker);
    }
    
    /**
     * Remove all workers from a zone
     */
    public void removeZoneWorkers(String zoneId) {
        List<UUID> workerList = zoneWorkers.get(zoneId);
        if (workerList == null) return;
        
        List<UUID> toRemove = new ArrayList<>(workerList);
        toRemove.forEach(this::removeWorker);
    }
    
    /**
     * Get player's worker in a zone
     */
    public UUID getPlayerWorkerInZone(UUID playerId, String zoneId) {
        for (AfkWorker worker : workers.values()) {
            if (worker.getPlayerId().equals(playerId) && 
                worker.getZone().getId().equals(zoneId)) {
                return worker.getWorkerId();
            }
        }
        return null;
    }
    
    /**
     * Get all workers for a player
     */
    public List<AfkWorker> getPlayerWorkers(UUID playerId) {
        List<AfkWorker> playerWorkers = new ArrayList<>();
        for (AfkWorker worker : workers.values()) {
            if (worker.getPlayerId().equals(playerId)) {
                playerWorkers.add(worker);
            }
        }
        return playerWorkers;
    }
    
    /**
     * Get all workers in a zone
     */
    public List<AfkWorker> getZoneWorkers(String zoneId) {
        List<UUID> workerIds = zoneWorkers.get(zoneId);
        if (workerIds == null) return Collections.emptyList();
        
        List<AfkWorker> result = new ArrayList<>();
        for (UUID workerId : workerIds) {
            AfkWorker worker = workers.get(workerId);
            if (worker != null) {
                result.add(worker);
            }
        }
        return result;
    }
    
    /**
     * Get a worker by ID
     */
    public AfkWorker getWorker(UUID workerId) {
        return workers.get(workerId);
    }
    
    /**
     * Get worker by player
     */
    public AfkWorker getWorkerByPlayer(Player player) {
        if (player == null) return null;
        
        for (AfkWorker worker : workers.values()) {
            if (worker.getPlayerId().equals(player.getUniqueId())) {
                return worker;
            }
        }
        return null;
    }
    
    /**
     * Get all workers
     */
    public Collection<AfkWorker> getAllWorkers() {
        return workers.values();
    }
    
    /**
     * Shutdown worker manager
     */
    public void shutdown() {
        // Stop task
        if (workerTaskId != -1) {
            Bukkit.getScheduler().cancelTask(workerTaskId);
        }
        
        // Remove all workers
        List<UUID> allWorkerIds = new ArrayList<>(workers.keySet());
        allWorkerIds.forEach(this::removeWorker);
        
        workers.clear();
        zoneWorkers.clear();
    }
}
