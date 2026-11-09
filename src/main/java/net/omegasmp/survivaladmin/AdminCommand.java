package net.omegasmp.survivaladmin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommand implements CommandExecutor {

    private final SurvivalAdmin plugin;

    public AdminCommand(SurvivalAdmin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cТільки гравці можуть використовувати цю команду.");
            return true;
        }

        if (!plugin.getRoleManager().isStaff(player)) {
            player.sendMessage(plugin.getConfigManager().msg("no-permission"));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§6=== SurvivalAdmin ===");
            player.sendMessage("§7/staff <гравець> §f- видати роль Staff");
            player.sendMessage("§7/unstaff <гравець> §f- забрати роль Staff");
            player.sendMessage("§7/owner <гравець> §f- видати роль Owner");
            player.sendMessage("§7/unowner <гравець> §f- забрати роль Owner");
            player.sendMessage("§7/sit §f- сісти");
            return true;
        }

        return true;
    }
}
