package net.omegasmp.survivaladmin;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Date;

public class AdminCommands implements CommandExecutor, org.bukkit.command.TabCompleter {

    private final SurvivalAdmin plugin;

    public AdminCommands(SurvivalAdmin plugin) {
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

        String cmd = command.getName().toLowerCase();

        if (cmd.equals("heal")) {
            doHeal(player, args);
        } else if (cmd.equals("feed")) {
            doFeed(player, args);
        } else if (cmd.equals("tp")) {
            doTp(player, args);
        } else if (cmd.equals("gm") || cmd.equals("gamemode")) {
            doGamemode(player, args);
        } else if (cmd.equals("fly")) {
            doFly(player, args);
        } else if (cmd.equals("god")) {
            doGod(player, args);
        } else if (cmd.equals("time")) {
            doTime(player, args);
        } else if (cmd.equals("weather")) {
            doWeather(player, args);
        } else if (cmd.equals("invsee") || cmd.equals("inv")) {
            doInvsee(player, args);
        } else if (cmd.equals("getoff")) {
            doGetoff(player, args);
        } else if (cmd.equals("invis") || cmd.equals("vanish")) {
            doVanish(player);
        }

        return true;
    }

    private void doHeal(Player player, String[] args) {
        if (!plugin.getRoleManager().hasPermission(player, "survivaladmin.heal")) {
            player.sendMessage(plugin.getConfigManager().msg("no-permission"));
            return;
        }
        Player target = player;
        if (args.length > 0) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(plugin.getConfigManager().msg("player-not-found").replace("{player}", args[0]));
                return;
            }
        }
        target.setHealth(target.getMaxHealth());
        target.setFoodLevel(20);
        target.setSaturation(20);
        target.sendMessage(plugin.getConfigManager().msg("healed"));
        if (!target.equals(player)) {
            player.sendMessage(plugin.getConfigManager().msg("healed-other").replace("{player}", target.getName()));
        }
        plugin.getAuditLog().log(player.getName(), "HEAL", target.getName(), "health+food");
    }

    private void doFeed(Player player, String[] args) {
        if (!plugin.getRoleManager().hasPermission(player, "survivaladmin.feed")) {
            player.sendMessage(plugin.getConfigManager().msg("no-permission"));
            return;
        }
        Player target = player;
        if (args.length > 0) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(plugin.getConfigManager().msg("player-not-found").replace("{player}", args[0]));
                return;
            }
        }
        target.setFoodLevel(20);
        target.setSaturation(20);
        target.sendMessage(plugin.getConfigManager().msg("fed"));
        if (!target.equals(player)) {
            player.sendMessage(plugin.getConfigManager().msg("fed-other").replace("{player}", target.getName()));
        }
        plugin.getAuditLog().log(player.getName(), "FEED", target.getName(), "food");
    }

    private void doTp(Player player, String[] args) {
        if (!plugin.getRoleManager().hasPermission(player, "survivaladmin.tp")) {
            player.sendMessage(plugin.getConfigManager().msg("no-permission"));
            return;
        }
        if (args.length < 1) {
            player.sendMessage(plugin.getConfigManager().msg("usage-tp"));
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(plugin.getConfigManager().msg("player-not-found").replace("{player}", args[0]));
            return;
        }
        player.teleport(target);
        player.sendMessage(plugin.getConfigManager().msg("teleported").replace("{player}", target.getName()));
        plugin.getAuditLog().log(player.getName(), "TP", target.getName(), "teleport");
    }

    private void doGamemode(Player player, String[] args) {
        if (!plugin.getRoleManager().hasPermission(player, "survivaladmin.gamemode")) {
            player.sendMessage(plugin.getConfigManager().msg("no-permission"));
            return;
        }
        if (args.length < 1) {
            player.sendMessage(plugin.getConfigManager().msg("usage-gm"));
            return;
        }
        GameMode gm = null;
        String mode = args[0].toLowerCase();
        if (mode.equals("0") || mode.equals("survival") || mode.equals("s")) gm = GameMode.SURVIVAL;
        else if (mode.equals("1") || mode.equals("creative") || mode.equals("c")) gm = GameMode.CREATIVE;
        else if (mode.equals("2") || mode.equals("adventure") || mode.equals("a")) gm = GameMode.ADVENTURE;
        else if (mode.equals("3") || mode.equals("spectator") || mode.equals("sp")) gm = GameMode.SPECTATOR;

        if (gm == null) {
            player.sendMessage(plugin.getConfigManager().msg("invalid-gamemode"));
            return;
        }
        Player target = player;
        if (args.length > 1) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(plugin.getConfigManager().msg("player-not-found").replace("{player}", args[1]));
                return;
            }
        }
        target.setGameMode(gm);
        target.sendMessage(plugin.getConfigManager().msg("gamemode-changed").replace("{mode}", gm.name()));
        if (!target.equals(player)) {
            player.sendMessage(plugin.getConfigManager().msg("gamemode-changed-other")
                    .replace("{player}", target.getName()).replace("{mode}", gm.name()));
        }
        plugin.getAuditLog().log(player.getName(), "GAMEMODE", target.getName(), gm.name());
    }

    private void doFly(Player player, String[] args) {
        if (!plugin.getRoleManager().hasPermission(player, "survivaladmin.fly")) {
            player.sendMessage(plugin.getConfigManager().msg("no-permission"));
            return;
        }
        Player target = player;
        if (args.length > 0) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(plugin.getConfigManager().msg("player-not-found").replace("{player}", args[0]));
                return;
            }
        }
        boolean newState = !target.getAllowFlight();
        target.setAllowFlight(newState);
        target.setFlying(newState);
        target.sendMessage(newState ? plugin.getConfigManager().msg("fly-on") : plugin.getConfigManager().msg("fly-off"));
        if (!target.equals(player)) {
            player.sendMessage(plugin.getConfigManager().msg("fly-other")
                    .replace("{player}", target.getName()).replace("{state}", newState ? "enabled" : "disabled"));
        }
        plugin.getAuditLog().log(player.getName(), "FLY", target.getName(), newState ? "on" : "off");
    }

    private void doGod(Player player, String[] args) {
        if (!plugin.getRoleManager().hasPermission(player, "survivaladmin.god")) {
            player.sendMessage(plugin.getConfigManager().msg("no-permission"));
            return;
        }
        Player target = player;
        if (args.length > 0) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(plugin.getConfigManager().msg("player-not-found").replace("{player}", args[0]));
                return;
            }
        }
        boolean newState = !target.isInvulnerable();
        target.setInvulnerable(newState);
        target.sendMessage(newState ? plugin.getConfigManager().msg("god-on") : plugin.getConfigManager().msg("god-off"));
        if (!target.equals(player)) {
            player.sendMessage(plugin.getConfigManager().msg("god-other")
                    .replace("{player}", target.getName()).replace("{state}", newState ? "enabled" : "disabled"));
        }
        plugin.getAuditLog().log(player.getName(), "GOD", target.getName(), newState ? "on" : "off");
    }

    private void doTime(Player player, String[] args) {
        if (!plugin.getRoleManager().hasPermission(player, "survivaladmin.time")) {
            player.sendMessage(plugin.getConfigManager().msg("no-permission"));
            return;
        }
        if (args.length < 1) {
            player.sendMessage(plugin.getConfigManager().msg("usage-time"));
            return;
        }
        long time = -1;
        String t = args[0].toLowerCase();
        if (t.equals("day")) time = 1000;
        else if (t.equals("noon")) time = 6000;
        else if (t.equals("night")) time = 13000;
        else if (t.equals("midnight")) time = 18000;
        else {
            try { time = Long.parseLong(args[0]); } catch (NumberFormatException e) { time = -1; }
        }
        if (time < 0) {
            player.sendMessage(plugin.getConfigManager().msg("invalid-time"));
            return;
        }
        player.getWorld().setTime(time);
        player.sendMessage(plugin.getConfigManager().msg("time-set").replace("{time}", args[0]));
        plugin.getAuditLog().log(player.getName(), "TIME", player.getWorld().getName(), args[0]);
    }

    private void doWeather(Player player, String[] args) {
        if (!plugin.getRoleManager().hasPermission(player, "survivaladmin.weather")) {
            player.sendMessage(plugin.getConfigManager().msg("no-permission"));
            return;
        }
        if (args.length < 1) {
            player.sendMessage(plugin.getConfigManager().msg("usage-weather"));
            return;
        }
        String w = args[0].toLowerCase();
        if (w.equals("clear") || w.equals("sun")) {
            player.getWorld().setStorm(false);
            player.getWorld().setThundering(false);
            player.sendMessage(plugin.getConfigManager().msg("weather-set").replace("{weather}", "clear"));
        } else if (w.equals("rain")) {
            player.getWorld().setStorm(true);
            player.getWorld().setThundering(false);
            player.sendMessage(plugin.getConfigManager().msg("weather-set").replace("{weather}", "rain"));
        } else if (w.equals("thunder")) {
            player.getWorld().setStorm(true);
            player.getWorld().setThundering(true);
            player.sendMessage(plugin.getConfigManager().msg("weather-set").replace("{weather}", "thunder"));
        } else {
            player.sendMessage(plugin.getConfigManager().msg("invalid-weather"));
            return;
        }
        plugin.getAuditLog().log(player.getName(), "WEATHER", player.getWorld().getName(), args[0]);
    }

    private void doInvsee(Player player, String[] args) {
        if (!plugin.getRoleManager().hasPermission(player, "survivaladmin.invsee")) {
            player.sendMessage(plugin.getConfigManager().msg("no-permission"));
            return;
        }
        if (args.length < 1) {
            player.sendMessage(plugin.getConfigManager().msg("usage-inv"));
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(plugin.getConfigManager().msg("player-not-found").replace("{player}", args[0]));
            return;
        }
        player.openInventory(target.getInventory());
        player.sendMessage(plugin.getConfigManager().msg("invsee-opened").replace("{player}", target.getName()));
        plugin.getAuditLog().log(player.getName(), "INV", target.getName(), "opened inventory");
    }

    private void doGetoff(Player player, String[] args) {
        if (!plugin.getRoleManager().hasPermission(player, "survivaladmin.ban")) {
            player.sendMessage(plugin.getConfigManager().msg("no-permission"));
            return;
        }
        if (args.length < 2) {
            player.sendMessage(plugin.getConfigManager().msg("usage-getoff"));
            player.sendMessage(plugin.getConfigManager().msg("time-formats"));
            return;
        }
        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);
        String reason = "Banned by admin";
        if (args.length > 2) {
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                if (i > 2) sb.append(" ");
                sb.append(args[i]);
            }
            reason = sb.toString();
        }
        long duration = parseDuration(args[1]);
        if (duration == -1) {
            player.sendMessage(plugin.getConfigManager().msg("invalid-time-format"));
            return;
        }
        org.bukkit.BanList banList = Bukkit.getBanList(org.bukkit.BanList.Type.NAME);
        if (duration == 0) {
            ((org.bukkit.BanList) banList).addBan(targetName, reason, (java.util.Date) null, player.getName());
        } else {
            ((org.bukkit.BanList) banList).addBan(targetName, reason, new Date(System.currentTimeMillis() + duration), player.getName());
        }
        if (target != null) {
            String timeStr = (duration == 0) ? "permanent" : formatDuration(duration);
            target.kickPlayer(plugin.getConfigManager().msg("banned")
                    .replace("{player}", targetName).replace("{time}", timeStr).replace("{reason}", reason));
        }
        String timeStr = (duration == 0) ? "permanent" : formatDuration(duration);
        player.sendMessage(plugin.getConfigManager().msg("banned")
                .replace("{player}", targetName).replace("{time}", timeStr).replace("{reason}", reason));
        plugin.getAuditLog().log(player.getName(), "BAN", targetName, "duration=" + duration + " reason=" + reason);
    }

    private void doVanish(Player player) {
        if (!plugin.getRoleManager().hasPermission(player, "survivaladmin.vanish")) {
            player.sendMessage(plugin.getConfigManager().msg("no-permission"));
            return;
        }
        boolean newState = !player.hasMetadata("survivaladmin_vanished");
        if (newState) {
            player.setMetadata("survivaladmin_vanished", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.equals(player)) {
                    p.hidePlayer(plugin, player);
                }
            }
            player.setPlayerListName("");
            player.sendMessage(plugin.getConfigManager().msg("vanished-on"));
            plugin.getAuditLog().log(player.getName(), "VANISH", player.getName(), "on");
        } else {
            player.removeMetadata("survivaladmin_vanished", plugin);
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.equals(player)) {
                    p.showPlayer(plugin, player);
                }
            }
            player.setPlayerListName(player.getName());
            player.sendMessage(plugin.getConfigManager().msg("vanished-off"));
            plugin.getAuditLog().log(player.getName(), "VANISH", player.getName(), "off");
        }
    }

    private long parseDuration(String input) {
        if (input.equalsIgnoreCase("permanent") || input.equalsIgnoreCase("perm")) {
            return 0;
        }
        if (input.equalsIgnoreCase("0")) {
            return 0;
        }
        long total = 0;
        String num = "";
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isDigit(c)) {
                num += c;
            } else {
                if (num.isEmpty()) return -1;
                long val = Long.parseLong(num);
                char lower = Character.toLowerCase(c);
                if (lower == 's') total += val * 1000;
                else if (lower == 'm') total += val * 60 * 1000;
                else if (lower == 'h') total += val * 60 * 60 * 1000;
                else if (lower == 'd') total += val * 24 * 60 * 60 * 1000;
                else if (lower == 'w') total += val * 7 * 24 * 60 * 60 * 1000;
                else return -1;
                num = "";
            }
        }
        if (!num.isEmpty()) return -1;
        return total > 0 ? total : -1;
    }

    private String formatDuration(long millis) {
        long seconds = millis / 1000;
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (sb.length() == 0) sb.append(seconds).append("s");
        return sb.toString().trim();
    }

    @Override
    public java.util.List<String> onTabComplete(org.bukkit.command.CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        String cmd = command.getName().toLowerCase();
        java.util.List<String> completions = new java.util.ArrayList<>();
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            if (cmd.equals("gm") || cmd.equals("gamemode")) {
                for (String v : new String[]{"0","1","2","3","survival","creative","adventure","spectator"}) {
                    if (v.startsWith(prefix)) completions.add(v);
                }
            } else if (cmd.equals("time")) {
                for (String v : new String[]{"day","night","noon","midnight"}) {
                    if (v.startsWith(prefix)) completions.add(v);
                }
            } else if (cmd.equals("weather")) {
                for (String v : new String[]{"clear","rain","thunder"}) {
                    if (v.startsWith(prefix)) completions.add(v);
                }
            } else {
                for (org.bukkit.entity.Player p : org.bukkit.Bukkit.getOnlinePlayers()) {
                    if (p.getName().toLowerCase().startsWith(prefix)) completions.add(p.getName());
                }
            }
        } else if (args.length == 2 && (cmd.equals("gm") || cmd.equals("gamemode"))) {
            String prefix = args[1].toLowerCase();
            for (org.bukkit.entity.Player p : org.bukkit.Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(prefix)) completions.add(p.getName());
            }
        } else if (args.length == 2 && cmd.equals("getoff")) {
            String prefix = args[1].toLowerCase();
            for (String v : new String[]{"10m","1h","1d","permanent"}) {
                if (v.startsWith(prefix)) completions.add(v);
            }
        } else if (args.length >= 3 && cmd.equals("getoff")) {
            // no completion
        }
        return completions;
    }
}