package gz.devian.afkzoneedtools.commands;

import gz.devian.afkzoneedtools.AfkZoneEdtools;
import gz.devian.afkzoneedtools.models.AfkWorker;
import gz.devian.afkzoneedtools.models.AfkZone;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Main command for AFK zone management
 * Commands: /afkzone [create|delete|list|edit|teleport|reload|join|leave|info|workers]
 */
public class AfkZoneCommand implements CommandExecutor, TabCompleter {
    
    private final AfkZoneEdtools plugin;
    
    public AfkZoneCommand(AfkZoneEdtools plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "list":
                return handleList(sender);
                
            case "reload":
                return handleReload(sender);
                
            case "join":
                return handleJoin(sender, args);
                
            case "leave":
                return handleLeave(sender);
                
            case "info":
                return handleInfo(sender, args);
                
            case "workers":
                return handleWorkers(sender, args);
                
            case "teleport":
            case "tp":
                return handleTeleport(sender, args);
                
            case "create":
                return handleCreate(sender, args);
                
            case "delete":
                return handleDelete(sender, args);
                
            case "edit":
                return handleEdit(sender, args);
                
            case "check":
                return handleCheck(sender, args);
                
            default:
                sendHelp(sender);
                return true;
        }
    }
    
    /**
     * List all AFK zones
     */
    private boolean handleList(CommandSender sender) {
        if (!sender.hasPermission("afkzone.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
            return true;
        }
        
        sender.sendMessage(plugin.getConfigManager().getMessage("zone_list_header"));
        
        for (AfkZone zone : plugin.getZoneManager().getAllZones()) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("id", zone.getId());
            replacements.put("name", zone.getDisplayName());
            replacements.put("type", zone.getType().name());
            
            sender.sendMessage(plugin.getConfigManager().getMessage("zone_list_entry", replacements));
        }
        
        return true;
    }
    
    /**
     * Reload plugin configuration
     */
    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("afkzone.reload")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
            return true;
        }
        
        plugin.getConfigManager().reloadConfigs();
        plugin.getZoneManager().loadZones();
        plugin.getBlockManager().cleanup();
        plugin.getBlockManager().initializeAllZones();
        
        sender.sendMessage(plugin.getConfigManager().getMessage("config_reloaded"));
        return true;
    }
    
    /**
     * Join an AFK zone
     */
    private boolean handleJoin(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }
        
        if (!sender.hasPermission("afkzone.use")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /afkzone join <zone>");
            return true;
        }
        
        Player player = (Player) sender;
        String zoneId = args[1];
        
        AfkZone zone = plugin.getZoneManager().getZone(zoneId);
        if (zone == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("zone_not_found"));
            return true;
        }
        
        // CRITICAL: Verify player is within zone boundaries before allowing join
        if (!zone.containsLocation(player.getLocation())) {
            sender.sendMessage("§cYou must be inside the zone boundaries to join!");
            sender.sendMessage("§eZone boundaries:");
            sender.sendMessage("§7  Min: " + zone.getMinCorner());
            sender.sendMessage("§7  Max: " + zone.getMaxCorner());
            sender.sendMessage("§7  Your location: " + player.getLocation());
            return true;
        }
        
        // Verify zone has valid boundaries
        if (zone.getMinCorner() == null || zone.getMaxCorner() == null) {
            sender.sendMessage("§cZone has invalid boundaries! Please contact an administrator.");
            return true;
        }
        
        // Create worker
        AfkWorker worker = plugin.getWorkerManager().createWorker(player, zone);
        if (worker == null) {
            sender.sendMessage("§cFailed to join AFK zone! Zone may be full.");
            return true;
        }
        
        Map<String, String> replacements = new HashMap<>();
        replacements.put("zone", zone.getDisplayName());
        sender.sendMessage(plugin.getConfigManager().getMessage("worker_spawned", replacements));
        
        return true;
    }
    
    /**
     * Leave current AFK zone
     */
    private boolean handleLeave(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Remove all workers for player
        plugin.getWorkerManager().removePlayerWorkers(player.getUniqueId());
        
        sender.sendMessage("§aYou have left all AFK zones.");
        return true;
    }
    
    /**
     * Get zone info
     */
    private boolean handleInfo(CommandSender sender, String[] args) {
        if (!sender.hasPermission("afkzone.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /afkzone info <zone>");
            return true;
        }
        
        String zoneId = args[1];
        AfkZone zone = plugin.getZoneManager().getZone(zoneId);
        
        if (zone == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("zone_not_found"));
            return true;
        }
        
        sender.sendMessage("§6=== AFK Zone Info: " + zone.getDisplayName() + " ===");
        sender.sendMessage("§eID: §f" + zone.getId());
        sender.sendMessage("§eType: §f" + zone.getType());
        sender.sendMessage("§eBlock: §f" + zone.getBlockMaterial());
        sender.sendMessage("§eReward: §f" + zone.getBaseReward() + " " + zone.getRewardCurrency());
        sender.sendMessage("§eHarvest Interval: §f" + (zone.getHarvestInterval() / 1000.0) + "s");
        sender.sendMessage("§eActive Workers: §f" + zone.getActiveWorkers().size() + "/" + zone.getMaxWorkers());
        sender.sendMessage("§eEnabled: §f" + zone.isEnabled());
        
        return true;
    }
    
    /**
     * List workers in a zone
     */
    private boolean handleWorkers(CommandSender sender, String[] args) {
        if (!sender.hasPermission("afkzone.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /afkzone workers <zone>");
            return true;
        }
        
        String zoneId = args[1];
        AfkZone zone = plugin.getZoneManager().getZone(zoneId);
        
        if (zone == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("zone_not_found"));
            return true;
        }
        
        sender.sendMessage("§6=== Workers in " + zone.getDisplayName() + " ===");
        
        List<AfkWorker> workers = plugin.getWorkerManager().getZoneWorkers(zoneId);
        if (workers.isEmpty()) {
            sender.sendMessage("§7No active workers in this zone.");
            return true;
        }
        
        for (AfkWorker worker : workers) {
            sender.sendMessage("§e" + worker.getPlayerName() + " §7- §fHarvests: " + worker.getHarvestCount());
        }
        
        return true;
    }
    
    /**
     * Teleport to a zone
     */
    private boolean handleTeleport(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }
        
        if (!sender.hasPermission("afkzone.teleport")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /afkzone tp <zone>");
            return true;
        }
        
        Player player = (Player) sender;
        String zoneId = args[1];
        
        AfkZone zone = plugin.getZoneManager().getZone(zoneId);
        if (zone == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("zone_not_found"));
            return true;
        }
        
        if (zone.getCenterLocation() == null) {
            sender.sendMessage("§cZone has no valid location!");
            return true;
        }
        
        player.teleport(zone.getCenterLocation());
        
        Map<String, String> replacements = new HashMap<>();
        replacements.put("zone", zone.getDisplayName());
        sender.sendMessage(plugin.getConfigManager().getMessage("zone_teleported", replacements));
        
        return true;
    }
    
    /**
     * Create a new zone (placeholder)
     */
    private boolean handleCreate(CommandSender sender, String[] args) {
        if (!sender.hasPermission("afkzone.create")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
            return true;
        }
        
        sender.sendMessage("§eZone creation is done through the zones.yml configuration file.");
        sender.sendMessage("§ePlease edit the file and use /afkzone reload to apply changes.");
        return true;
    }
    
    /**
     * Delete a zone (placeholder)
     */
    private boolean handleDelete(CommandSender sender, String[] args) {
        if (!sender.hasPermission("afkzone.delete")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
            return true;
        }
        
        sender.sendMessage("§eZone deletion is done through the zones.yml configuration file.");
        sender.sendMessage("§ePlease edit the file and use /afkzone reload to apply changes.");
        return true;
    }
    
    /**
     * Edit a zone (placeholder)
     */
    private boolean handleEdit(CommandSender sender, String[] args) {
        if (!sender.hasPermission("afkzone.edit")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
            return true;
        }
        
        sender.sendMessage("§eZone editing is done through the zones.yml configuration file.");
        sender.sendMessage("§ePlease edit the file and use /afkzone reload to apply changes.");
        return true;
    }
    
    /**
     * Check if player is inside a zone
     */
    private boolean handleCheck(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }
        
        if (!sender.hasPermission("afkzone.check")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no_permission"));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /afkzone check <zone>");
            return true;
        }
        
        String zoneId = args[1];
        AfkZone zone = plugin.getZoneManager().getZone(zoneId);
        
        if (zone == null) {
            sender.sendMessage(plugin.getConfigManager().getMessage("zone_not_found"));
            return true;
        }
        
        sender.sendMessage("§6=== Zone Location Check ===");
        sender.sendMessage("§eZone: §f" + zone.getDisplayName());
        sender.sendMessage("§eYour Location: §f" + player.getLocation());
        sender.sendMessage("§eZone Min Corner: §f" + zone.getMinCorner());
        sender.sendMessage("§eZone Max Corner: §f" + zone.getMaxCorner());
        sender.sendMessage("§eZone Center: §f" + zone.getCenterLocation());
        
        boolean inside = zone.containsLocation(player.getLocation());
        sender.sendMessage("§eInside Zone: §f" + (inside ? "§aYES" : "§cNO"));
        
        if (!inside) {
            sender.sendMessage("§cYou must be inside the zone boundaries to join!");
        }
        
        return true;
    }
    
    /**
     * Send help message
     */
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6=== AfkZoneEdtools Commands ===");
        sender.sendMessage("§e/afkzone list §7- List all AFK zones");
        sender.sendMessage("§e/afkzone join <zone> §7- Join an AFK zone");
        sender.sendMessage("§e/afkzone leave §7- Leave all AFK zones");
        sender.sendMessage("§e/afkzone info <zone> §7- Get zone information");
        sender.sendMessage("§e/afkzone workers <zone> §7- List workers in a zone");
        sender.sendMessage("§e/afkzone tp <zone> §7- Teleport to a zone");
        sender.sendMessage("§e/afkzone check <zone> §7- Check if you're inside a zone");
        sender.sendMessage("§e/afkzone reload §7- Reload configuration");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("list", "join", "leave", "info", "workers", "tp", "check", "reload")
                .stream()
                .filter(s -> s.startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("join") || subCommand.equals("info") || 
                subCommand.equals("workers") || subCommand.equals("tp") || subCommand.equals("check")) {
                return plugin.getZoneManager().getAllZones().stream()
                    .map(AfkZone::getId)
                    .filter(id -> id.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
            }
        }
        
        return Collections.emptyList();
    }
}
