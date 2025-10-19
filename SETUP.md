# AfkZoneEdtools - Setup Guide

## Quick Start Guide

### Step 1: Install Dependencies

1. **Download Required Files**:
   - EdTools plugin (main dependency)
   - EdLib API (included with EdTools)
   - AfkZoneEdtools plugin

2. **File Placement**:
   ```
   server/
   ├── plugins/
   │   ├── EdTools.jar
   │   └── AfkZoneEdtools.jar
   └── lib/
       ├── EdTools-API.jar
       └── EdLib-API.jar
   ```

3. **Start Server**: The plugin will generate default configuration files

### Step 2: Configure Your First Zone

1. **Edit `plugins/AfkZoneEdtools/zones.yml`**:

```yaml
zones:
  my_first_zone:
    display_name: "&6My First AFK Zone"
    type: "MINING"
    
    # Set this to your desired location (world:x:y:z)
    center_location: "world:100:64:100"
    
    # Block to mine (any Minecraft material)
    block_material: "DIAMOND_ORE"
    
    # Which EdTools OmniTools can use this zone
    allowed_tools:
      - "diamond_pickaxe"
      - "netherite_pickaxe"
    
    # Mining speed (milliseconds between harvests)
    harvest_interval: 5000
    regeneration_time: 3000
    
    # Rewards (uses EdTools currency system)
    reward_currency: "farm-coins"
    base_reward: 100
    
    # Worker settings
    max_workers: 5
    worker_type: "VILLAGER"
    
    # CRITICAL: Full EdTools integration
    edtools_integration:
      affect_enchants: true
      affect_sell: true
      affect_block_currencies: true
      affect_lucky_blocks: true
      use_global_session: true
      apply_boosters: true
      grant_experience: true
    
    # Visual effects
    effects:
      mining_particles: true
      break_particles: true
      success_sound: "BLOCK_STONE_BREAK"
      particle_type: "BLOCK_CRACK"
    
    enabled: true
```

2. **Reload Configuration**:
   ```
   /afkzone reload
   ```

### Step 3: Test Your Zone

1. **Teleport to Zone**:
   ```
   /afkzone tp my_first_zone
   ```

2. **Join the Zone**:
   ```
   /afkzone join my_first_zone
   ```

3. **Check Zone Info**:
   ```
   /afkzone info my_first_zone
   ```

4. **Leave Zone**:
   ```
   /afkzone leave
   ```

## Common Zone Configurations

### Mining Zone (Diamond Ore)

```yaml
diamond_mine:
  display_name: "&bDiamond Mine"
  type: "MINING"
  center_location: "world:100:64:100"
  block_material: "DIAMOND_ORE"
  allowed_tools: ["diamond_pickaxe", "netherite_pickaxe"]
  harvest_interval: 5000
  reward_currency: "farm-coins"
  base_reward: 150
  max_workers: 5
  edtools_integration:
    affect_enchants: true
    affect_sell: true
    affect_block_currencies: true
    affect_lucky_blocks: true
```

### Farming Zone (Wheat)

```yaml
wheat_farm:
  display_name: "&eWheat Farm"
  type: "FARMING"
  center_location: "world:200:64:200"
  block_material: "WHEAT"
  allowed_tools: ["diamond_hoe", "netherite_hoe"]
  harvest_interval: 3000
  reward_currency: "farm-coins"
  base_reward: 75
  max_workers: 10
  edtools_integration:
    affect_enchants: true
    affect_sell: true
    affect_block_currencies: true
    affect_lucky_blocks: true
```

### High-Value Zone (Netherite)

```yaml
netherite_mine:
  display_name: "&4Netherite Mine"
  type: "MINING"
  center_location: "world_nether:0:64:0"
  block_material: "ANCIENT_DEBRIS"
  allowed_tools: ["netherite_pickaxe", "ultimate_pickaxe"]
  harvest_interval: 10000
  reward_currency: "farm-coins"
  base_reward: 500
  max_workers: 3
  edtools_integration:
    affect_enchants: true
    affect_sell: true
    affect_block_currencies: true
    affect_lucky_blocks: true
```

## Configuration Tips

### Harvest Interval Calculation

- **Fast Mining** (3000ms = 3 seconds): For common blocks
- **Normal Mining** (5000ms = 5 seconds): For rare blocks
- **Slow Mining** (10000ms = 10 seconds): For very rare blocks

### Reward Balancing

Calculate rewards based on:
1. Block rarity
2. Harvest interval
3. Tool requirements
4. Zone accessibility

