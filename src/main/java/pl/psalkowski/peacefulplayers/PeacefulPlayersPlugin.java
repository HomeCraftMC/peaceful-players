package pl.psalkowski.peacefulplayers;

import org.bukkit.plugin.java.JavaPlugin;
import pl.psalkowski.peacefulplayers.command.PeacefulCommand;
import pl.psalkowski.peacefulplayers.listener.MobTargetListener;
import pl.psalkowski.peacefulplayers.listener.PlayerDeathListener;

public class PeacefulPlayersPlugin extends JavaPlugin {

    private PlayerModeManager modeManager;

    @Override
    public void onEnable() {
        modeManager = new PlayerModeManager(this);

        PeacefulCommand command = new PeacefulCommand(modeManager);
        getCommand("peaceful").setExecutor(command);
        getCommand("peaceful").setTabCompleter(command);
        getCommand("peacefullist").setExecutor(command);
        getCommand("peacefullist").setTabCompleter(command);

        getServer().getPluginManager().registerEvents(new MobTargetListener(modeManager), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(modeManager), this);

        getLogger().info("PeacefulPlayers enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("PeacefulPlayers disabled!");
    }
}
