package net.omegasmp.survivaladmin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
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
        event.setJoinMessage(plugin.getConfigManager().msgRaw("join").replace("{player}", rolePrefix(role) + player.getName()));
    }

    private String rolePrefix(String role) {
        return switch (role) {
            case "owner" -> "§6[Owner] ";
            case "staff" -> "§e[Staff] ";
            default -> "";
        };
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(plugin.getConfigManager().msgRaw("quit").replace("{player}", player.getName()));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();
        if (killer != null) {
            event.setDeathMessage(plugin.getConfigManager().msg("kill")
                    .replace("{player}", player.getName())
                    .replace("{killer}", killer.getName()));
        } else {
            event.setDeathMessage(plugin.getConfigManager().msg("death").replace("{player}", player.getName()));
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        event.setLeaveMessage(plugin.getConfigManager().msg("kick").replace("{player}", player.getName()));
    }

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        if (event.getAdvancement().getKey().getKey().startsWith("recipes/")) return;
        Player player = event.getPlayer();
        String name = event.getAdvancement().getDisplay() == null ? event.getAdvancement().getKey().getKey()
                : event.getAdvancement().getDisplay().title().toString();
        Bukkit.broadcastMessage(plugin.getConfigManager().msg("advancement")
                .replace("{player}", player.getName())
                .replace("{advancement}", name));
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String raw = event.getMessage().toLowerCase().trim();
        if (!raw.startsWith("/")) return;
        String cmd = raw.substring(1).split(" ")[0];
        if (cmd.contains(":")) {
            cmd = cmd.substring(cmd.lastIndexOf(":") + 1);
        }

        if (plugin.getRoleManager().isStaff(player)) return;

        List<String> allowed = plugin.getConfig().getStringList("user-commands");
        if (allowed.contains(cmd)) return;

        event.setCancelled(true);
        player.sendMessage(plugin.getConfigManager().msg("command-blocked"));
    }
}