package pl.psalkowski.peacefulplayers.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import pl.psalkowski.peacefulplayers.PlayerModeManager;
import pl.psalkowski.peacefulplayers.PlayerModeManager.Mode;

import java.util.*;

public class PeacefulCommand implements CommandExecutor, TabCompleter {

    private final PlayerModeManager modeManager;

    public PeacefulCommand(PlayerModeManager modeManager) {
        this.modeManager = modeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("peacefullist")) {
            return handleList(sender);
        }
        return handlePeaceful(sender, args);
    }

    private boolean handlePeaceful(CommandSender sender, String[] args) {
        Player target;
        Mode mode = null;

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Component.text("Usage: /peaceful <player> [peaceful|survival]", NamedTextColor.RED));
                return true;
            }
            target = player;
        } else if (args.length == 1) {
            if (sender instanceof Player player && (args[0].equalsIgnoreCase("peaceful") || args[0].equalsIgnoreCase("survival"))) {
                target = player;
                mode = Mode.valueOf(args[0].toUpperCase());
            } else {
                target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage(Component.text("Player not found: " + args[0], NamedTextColor.RED));
                    return true;
                }
                if (!sender.equals(target) && !sender.hasPermission("peacefulplayers.admin")) {
                    sender.sendMessage(Component.text("You don't have permission to change other players' mode", NamedTextColor.RED));
                    return true;
                }
            }
        } else {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Component.text("Player not found: " + args[0], NamedTextColor.RED));
                return true;
            }
            if (!sender.equals(target) && !sender.hasPermission("peacefulplayers.admin")) {
                sender.sendMessage(Component.text("You don't have permission to change other players' mode", NamedTextColor.RED));
                return true;
            }
            try {
                mode = Mode.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                sender.sendMessage(Component.text("Invalid mode. Use 'peaceful' or 'survival'", NamedTextColor.RED));
                return true;
            }
        }

        if (mode == null) {
            mode = modeManager.isPeaceful(target) ? Mode.SURVIVAL : Mode.PEACEFUL;
        }

        modeManager.setMode(target, mode);

        Component message = Component.text()
                .append(Component.text(target.getName(), NamedTextColor.YELLOW))
                .append(Component.text(" is now in ", NamedTextColor.GRAY))
                .append(mode == Mode.PEACEFUL
                        ? Component.text("peaceful", NamedTextColor.GREEN)
                        : Component.text("survival", NamedTextColor.RED))
                .append(Component.text(" mode", NamedTextColor.GRAY))
                .build();

        sender.sendMessage(message);
        if (!sender.equals(target)) {
            target.sendMessage(message);
        }

        return true;
    }

    private boolean handleList(CommandSender sender) {
        Map<UUID, Mode> peacefulPlayers = modeManager.getAllPeacefulPlayers();

        if (peacefulPlayers.isEmpty()) {
            sender.sendMessage(Component.text("No players are in peaceful mode", NamedTextColor.GRAY));
            return true;
        }

        sender.sendMessage(Component.text("Players in peaceful mode:", NamedTextColor.GREEN));
        peacefulPlayers.forEach((uuid, mode) -> {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            String name = player.getName() != null ? player.getName() : uuid.toString();
            boolean online = player.isOnline();
            sender.sendMessage(Component.text()
                    .append(Component.text("  - ", NamedTextColor.GRAY))
                    .append(Component.text(name, online ? NamedTextColor.GREEN : NamedTextColor.GRAY))
                    .append(online ? Component.empty() : Component.text(" (offline)", NamedTextColor.DARK_GRAY))
                    .build());
        });

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("peacefullist")) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            if (sender instanceof Player) {
                completions.add("peaceful");
                completions.add("survival");
            }
            if (sender.hasPermission("peacefulplayers.admin")) {
                Bukkit.getOnlinePlayers().forEach(p -> completions.add(p.getName()));
            }
            return completions.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }

        if (args.length == 2 && sender.hasPermission("peacefulplayers.admin")) {
            return Arrays.asList("peaceful", "survival").stream()
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }

        return Collections.emptyList();
    }
}
