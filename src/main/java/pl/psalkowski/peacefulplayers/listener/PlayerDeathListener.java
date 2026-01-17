package pl.psalkowski.peacefulplayers.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import pl.psalkowski.peacefulplayers.PlayerModeManager;

public class PlayerDeathListener implements Listener {

    private final PlayerModeManager modeManager;

    public PlayerDeathListener(PlayerModeManager modeManager) {
        this.modeManager = modeManager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!modeManager.isPeaceful(event.getEntity())) {
            return;
        }
        event.setKeepInventory(true);
        event.setKeepLevel(true);
        event.getDrops().clear();
        event.setDroppedExp(0);
    }
}