Example: Diamond Ore
- Harvest every 5 seconds = 12 harvests per minute
- Base reward: 100 coins
- Per minute: 1,200 coins
- With 2x booster: 2,400 coins per minute

### Worker Limits

- **Small Zones**: 3-5 workers
- **Medium Zones**: 5-10 workers
- **Large Zones**: 10-20 workers

Consider server performance when setting limits.

## EdTools Integration Settings

### Critical Flags (Must be TRUE for normal zone behavior)

```yaml
edtools_integration:
  # Activates tool enchantments (TNT, Lightning, etc.)
  affect_enchants: true
  
  # Triggers EdTools selling system
  affect_sell: true
  
  # Gives currency rewards based on blocks
  affect_block_currencies: true
  
  # Enables lucky block mechanics
  affect_lucky_blocks: true
  
  # Uses EdTools global sessions
  use_global_session: true
  
  # Applies all EdTools boosters
  apply_boosters: true
  
  # Grants EdTools experience
  grant_experience: true
```

### Tool Configuration

Match your EdTools OmniTool IDs:

```yaml
allowed_tools:
  - "diamond_pickaxe"      # Basic pickaxe
  - "netherite_pickaxe"    # Advanced pickaxe
  - "ultimate_pickaxe"     # Ultimate pickaxe
  - "diamond_hoe"          # Farming tool
  - "custom_tool_id"       # Your custom tools
```

## Troubleshooting

### Issue: Workers not spawning
**Solution**: 
- Check if zone has valid center_location
- Verify world name is correct
- Ensure player has valid OmniTool
- Check max_workers limit

### Issue: No currency rewards
**Solution**:
- Verify `affect_block_currencies: true`
- Check EdTools currency configuration
- Ensure reward_currency matches EdTools currency ID
- Check if boosters are configured correctly

### Issue: Enchantments not working
**Solution**:
- Verify `affect_enchants: true`
- Check if player's tool has enchantments
- Ensure EdTools enchantments are configured
- Check tool ID matches in allowed_tools

### Issue: Block display not showing
**Solution**:
- Verify EdLib is installed and working
- Check block_material is valid
- Reload plugin: `/afkzone reload`
- Check server logs for errors

## Performance Optimization

### Config Settings

```yaml
performance:
  async_block_generation: true
  entity_tick_rate: 20
  block_regeneration_delay: 60
  worker_update_interval: 10
```

### Recommended Settings by Server Size

**Small Server (< 20 players)**:
```yaml
entity_tick_rate: 10
worker_update_interval: 5
max_workers_per_zone: 10
```

**Medium Server (20-50 players)**:
```yaml
entity_tick_rate: 20
worker_update_interval: 10
max_workers_per_zone: 8
```

**Large Server (> 50 players)**:
```yaml
entity_tick_rate: 40
worker_update_interval: 20
max_workers_per_zone: 5
```

## Advanced Features

### Custom Block Displays

```yaml
block_display:
  custom_model_data: 100
  scale: 1.5
  glow: true
  glow_color: "AQUA"
```

### Visual Effects

```yaml
effects:
  mining_particles: true
  break_particles: true
  success_sound: "BLOCK_STONE_BREAK"
  particle_type: "CRIT"
```

### Session Types

- **Global Session**: All players share the same zone instance
  ```yaml
  use_global_session: true
  ```

- **Alone Session**: Each player gets their own zone instance
  ```yaml
  use_global_session: false
  ```

## Support & Debugging

### Enable Debug Mode

```yaml
general:
  debug: true
```

This will log:
- Worker creation/removal
- Block mining events
- Currency rewards
- Enchantment activations
- Zone session management

### Check Plugin Status

```
/afkzone list         # List all zones
/afkzone info <zone>  # Detailed zone info
/afkzone workers <zone> # Active workers
```

### Common Log Messages

- `"Worker created for <player>"` - Worker successfully spawned
- `"Mined block for <player>"` - Successful harvest
- `"Currency earned"` - Reward given
- `"Enchant activated"` - Enchantment triggered

## Best Practices

1. **Start Small**: Create one test zone first
2. **Test Thoroughly**: Test all EdTools integration features
3. **Balance Rewards**: Consider economy impact
4. **Monitor Performance**: Watch TPS and resource usage
5. **Regular Backups**: Backup zones.yml regularly
6. **Update Dependencies**: Keep EdTools and EdLib updated

## Next Steps

1. Create multiple zones for different resources
2. Configure zone-specific boosters
3. Set up custom enchantments
4. Create progression paths (easy → hard zones)
5. Integrate with other plugins (ranks, permissions)

---

**For more help, enable debug mode and check server logs!**
