package gz.devian.afkzoneedtools.listeners;

import gz.devian.afkzoneedtools.AfkZoneEdtools;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles player events for AFK zones
 */
public class PlayerListener implements Listener {
    
    private final AfkZoneEdtools plugin;
    
    public PlayerListener(AfkZoneEdtools plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Handle player join
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Initialize player data if needed
        if (plugin.getConfigManager().isDebugEnabled()) {
            plugin.getLogger().info("Player " + player.getName() + " joined - AFK workers: " + 
                plugin.getWorkerManager().getPlayerWorkers(player.getUniqueId()).size());
        }
    }
    
    /**
     * Handle player quit - cleanup workers
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Remove all workers for player
        plugin.getWorkerManager().removePlayerWorkers(player.getUniqueId());
        
        if (plugin.getConfigManager().isDebugEnabled()) {
            plugin.getLogger().info("Removed all AFK workers for " + player.getName());
        }
    }
}
