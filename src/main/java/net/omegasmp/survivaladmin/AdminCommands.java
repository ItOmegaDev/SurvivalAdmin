package net.omegasmp.survivaladmin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommands implements CommandExecutor {

    private final SurvivalAdmin plugin;

    public AdminCommands(SurvivalAdmin plugin) {
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

        String cmd = command.getName().toLowerCase();

        switch (cmd) {
            case "heal" -> {
                if (!plugin.getRoleManager().hasPermission(player, "survivaladmin.heal")) {
                    player.sendMessage(plugin.getConfigManager().msg("no-permission"));
                    return true;
                }
                Player target = args.length > 0 ? Bukkit.getPlayer(args[0]) : player;
                if (target == null) {
                    player.sendMessage(plugin.getConfigManager().msg("player-not-found").replace("{player}", args[0]));
                    return true;
                }
                target.setHealth(target.getMaxHealth());
                target.setFoodLevel(20);
                target.setSaturation(20);
                target.sendMessage("§aВас вилікувано!");
                if (!target.equals(player)) {
                    player.sendMessage("§aВилікувано " + target.getName());
                }
                plugin.getAuditLog().log(player.getName(), "HEAL", target.getName(), "health+food");
            }
            case "feed" -> {
                if (!plugin.getRoleManager().hasPermission(player, "survivaladmin.feed")) {
                    player.sendMessage(plugin.getConfigManager().msg("no-permission"));
                    return true;
                }
                Player target = args.length > 0 ? Bukkit.getPlayer(args[0]) : player;
                if (target == null) {
                    player.sendMessage(plugin.getConfigManager().msg("player-not-found").replace("{player}", args[0]));
                    return true;
                }
                target.setFoodLevel(20);
                target.setSaturation(20);
                target.sendMessage("§aВас нагодовано!");
                if (!target.equals(player)) {
                    player.sendMessage("§aНагодовано " + target.getName());
                }
                plugin.getAuditLog().log(player.getName(), "FEED", target.getName(), "food+saturation");
            }
            case "tp" -> {
                if (!plugin.getRoleManager().hasPermission(player, "survivaladmin.tp")) {
                    player.sendMessage(plugin.getConfigManager().msg("no-permission"));
                    return true;
                }
                if (args.length < 1) {
                    player.sendMessage("§eВикористання: /tp <гравець>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage(plugin.getConfigManager().msg("player-not-found").replace("{player}", args[0]));
                    return true;
                }
                player.teleport(target);
                player.sendMessage("§aТелепортовано до " + target.getName());
                plugin.getAuditLog().log(player.getName(), "TP", target.getName(), "teleport");
            }
            case "gm", "gamemode" -> {
                if (!plugin.getRoleManager().hasPermission(player, "survivaladmin.gamemode")) {
                    player.sendMessage(plugin.getConfigManager().msg("no-permission"));
                    return true;
                }
                if (args.length < 1) {
                    player.sendMessage("§eВикористання: /gm <0|1|2|3> [гравець]");
                    return true;
                }
                GameMode gm = switch (args[0]) {
                    case "0", "survival", "s" -> GameMode.SURVIVAL;
                    case "1", "creative", "c" -> GameMode.CREATIVE;
                    case "2", "adventure", "a" -> GameMode.ADVENTURE;
                    case "3", "spectator", "sp" -> GameMode.SPECTATOR;
                    default -> null;
                };
                if (gm == null) {
                    player.sendMessage("§cНевірний режим гри.");
                    return true;
                }
                Player target = args.length > 1 ? Bukkit.getPlayer(args[1]) : player;
                if (target == null) {
                    player.sendMessage(plugin.getConfigManager().msg("player-not-found").replace("{player}", args[1]));
                    return true;
                }
                target.setGameMode(gm);
                target.sendMessage("§aРежим гри змінено на " + gm.name());
                if (!target.equals(player)) {
                    player.sendMessage("§aЗмінено режим гри " + target.getName() + " на " + gm.name());
                }
                plugin.getAuditLog().log(player.getName(), "GAMEMODE", target.getName(), gm.name());
            }
            case "fly" -> {
                if (!plugin.getRoleManager().hasPermission(player, "survivaladmin.fly")) {
                    player.sendMessage(plugin.getConfigManager().msg("no-permission"));
                    return true;
                }
                Player target = args.length > 0 ? Bukkit.getPlayer(args[0]) : player;
                if (target == null) {
                    player.sendMessage(plugin.getConfigManager().msg("player-not-found").replace("{player}", args[0]));
                    return true;
                }
                boolean newState = !target.getAllowFlight();
                target.setAllowFlight(newState);
                target.setFlying(newState);
                target.sendMessage(newState ? "§aПоліт увімкнено" : "§cПоліт вимкнено");
                if (!target.equals(player)) {
                    player.sendMessage((newState ? "§aУвімкнено" : "§cВимкнено") + " політ для " + target.getName());
                }
                plugin.getAuditLog().log(player.getName(), "FLY", target.getName(), newState ? "on" : "off");
            }
            case "god" -> {
                if (!plugin.getRoleManager().hasPermission(player, "survivaladmin.god")) {
                    player.sendMessage(plugin.getConfigManager().msg("no-permission"));
                    return true;
                }
                Player target = args.length > 0 ? Bukkit.getPlayer(args[0]) : player;
                if (target == null) {
                    player.sendMessage(plugin.getConfigManager().msg("player-not-found").replace("{player}", args[0]));
                    return true;
                }
                boolean newState = !target.isInvulnerable();
                target.setInvulnerable(newState);
                target.sendMessage(newState ? "§aРежим бога увімкнено" : "§cРежим бога вимкнено");
                if (!target.equals(player)) {
                    player.sendMessage((newState ? "§aУвімкнено" : "§cВимкнено") + " режим бога для " + target.getName());
                }
                plugin.getAuditLog().log(player.getName(), "GOD", target.getName(), newState ? "on" : "off");
            }
            case "time" -> {
                if (!plugin.getRoleManager().hasPermission(player, "survivaladmin.time")) {
                    player.sendMessage(plugin.getConfigManager().msg("no-permission"));
                    return true;
                }
                if (args.length < 1) {
                    player.sendMessage("§eВикористання: /time <day|night|noon|midnight|число>");
                    return true;
                }
                long time = switch (args[0].toLowerCase()) {
                    case "day" -> 1000;
                    case "noon" -> 6000;
                    case "night" -> 13000;
                    case "midnight" -> 18000;
                    default -> {
                        try { yield Long.parseLong(args[0]); }
                        catch (NumberFormatException e) { yield -1; }
                    }
                };
                if (time < 0) {
                    player.sendMessage("§cНевірний час.");
                    return true;
                }
                player.getWorld().setTime(time);
                player.sendMessage("§aЧас встановлено: " + args[0]);
                plugin.getAuditLog().log(player.getName(), "TIME", player.getWorld().getName(), args[0]);
            }
            case "weather" -> {
                if (!plugin.getRoleManager().hasPermission(player, "survivaladmin.weather")) {
                    player.sendMessage(plugin.getConfigManager().msg("no-permission"));
                    return true;
                }
                if (args.length < 1) {
                    player.sendMessage("§eВикористання: /weather <clear|rain|thunder>");
                    return true;
                }
                switch (args[0].toLowerCase()) {
                    case "clear", "sun" -> {
                        player.getWorld().setStorm(false);
                        player.getWorld().setThundering(false);
                        player.sendMessage("§aПогода: ясно");
                    }
                    case "rain" -> {
                        player.getWorld().setStorm(true);
                        player.getWorld().setThundering(false);
                        player.sendMessage("§aПогода: дощ");
                    }
                    case "thunder" -> {
                        player.getWorld().setStorm(true);
                        player.getWorld().setThundering(true);
                        player.sendMessage("§aПогода: гроза");
                    }
                    default -> player.sendMessage("§cНевірна погода. Використовуйте: clear, rain, thunder");
                }
                plugin.getAuditLog().log(player.getName(), "WEATHER", player.getWorld().getName(), args[0]);
            }
            case "invsee" -> {
                if (!plugin.getRoleManager().hasPermission(player, "survivaladmin.invsee")) {
                    player.sendMessage(plugin.getConfigManager().msg("no-permission"));
                    return true;
                }
                if (args.length < 1) {
                    player.sendMessage("§eВикористання: /invsee <гравець>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage(plugin.getConfigManager().msg("player-not-found").replace("{player}", args[0]));
                    return true;
                }
                player.openInventory(target.getInventory());
                player.sendMessage("§aВідкрито інвентар " + target.getName());
                plugin.getAuditLog().log(player.getName(), "INVSEE", target.getName(), "opened inventory");
            }
        }
        return true;
    }
}