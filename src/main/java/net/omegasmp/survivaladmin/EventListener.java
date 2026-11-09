package net.omegasmp.survivaladmin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class EventListener implements Listener {

    private final SurvivalAdmin plugin;

    public EventListener(SurvivalAdmin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getRoleManager().migrateOnJoin(player);
        String role = plugin.getRoleManager().getRole(player);
        String prefix = switch (role) {
            case "owner" -> "§6[Owner] ";
            case "staff" -> "§e[Staff] ";
            default -> "§7";
        };
        event.setJoinMessage(prefix + player.getName() + " §aприєднався до гри");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage("§7" + player.getName() + " §cпокинув гру");
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();
        if (killer != null) {
            event.setDeathMessage("§c" + player.getName() + " §7був убитий гравцем §c" + killer.getName());
        } else {
            event.setDeathMessage("§c" + player.getName() + " §7помер");
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String raw = event.getMessage().toLowerCase().trim();
        if (!raw.startsWith("/")) return;
        String cmd = raw.substring(1).split(" ")[0];
        // Strip namespace prefix (e.g., "bukkit:plugins" -> "plugins")
        if (cmd.contains(":")) {
            cmd = cmd.substring(cmd.lastIndexOf(":") + 1);
        }

        // Allow commands for staff and owner
        if (plugin.getRoleManager().isStaff(player)) return;

        // User commands allowed
        List<String> allowed = plugin.getConfig().getStringList("user-commands");
        if (allowed.contains(cmd)) return;

        // Block everything else for users
        event.setCancelled(true);
        player.sendMessage(plugin.getConfigManager().msg("command-blocked"));
    }
}
