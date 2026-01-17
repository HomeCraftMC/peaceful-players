package pl.psalkowski.peacefulplayers.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import pl.psalkowski.peacefulplayers.PlayerModeManager;

public class MobTargetListener implements Listener {

    private final PlayerModeManager modeManager;

    public MobTargetListener(PlayerModeManager modeManager) {
        this.modeManager = modeManager;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onMobTarget(EntityTargetLivingEntityEvent event) {
        if (!(event.getEntity() instanceof Monster)) {
            return;
        }
        if (!(event.getTarget() instanceof Player player)) {
            return;
        }
        if (!modeManager.isPeaceful(player)) {
            return;
        }
        if (modeManager.hasAttacked(player, event.getEntity().getUniqueId())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerAttackMob(EntityDamageByEntityEvent event) {
        Player player = getPlayerAttacker(event.getDamager());
        if (player == null) {
            return;
        }
        if (!(event.getEntity() instanceof Monster)) {
            return;
        }
        if (!modeManager.isPeaceful(player)) {
            return;
        }
        modeManager.recordAttack(player, event.getEntity().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMobDeath(EntityDeathEvent event) {
        modeManager.clearAttackedMob(event.getEntity().getUniqueId());
    }

    private Player getPlayerAttacker(Entity damager) {
        if (damager instanceof Player player) {
            return player;
        }
        if (damager instanceof org.bukkit.entity.Projectile projectile) {
            if (projectile.getShooter() instanceof Player player) {
                return player;
            }
        }
        return null;
    }
}
