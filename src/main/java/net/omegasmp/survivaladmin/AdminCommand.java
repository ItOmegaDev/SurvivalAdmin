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

        player.sendMessage("§6=== SurvivalAdmin v2.0.0 ===");
        player.sendMessage("§7/staff <гравець> §f- видати роль Staff");
        player.sendMessage("§7/unstaff <гравець> §f- забрати роль Staff");
        player.sendMessage("§7/owner <гравець> §f- видати роль Owner");
        player.sendMessage("§7/unowner <гравець> §f- забрати роль Owner");
        player.sendMessage("§7/sit §f- сісти");
        player.sendMessage("§7/heal [гравець] §f- вилікувати");
        player.sendMessage("§7/feed [гравець] §f- нагодувати");
        player.sendMessage("§7/tp <гравець> §f- телепортуватися");
        player.sendMessage("§7/gm <0-3> [гравець] §f- змінити режим гри");
        player.sendMessage("§7/fly [гравець] §f- увімкнути/вимкнути політ");
        player.sendMessage("§7/god [гравець] §f- режим бога");
        player.sendMessage("§7/time <day|night|число> §f- змінити час");
        player.sendMessage("§7/weather <clear|rain|thunder> §f- змінити погоду");
        player.sendMessage("§7/invsee <гравець> §f- переглянути інвентар");
        return true;
    }
}