package pl.psalkowski.peacefulplayers;

import org.bukkit.plugin.java.JavaPlugin;
import pl.psalkowski.peacefulplayers.command.PeacefulCommand;
import pl.psalkowski.peacefulplayers.listener.MobTargetListener;
import pl.psalkowski.peacefulplayers.listener.PlayerDamageListener;
import pl.psalkowski.peacefulplayers.listener.PlayerDeathListener;
import pl.psalkowski.peacefulplayers.listener.PlayerHungerListener;

public class PeacefulPlayersPlugin extends JavaPlugin {

    private PlayerModeManager modeManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        modeManager = new PlayerModeManager(this);

        double damageReduction = getConfig().getDouble("damage-reduction", 0.0);

        PeacefulCommand command = new PeacefulCommand(modeManager);
        getCommand("peaceful").setExecutor(command);
        getCommand("peaceful").setTabCompleter(command);
        getCommand("peacefullist").setExecutor(command);
        getCommand("peacefullist").setTabCompleter(command);

        getServer().getPluginManager().registerEvents(new MobTargetListener(modeManager), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(modeManager), this);
        getServer().getPluginManager().registerEvents(new PlayerDamageListener(modeManager, damageReduction), this);
        getServer().getPluginManager().registerEvents(new PlayerHungerListener(modeManager), this);

        getLogger().info("PeacefulPlayers enabled! Damage reduction: " + (damageReduction * 100) + "%");
    }

    @Override
    public void onDisable() {
        getLogger().info("PeacefulPlayers disabled!");
    }
}
