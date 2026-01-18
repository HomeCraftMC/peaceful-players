# PeacefulPlayers

A Paper plugin for family-friendly Minecraft servers that allows players to toggle "peaceful mode" where hostile mobs ignore them until attacked.

## Features

- **Neutral Mobs**: Hostile mobs won't target peaceful players - they walk around as if the player doesn't exist
- **Revenge Mechanic**: If a peaceful player attacks a mob, only that specific mob can target back (not nearby mobs)
- **Keep Inventory**: Peaceful players keep their inventory and XP on death
- **Damage Reduction**: Configurable damage reduction for peaceful players (default: 0%)
- **Per-Player Settings**: Each player can have different mode (peaceful or survival)
- **Persistent**: Settings are saved to disk and persist across server restarts
- **Tab Completion**: Full auto-complete support for commands

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/peaceful` | Toggle your own mode between peaceful and survival | `peacefulplayers.use` |
| `/peaceful peaceful` | Set yourself to peaceful mode | `peacefulplayers.use` |
| `/peaceful survival` | Set yourself to survival mode | `peacefulplayers.use` |
| `/peaceful <player>` | Toggle another player's mode | `peacefulplayers.admin` |
| `/peaceful <player> peaceful` | Set another player to peaceful mode | `peacefulplayers.admin` |
| `/peaceful <player> survival` | Set another player to survival mode | `peacefulplayers.admin` |
| `/peacefullist` | List all players currently in peaceful mode | `peacefulplayers.list` |

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `peacefulplayers.use` | Allows toggling own peaceful mode | op |
| `peacefulplayers.admin` | Allows changing other players' mode | op |
| `peacefulplayers.list` | Allows listing peaceful players | op |

## Behavior

### Peaceful Mode

When a player is in peaceful mode:

1. **Mob Targeting**: Hostile mobs (zombies, skeletons, creepers, etc.) will NOT target the player
   - Mobs ignore the player completely
   - Mobs won't chase or attack
   - Player can walk past mobs safely

2. **Revenge Mechanic**: If the player attacks a mob:
   - Only that specific mob can now target the player
   - Other nearby mobs still ignore the player
   - Example: Attack 1 zombie out of 5, only that 1 zombie fights back

3. **Death Protection**: When a peaceful player dies:
   - Inventory is kept
   - XP levels are kept
   - No items drop
   - No XP orbs drop

4. **Damage Reduction** (configurable): Peaceful players can receive reduced damage from all sources
   - Configured in `config.yml`
   - Default: 0% (no reduction)
   - Set to 0.5 for 50% reduction, 1.0 for invincibility

### Survival Mode

Normal Minecraft behavior - mobs target and attack as usual, items drop on death.

## Configuration

### config.yml

Main plugin configuration in `plugins/PeacefulPlayers/config.yml`:

```yaml
# Damage reduction for players in peaceful mode (0.0 - 1.0)
# 0.0 = no reduction (default)
# 0.5 = 50% reduction (half damage)
# 1.0 = 100% reduction (no damage)
damage-reduction: 0.0
```

### players.yml

Player settings are stored in `plugins/PeacefulPlayers/players.yml`:

```yaml
# Format: player-uuid: MODE
550e8400-e29b-41d4-a716-446655440000: PEACEFUL
6ba7b810-9dad-11d1-80b4-00c04fd430c8: PEACEFUL
```

Players not in the file default to PEACEFUL mode.

## Installation

### Download

- **Latest Release**: https://s3.psalkowski.pl/minecraft-plugins/peaceful-players/PeacefulPlayers.jar
- **Snapshot (dev)**: https://s3.psalkowski.pl/minecraft-plugins/peaceful-players/PeacefulPlayers-X.Y.Z-SNAPSHOT.jar

### Steps

1. Download `PeacefulPlayers.jar`
2. Place in your server's `plugins/` folder
3. Restart the server
4. All players start in peaceful mode by default
5. Use `/peaceful survival` to switch to survival mode

## Technical Details

- **Minecraft Version**: 1.21+
- **Server Software**: Paper (or Paper forks)
- **Java Version**: 21+

### Events Handled

- `EntityTargetLivingEntityEvent` - Cancels mob targeting for peaceful players
- `EntityDamageByEntityEvent` - Records when peaceful players attack mobs
- `EntityDamageEvent` - Applies damage reduction for peaceful players
- `EntityDeathEvent` - Cleans up tracked mobs when they die
- `PlayerDeathEvent` - Keeps inventory for peaceful players

## Building from Source

```bash
mvn clean package
```

Output: `target/PeacefulPlayers-<version>.jar`

## CI/CD

Builds are automated via Woodpecker CI:
- **Push to main**: Builds and uploads `PeacefulPlayers-X.Y.Z-SNAPSHOT.jar`
- **Tag `vX.Y.Z`**: Builds release, uploads `PeacefulPlayers.jar` (latest) and `PeacefulPlayers-X.Y.Z.jar` (versioned), then bumps version

## License

MIT
