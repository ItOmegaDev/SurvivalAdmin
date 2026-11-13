package net.omegasmp.survivaladmin;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

        switch (cmd) {
            case "tp" -> doTp(player, args);
            case "gm", "gamemode" -> doGamemode(player, args);
            case "time" -> doTime(player, args);
            case "weather" -> doWeather(player, args);
            case "invsee", "inv" -> doInvsee(player, args);
            case "getoff" -> doGetoff(player, args);
            case "invis", "vanish" -> doVanish(player);
            case "kick" -> doKick(player, args);
            case "mute" -> doMute(player, args);
            case "unmute" -> doUnmute(player, args);
            case "freeze" -> doFreeze(player, args);
            case "unfreeze" -> doUnfreeze(player, args);
            case "clearchat", "cc" -> doClearChat(player);
            case "broadcast", "bc" -> doBroadcast(player, args);
            case "warn" -> doWarn(player, args);
        }

        return true;
    }

    // ---- tp ----
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
    }

    // ---- gamemode ----
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
    }

    // ---- time ----
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
    }

    // ---- weather ----
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
    }

    // ---- invsee ----
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
    }

    // ---- getoff (ban) ----
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
            banList.addBan(targetName, reason, (java.util.Date) null, player.getName());
        } else {
            banList.addBan(targetName, reason, new Date(System.currentTimeMillis() + duration), player.getName());
        }
        if (target != null) {
            String timeStr = (duration == 0) ? "permanent" : formatDuration(duration);
            target.kickPlayer(plugin.getConfigManager().msg("banned")
                    .replace("{player}", targetName).replace("{time}", timeStr).replace("{reason}", reason));
        }
        String timeStr = (duration == 0) ? "permanent" : formatDuration(duration);
        player.sendMessage(plugin.getConfigManager().msg("banned")
                .replace("{player}", targetName).replace("{time}", timeStr).replace("{reason}", reason));
    }

    // ---- vanish ----
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
        } else {
            player.removeMetadata("survivaladmin_vanished", plugin);
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.equals(player)) {
                    p.showPlayer(plugin, player);
                }
            }
            player.setPlayerListName(player.getName());
            player.sendMessage(plugin.getConfigManager().msg("vanished-off"));
        }
    }

    // ---- kick ----
    private void doKick(Player player, String[] args) {
        if (!plugin.getRoleManager().hasPermission(player, "survivaladmin.kick")) {
            player.sendMessage(plugin.getConfigManager().msg("no-permission"));
            return;
        }
        if (args.length < 1) {
            player.sendMessage(plugin.getConfigManager().msg("usage-kick"));
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(plugin.getConfigManager().msg("player-not-found").replace("{player}", args[0]));
            return;
        }
        String reason = "Kicked by admin";
        if (args.length > 1) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                if (i > 1) sb.append(" ");
                sb.append(args[i]);
            }
            reason = sb.toString();
        }
        target.kickPlayer(plugin.getConfigManager().msg("kicked")
                .replace("{player}", target.getName()).replace("{reason}", reason));
        player.sendMessage(plugin.getConfigManager().msg("kick-success")
                .replace("{player}", target.getName()).replace("{reason}", reason));
        Bukkit.broadcastMessage(plugin.getConfigManager().msg("kick-broadcast")
                .replace("{player}", target.getName()).replace("{admin}", player.getName()));
    }

    // ---- mute ----
    private void doMute(Player player, String[] args) {
        if (!plugin.getRoleManager().hasPermission(player, "survivaladmin.mute")) {
            player.sendMessage(plugin.getConfigManager().msg("no-permission"));
            return;
        }
        if (args.length < 1) {
            player.sendMessage(plugin.getConfigManager().msg("usage-mute"));
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(plugin.getConfigManager().msg("player-not-found").replace("{player}", args[0]));
            return;
        }
        String reason = "Muted by admin";
        long duration = 0; // 0 = permanent by default? use config default
        // parse optional time + reason
        int idx = 1;
        if (args.length > 1) {
            long parsed = parseDuration(args[1]);
            if (parsed != -1) {
                duration = parsed;
                idx = 2;
            }
        }
        if (duration == 0 && args.length > 1 && parseDuration(args[1]) == -1) {
            // no time given -> use config default
            String def = plugin.getConfigManager().get("moderation.mute.default-duration", "10m");
            duration = parseDuration(def);
            if (duration == -1) duration = 10 * 60 * 1000L;
        }
        if (args.length > idx) {
            StringBuilder sb = new StringBuilder();
            for (int i = idx; i < args.length; i++) {
                if (i > idx) sb.append(" ");
                sb.append(args[i]);
            }
            reason = sb.toString();
        }
        plugin.getMuteManager().mute(target, reason, duration, player.getName());
        String timeStr = (duration <= 0) ? "permanent" : formatDuration(duration);
        target.sendMessage(plugin.getConfigManager().msg("muted-target")
                .replace("{time}", timeStr).replace("{reason}", reason));
        player.sendMessage(plugin.getConfigManager().msg("mute-success")
                .replace("{player}", target.getName()).replace("{time}", timeStr));
    }

    // ---- unmute ----
    private void doUnmute(Player player, String[] args) {
        if (!plugin.getRoleManager().hasPermission(player, "survivaladmin.unmute")) {
            player.sendMessage(plugin.getConfigManager().msg("no-permission"));
            return;
        }
        if (args.length < 1) {
            player.sendMessage(plugin.getConfigManager().msg("usage-unmute"));
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target != null) {
            plugin.getMuteManager().unmute(target);
        } else {
            plugin.getMuteManager().unmuteByName(args[0]);
        }
        player.sendMessage(plugin.getConfigManager().msg("unmute-success").replace("{player}", args[0]));
    }

    // ---- freeze ----
    private void doFreeze(Player player, String[] args) {
        if (!plugin.getRoleManager().hasPermission(player, "survivaladmin.freeze")) {
            player.sendMessage(plugin.getConfigManager().msg("no-permission"));
            return;
        }
        if (args.length < 1) {
            player.sendMessage(plugin.getConfigManager().msg("usage-freeze"));
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(plugin.getConfigManager().msg("player-not-found").replace("{player}", args[0]));
            return;
        }
        if (target.equals(player)) {
            player.sendMessage(plugin.getConfigManager().msg("cannot-self"));
            return;
        }
        plugin.setFrozen(target.getUniqueId(), true);
        target.sendMessage(plugin.getConfigManager().msg("frozen-target"));
        player.sendMessage(plugin.getConfigManager().msg("freeze-success").replace("{player}", target.getName()));
    }

    // ---- unfreeze ----
    private void doUnfreeze(Player player, String[] args) {
        if (!plugin.getRoleManager().hasPermission(player, "survivaladmin.freeze")) {
            player.sendMessage(plugin.getConfigManager().msg("no-permission"));
            return;
        }
        if (args.length < 1) {
            player.sendMessage(plugin.getConfigManager().msg("usage-unfreeze"));
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(plugin.getConfigManager().msg("player-not-found").replace("{player}", args[0]));
            return;
        }
        plugin.setFrozen(target.getUniqueId(), false);
        target.sendMessage(plugin.getConfigManager().msg("unfrozen-target"));
        player.sendMessage(plugin.getConfigManager().msg("unfreeze-success").replace("{player}", target.getName()));
    }

    // ---- clearchat ----
    private void doClearChat(Player player) {
        if (!plugin.getRoleManager().hasPermission(player, "survivaladmin.clearchat")) {
            player.sendMessage(plugin.getConfigManager().msg("no-permission"));
            return;
        }
        int lines = plugin.getConfigManager().getInt("moderation.clearchat.lines", 100);
        for (int i = 0; i < lines; i++) {
            Bukkit.broadcastMessage("");
        }
        Bukkit.broadcastMessage(plugin.getConfigManager().msg("clearchat-done").replace("{admin}", player.getName()));
    }

    // ---- broadcast ----
    private void doBroadcast(Player player, String[] args) {
        if (!plugin.getRoleManager().hasPermission(player, "survivaladmin.broadcast")) {
            player.sendMessage(plugin.getConfigManager().msg("no-permission"));
            return;
        }
        if (args.length < 1) {
            player.sendMessage(plugin.getConfigManager().msg("usage-broadcast"));
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(" ");
            sb.append(args[i]);
        }
        String msg = sb.toString().replace("&", "§");
        String prefix = plugin.getConfigManager().get("moderation.broadcast.prefix", "&6[Broadcast] ");
        Bukkit.broadcastMessage(prefix.replace("&", "§") + msg);
    }

    // ---- warn ----
    private void doWarn(Player player, String[] args) {
        if (!plugin.getRoleManager().hasPermission(player, "survivaladmin.warn")) {
            player.sendMessage(plugin.getConfigManager().msg("no-permission"));
            return;
        }
        if (args.length < 2) {
            player.sendMessage(plugin.getConfigManager().msg("usage-warn"));
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(plugin.getConfigManager().msg("player-not-found").replace("{player}", args[0]));
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (i > 1) sb.append(" ");
            sb.append(args[i]);
        }
        String reason = sb.toString();
        int count = plugin.getWarnManager().warn(target, reason, player.getName());
        int threshold = plugin.getConfigManager().getInt("moderation.warn.auto-ban-threshold", 3);
        target.sendMessage(plugin.getConfigManager().msg("warned-target")
                .replace("{count}", String.valueOf(count)).replace("{reason}", reason));
        player.sendMessage(plugin.getConfigManager().msg("warn-success")
                .replace("{player}", target.getName()).replace("{count}", String.valueOf(count)));
        Bukkit.broadcastMessage(plugin.getConfigManager().msg("warn-broadcast")
                .replace("{player}", target.getName()).replace("{admin}", player.getName()));

        if (threshold > 0 && count >= threshold) {
            long dur = parseDuration(plugin.getConfigManager().get("moderation.warn.auto-ban-duration", "1d"));
            if (dur <= 0) dur = 24 * 60 * 60 * 1000L;
            org.bukkit.BanList banList = Bukkit.getBanList(org.bukkit.BanList.Type.NAME);
            banList.addBan(target.getName(), "Auto-ban: " + count + " warnings", new Date(System.currentTimeMillis() + dur), "Console");
            target.kickPlayer(plugin.getConfigManager().msg("auto-banned").replace("{count}", String.valueOf(count)));
            Bukkit.broadcastMessage(plugin.getConfigManager().msg("auto-ban-broadcast")
                    .replace("{player}", target.getName()).replace("{count}", String.valueOf(count)));
        }
    }

    // ---- helpers ----
    private long parseDuration(String input) {
        if (input == null) return -1;
        if (input.equalsIgnoreCase("permanent") || input.equalsIgnoreCase("perm")) return 0;
        if (input.equalsIgnoreCase("0")) return 0;
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
            } else if (cmd.equals("mute")) {
                for (String v : new String[]{"10m","1h","1d","permanent"}) {
                    // not a player, skip
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
        } else if (args.length == 2 && cmd.equals("mute")) {
            String prefix = args[1].toLowerCase();
            for (String v : new String[]{"10m","1h","1d","permanent"}) {
                if (v.startsWith(prefix)) completions.add(v);
            }
        }
        return completions;
    }
}