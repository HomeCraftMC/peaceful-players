package pl.psalkowski.peacefulplayers.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import pl.psalkowski.peacefulplayers.PlayerModeManager;

public class PlayerDamageListener implements Listener {

    private final PlayerModeManager modeManager;
    private double damageReduction;

    public PlayerDamageListener(PlayerModeManager modeManager, double damageReduction) {
        this.modeManager = modeManager;
        this.damageReduction = damageReduction;
    }

    public void setDamageReduction(double damageReduction) {
        this.damageReduction = Math.max(0.0, Math.min(1.0, damageReduction));
    }

    public double getDamageReduction() {
        return damageReduction;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (damageReduction <= 0.0) {
            return;
        }
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (!modeManager.isPeaceful(player)) {
            return;
        }
        double newDamage = event.getDamage() * (1.0 - damageReduction);
        event.setDamage(newDamage);
    }
}
