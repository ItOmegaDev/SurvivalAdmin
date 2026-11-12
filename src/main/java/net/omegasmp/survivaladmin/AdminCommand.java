package net.omegasmp.survivaladmin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommand implements CommandExecutor, org.bukkit.command.TabCompleter {

    private final SurvivalAdmin plugin;

    public AdminCommand(SurvivalAdmin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().msg("only-players"));
            return true;
        }
        Player player = (Player) sender;
        if (!plugin.getRoleManager().isStaff(player)) {
            player.sendMessage(plugin.getConfigManager().msg("no-permission"));
            return true;
        }

        player.sendMessage(plugin.getConfigManager().msg("help-header"));
        player.sendMessage("§7/staff <player> §f- grant Staff role");
        player.sendMessage("§7/unstaff <player> §f- remove Staff role");
        player.sendMessage("§7/owner <player> §f- grant Owner role");
        player.sendMessage("§7/unowner <player> §f- remove Owner role");
        player.sendMessage("§7/sit §f- sit down");
        player.sendMessage("§7/heal [player] §f- heal");
        player.sendMessage("§7/feed [player] §f- feed");
        player.sendMessage("§7/tp <player> §f- teleport to player");
        player.sendMessage("§7/gm <0-3> [player] §f- change gamemode");
        player.sendMessage("§7/fly [player] §f- toggle flight");
        player.sendMessage("§7/god [player] §f- toggle god mode");
        player.sendMessage("§7/time <day|night|number> §f- change time");
        player.sendMessage("§7/weather <clear|rain|thunder> §f- change weather");
        player.sendMessage("§7/inv <player> §f- view inventory");
        player.sendMessage("§7/getoff <player> <time> [reason] §f- ban player");
        player.sendMessage("§7/invis §f- toggle invisibility");
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(org.bukkit.command.CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        return java.util.Collections.emptyList();
    }
}