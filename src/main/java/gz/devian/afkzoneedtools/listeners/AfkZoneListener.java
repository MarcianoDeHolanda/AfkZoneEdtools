package gz.devian.afkzoneedtools.listeners;

import gz.devian.afkzoneedtools.AfkZoneEdtools;
import gz.devian.afkzoneedtools.models.AfkZone;
import gz.devian.afkzoneedtools.models.AfkWorker;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Listener for AFK zone interactions and events
 */
public class AfkZoneListener implements Listener {
    
    private final AfkZoneEdtools plugin;
    
    public AfkZoneListener(AfkZoneEdtools plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Handle player clicking on the center block to activate auto-AFK
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }
        
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        
        if (clickedBlock == null) {
            return;
        }
        
        plugin.getLogger().info("Player " + player.getName() + " clicked block at " + clickedBlock.getLocation());
        
        // Check if the clicked block is a center block of any AFK zone
        AfkZone zone = plugin.getZoneManager().getZoneByCenterLocation(clickedBlock.getLocation());
        plugin.getLogger().info("Zone found: " + (zone != null ? zone.getId() : "NULL"));
        
        if (zone == null || !zone.isEnabled()) {
            plugin.getLogger().info("Zone is null or disabled");
            return;
        }
        
        // Check permissions
        if (!player.hasPermission("afkzone.click")) {
            plugin.getLogger().info("Player " + player.getName() + " lacks afkzone.click permission");
            player.sendMessage(ChatColor.RED + "You don't have permission to use AFK zones!");
            return;
        }
        
        // Check if player has the required tool
        ItemStack tool = player.getInventory().getItemInMainHand();
        plugin.getLogger().info("Player tool: " + (tool != null ? tool.getType().name() : "NULL"));
        plugin.getLogger().info("Zone allowed tools: " + zone.getAllowedTools());
        
        if (!isAllowedTool(tool, zone)) {
            plugin.getLogger().info("Tool not allowed for this zone");
            player.sendMessage(ChatColor.RED + "You need a " + zone.getAllowedTools().get(0) + " to activate this AFK zone!");
            return;
        }
        
        // Check if player is already in an AFK worker
        AfkWorker existingWorker = plugin.getWorkerManager().getWorkerByPlayer(player);
        if (existingWorker != null) {
            plugin.getLogger().info("Deactivating existing worker for " + player.getName());
            // Deactivate existing worker (but keep EdTools session)
            plugin.getWorkerManager().removeWorker(existingWorker.getWorkerId());
            player.sendMessage(ChatColor.YELLOW + "AFK zone deactivated!");
            event.setCancelled(true); // Prevent block destruction
            return;
        }
        
        plugin.getLogger().info("Creating new worker for " + player.getName());
        // Activate auto-AFK
        AfkWorker worker = plugin.getWorkerManager().createWorker(player, zone);
        if (worker != null) {
            plugin.getLogger().info("Worker created successfully for " + player.getName());
            player.sendMessage(ChatColor.GREEN + "AFK zone activated! You will now auto-mine " + zone.getBlockMaterial().name().toLowerCase().replace("_", " ") + ".");
        } else {
            plugin.getLogger().warning("Failed to create worker for " + player.getName());
            player.sendMessage(ChatColor.RED + "Failed to activate AFK zone! Zone may be full.");
        }
        
        event.setCancelled(true); // Prevent normal block interaction
    }
    
    /**
     * Handle player movement to deactivate auto-AFK when leaving the zone
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        
        // Only check if player actually moved to a different block
        if (to == null || (from.getBlockX() == to.getBlockX() && 
                          from.getBlockY() == to.getBlockY() && 
                          from.getBlockZ() == to.getBlockZ())) {
            return;
        }
        
        // Check if player is in an AFK worker
        AfkWorker worker = plugin.getWorkerManager().getWorkerByPlayer(player);
        if (worker == null) {
            return;
        }
        
        AfkZone zone = worker.getZone();
        if (zone == null) {
            return;
        }
        
        // Check if player is still within the zone boundaries
        if (!zone.containsLocation(to)) {
            // Player left the zone, deactivate auto-AFK (but keep EdTools session)
            plugin.getWorkerManager().removeWorker(worker.getWorkerId());
            player.sendMessage(ChatColor.YELLOW + "You left the AFK zone. Auto-mining deactivated.");
            // Note: We intentionally keep the EdTools session active so the player can return and reactivate
        }
    }
    
    /**
     * Prevent breaking the center block of AFK zones
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        
        // Check if the block is a center block of any AFK zone
        AfkZone zone = plugin.getZoneManager().getZoneByCenterLocation(block.getLocation());
        if (zone != null && zone.isEnabled()) {
            // Only allow breaking if the player is in an AFK worker for this zone
            AfkWorker worker = plugin.getWorkerManager().getWorkerByPlayer(player);
            if (worker == null || !worker.getZone().getId().equals(zone.getId())) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot break this block! It's part of an AFK zone.");
            }
        }
    }
    
    /**
     * Check if the player's tool is allowed for the zone
     */
    private boolean isAllowedTool(ItemStack tool, AfkZone zone) {
        if (tool == null || tool.getType() == Material.AIR) {
            return false;
        }
        
        List<String> allowedTools = zone.getAllowedTools();
        if (allowedTools == null || allowedTools.isEmpty()) {
            return true; // No restrictions
        }
        
        // Check if the tool matches any allowed tool ID
        for (String allowedTool : allowedTools) {
            if (plugin.getEdToolsIntegration().isOmniTool(tool, allowedTool)) {
                return true;
            }
        }
        
        return false;
    }
}
