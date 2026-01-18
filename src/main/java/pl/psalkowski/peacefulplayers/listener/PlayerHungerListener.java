package pl.psalkowski.peacefulplayers.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import pl.psalkowski.peacefulplayers.PlayerModeManager;

public class PlayerHungerListener implements Listener {

    private final PlayerModeManager modeManager;

    public PlayerHungerListener(PlayerModeManager modeManager) {
        this.modeManager = modeManager;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (!modeManager.isPeaceful(player)) {
            return;
        }
        if (event.getFoodLevel() < player.getFoodLevel()) {
            event.setCancelled(true);
        }
    }
}
