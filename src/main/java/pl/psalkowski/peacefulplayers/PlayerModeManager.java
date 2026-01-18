package pl.psalkowski.peacefulplayers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerModeManager {

    public enum Mode {
        PEACEFUL,
        SURVIVAL
    }

    private final Plugin plugin;
    private final File configFile;
    private final Map<UUID, Mode> playerModes = new ConcurrentHashMap<>();
    private final Map<UUID, Set<UUID>> attackedMobs = new ConcurrentHashMap<>();

    public PlayerModeManager(Plugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "players.yml");
        load();
    }

    public Mode getMode(Player player) {
        return playerModes.getOrDefault(player.getUniqueId(), Mode.PEACEFUL);
    }

    public void setMode(Player player, Mode mode) {
        if (mode == Mode.PEACEFUL) {
            playerModes.remove(player.getUniqueId());
        } else {
            playerModes.put(player.getUniqueId(), mode);
        }
        attackedMobs.remove(player.getUniqueId());
        save();
    }

    public boolean isPeaceful(Player player) {
        return getMode(player) == Mode.PEACEFUL;
    }

    public void recordAttack(Player player, UUID mobId) {
        attackedMobs.computeIfAbsent(player.getUniqueId(), k -> ConcurrentHashMap.newKeySet()).add(mobId);
    }

    public boolean hasAttacked(Player player, UUID mobId) {
        Set<UUID> mobs = attackedMobs.get(player.getUniqueId());
        return mobs != null && mobs.contains(mobId);
    }

    public void clearAttackedMob(UUID mobId) {
        attackedMobs.values().forEach(set -> set.remove(mobId));
    }

    public Map<UUID, Mode> getAllPeacefulPlayers() {
        Map<UUID, Mode> result = new HashMap<>();
        playerModes.forEach((uuid, mode) -> {
            if (mode == Mode.PEACEFUL) {
                result.put(uuid, mode);
            }
        });
        return result;
    }

    private void load() {
        if (!configFile.exists()) {
            return;
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        for (String key : config.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                String modeStr = config.getString(key);
                if (modeStr != null) {
                    Mode mode = Mode.valueOf(modeStr.toUpperCase());
                    playerModes.put(uuid, mode);
                }
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid player data: " + key);
            }
        }
        plugin.getLogger().info("Loaded " + playerModes.size() + " player mode settings");
    }

    private void save() {
        FileConfiguration config = new YamlConfiguration();
        playerModes.forEach((uuid, mode) -> config.set(uuid.toString(), mode.name()));
        try {
            plugin.getDataFolder().mkdirs();
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save player modes: " + e.getMessage());
        }
    }
}
