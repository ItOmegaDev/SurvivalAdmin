package net.omegasmp.survivaladmin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RoleCommand implements CommandExecutor {

    private final SurvivalAdmin plugin;
    private final String action;

    public RoleCommand(SurvivalAdmin plugin, String action) {
        this.plugin = plugin;
        this.action = action;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getConfigManager().msg("only-players"));
            return true;
        }

        // Only owner can manage roles
        if (!plugin.getRoleManager().isOwner(player)) {
            player.sendMessage(plugin.getConfigManager().msg("no-permission"));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(plugin.getConfigManager().msg("usage-role").replace("{action}", action));
            return true;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            player.sendMessage(plugin.getConfigManager().msg("player-not-found").replace("{player}", targetName));
            return true;
        }

        String targetLower = target.getName().toLowerCase();

        switch (action) {
            case "staff" -> {
                plugin.getRoleManager().setRole(targetLower, "staff");
                player.sendMessage(plugin.getConfigManager().msg("role-given")
                        .replace("{player}", target.getName()).replace("{role}", "staff"));
                target.sendMessage(plugin.getConfigManager().msg("role-staff-given"));
            }
            case "unstaff" -> {
                plugin.getRoleManager().setRole(targetLower, "user");
                player.sendMessage(plugin.getConfigManager().msg("role-removed")
                        .replace("{player}", target.getName()).replace("{role}", "staff"));
                target.sendMessage(plugin.getConfigManager().msg("role-staff-removed"));
            }
            case "owner" -> {
                plugin.getRoleManager().setRole(targetLower, "owner");
                player.sendMessage(plugin.getConfigManager().msg("role-given")
                        .replace("{player}", target.getName()).replace("{role}", "owner"));
                target.sendMessage(plugin.getConfigManager().msg("role-owner-given"));
            }
            case "unowner" -> {
                plugin.getRoleManager().setRole(targetLower, "user");
                player.sendMessage(plugin.getConfigManager().msg("role-removed")
                        .replace("{player}", target.getName()).replace("{role}", "owner"));
                target.sendMessage(plugin.getConfigManager().msg("role-owner-removed"));
            }
        }
        return true;
    }
}
