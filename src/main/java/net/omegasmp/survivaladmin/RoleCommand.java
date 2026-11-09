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
            sender.sendMessage("§cТільки гравці можуть використовувати цю команду.");
            return true;
        }

        // Only owner can manage roles
        if (!plugin.getRoleManager().isOwner(player)) {
            player.sendMessage(plugin.getConfigManager().msg("no-permission"));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("§eВикористання: /" + action + " <гравець>");
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
                target.sendMessage("§aВам видано роль §eStaff§a!");
            }
            case "unstaff" -> {
                plugin.getRoleManager().setRole(targetLower, "user");
                player.sendMessage(plugin.getConfigManager().msg("role-removed")
                        .replace("{player}", target.getName()).replace("{role}", "staff"));
                target.sendMessage("§cУ вас забрано роль §eStaff§c.");
            }
            case "owner" -> {
                plugin.getRoleManager().setRole(targetLower, "owner");
                player.sendMessage(plugin.getConfigManager().msg("role-given")
                        .replace("{player}", target.getName()).replace("{role}", "owner"));
                target.sendMessage("§aВам видано роль §6Owner§a!");
            }
            case "unowner" -> {
                plugin.getRoleManager().setRole(targetLower, "user");
                player.sendMessage(plugin.getConfigManager().msg("role-removed")
                        .replace("{player}", target.getName()).replace("{role}", "owner"));
                target.sendMessage("§cУ вас забрано роль §6Owner§c.");
            }
        }
        return true;
    }
}
