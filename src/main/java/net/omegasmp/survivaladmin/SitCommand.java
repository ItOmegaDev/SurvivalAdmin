package net.omegasmp.survivaladmin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SitCommand implements CommandExecutor {

    private final SurvivalAdmin plugin;
    public SitCommand(SurvivalAdmin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cТільки гравці можуть використовувати цю команду.");
            return true;
        }
        // Toggle sit
        if (player.isSneaking()) {
            player.setSneaking(false);
        }
        // Use Paper's sit via pose
        try {
            player.setPose(org.bukkit.entity.Pose.SITTING);
        } catch (Exception e) {
            player.sendMessage("§cНе вдалося сісти.");
        }
        return true;
    }
}
