package net.omegasmp.survivaladmin;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * Mute management with optional expiry. Persists to mutes.json.
 */
public class MuteManager {

    private final JavaPlugin plugin;
    private final File file;
    // uuid -> MuteEntry
    private final Map<String, MuteEntry> mutes = new HashMap<>();

    public static class MuteEntry {
        public String name;
        public String reason;
        public String by;
        public long until; // 0 = permanent

        public boolean isActive() {
            return until == 0 || System.currentTimeMillis() < until;
        }
    }

    public MuteManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "mutes.json");
        load();
    }

    private void load() {
        if (!file.exists()) return;
        try {
            String content = new String(Files.readAllBytes(file.toPath())).trim();
            if (!content.startsWith("{") || !content.endsWith("}")) return;
            content = content.substring(1, content.length() - 1);
            // entries: "uuid":{"name":"x","reason":"y","by":"z","until":123}
            int depth = 0;
            StringBuilder entry = new StringBuilder();
            List<String> entries = new ArrayList<>();
            for (char c : content.toCharArray()) {
                if (c == '{') depth++;
                if (c == '}') depth--;
                if (c == ',' && depth == 0) {
                    entries.add(entry.toString());
                    entry.setLength(0);
                } else {
                    entry.append(c);
                }
            }
            if (entry.length() > 0) entries.add(entry.toString());

            for (String e : entries) {
                int colonIdx = e.indexOf(':');
                if (colonIdx < 0) continue;
                String uuid = e.substring(0, colonIdx).replace("\"", "").trim();
                String rest = e.substring(colonIdx + 1).trim();
                if (!rest.startsWith("{") || !rest.endsWith("}")) continue;
                rest = rest.substring(1, rest.length() - 1);
                MuteEntry me = new MuteEntry();
                for (String kv : splitTopLevel(rest)) {
                    String[] parts = kv.split(":", 2);
                    if (parts.length != 2) continue;
                    String k = parts[0].replace("\"", "").trim();
                    String v = parts[1].trim();
                    if (v.startsWith("\"")) v = v.substring(1, v.length() - 1).replace("\\\"", "\"");
                    switch (k) {
                        case "name" -> me.name = v;
                        case "reason" -> me.reason = v;
                        case "by" -> me.by = v;
                        case "until" -> {
                            try { me.until = Long.parseLong(v); } catch (NumberFormatException ignored) { }
                        }
                    }
                }
                if (me.name != null) mutes.put(uuid, me);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Could not load mutes: " + e.getMessage());
        }
        cleanupExpired();
    }

    private List<String> splitTopLevel(String s) {
        List<String> out = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int depth = 0;
        boolean inStr = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '"' && (i == 0 || s.charAt(i - 1) != '\\')) inStr = !inStr;
            if (!inStr) {
                if (c == '{' || c == '[') depth++;
                if (c == '}' || c == ']') depth--;
                if (c == ',' && depth == 0) {
                    out.add(sb.toString());
                    sb.setLength(0);
                    continue;
                }
            }
            sb.append(c);
        }
        if (sb.length() > 0) out.add(sb.toString());
        return out;
    }

    public void save() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, MuteEntry> e : mutes.entrySet()) {
            if (!first) sb.append(",");
            MuteEntry m = e.getValue();
            sb.append("\"").append(e.getKey()).append("\":{");
            sb.append("\"name\":\"").append(escape(m.name)).append("\",");
            sb.append("\"reason\":\"").append(escape(m.reason == null ? "" : m.reason)).append("\",");
            sb.append("\"by\":\"").append(escape(m.by == null ? "" : m.by)).append("\",");
            sb.append("\"until\":").append(m.until);
            sb.append("}");
            first = false;
        }
        sb.append("}");
        try {
            File tmp = new File(file.getParent(), "mutes.json.tmp");
            Files.write(tmp.toPath(), sb.toString().getBytes());
            Files.move(tmp.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (Exception e) {
            plugin.getLogger().warning("Could not save mutes: " + e.getMessage());
        }
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private void cleanupExpired() {
        Iterator<Map.Entry<String, MuteEntry>> it = mutes.entrySet().iterator();
        while (it.hasNext()) {
            if (!it.next().getValue().isActive()) it.remove();
        }
    }

    public void mute(Player target, String reason, long durationMillis, String by) {
        MuteEntry me = new MuteEntry();
        me.name = target.getName();
        me.reason = reason;
        me.by = by;
        me.until = (durationMillis <= 0) ? 0 : System.currentTimeMillis() + durationMillis;
        mutes.put(target.getUniqueId().toString(), me);
        save();
    }

    public void muteByName(String name, String reason, long durationMillis, String by) {
        MuteEntry me = new MuteEntry();
        me.name = name;
        me.reason = reason;
        me.by = by;
        me.until = (durationMillis <= 0) ? 0 : System.currentTimeMillis() + durationMillis;
        // store by name-keyed uuid fallback
        mutes.put("name:" + name.toLowerCase(), me);
        save();
    }

    public void unmute(Player target) {
        mutes.remove(target.getUniqueId().toString());
        mutes.remove("name:" + target.getName().toLowerCase());
        save();
    }

    public void unmuteByName(String name) {
        mutes.remove("name:" + name.toLowerCase());
        // also try to find by name in entries
        Iterator<Map.Entry<String, MuteEntry>> it = mutes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, MuteEntry> e = it.next();
            if (e.getValue().name != null && e.getValue().name.equalsIgnoreCase(name)) {
                it.remove();
            }
        }
        save();
    }

    public boolean isMuted(Player player) {
        cleanupExpired();
        MuteEntry me = mutes.get(player.getUniqueId().toString());
        if (me == null) me = mutes.get("name:" + player.getName().toLowerCase());
        return me != null && me.isActive();
    }

    public MuteEntry getMute(Player player) {
        cleanupExpired();
        MuteEntry me = mutes.get(player.getUniqueId().toString());
        if (me == null) me = mutes.get("name:" + player.getName().toLowerCase());
        return (me != null && me.isActive()) ? me : null;
    }

    public long remainingMillis(Player player) {
        MuteEntry me = getMute(player);
        if (me == null) return 0;
        if (me.until == 0) return -1; // permanent
        return Math.max(0, me.until - System.currentTimeMillis());
    }
}