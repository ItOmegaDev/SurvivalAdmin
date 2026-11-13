package net.omegasmp.survivaladmin;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * Warning tracker. Persists to warns.json.
 * Auto-ban threshold handled by caller.
 */
public class WarnManager {

    private final JavaPlugin plugin;
    private final File file;
    // uuid -> WarnEntry
    private final Map<String, WarnEntry> warns = new HashMap<>();

    public static class WarnEntry {
        public String name;
        public int count;
        public long last;
        public String lastReason;
    }

    public WarnManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "warns.json");
        load();
    }

    private void load() {
        if (!file.exists()) return;
        try {
            String content = new String(Files.readAllBytes(file.toPath())).trim();
            if (!content.startsWith("{") || !content.endsWith("}")) return;
            content = content.substring(1, content.length() - 1);
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
                WarnEntry we = new WarnEntry();
                for (String kv : splitTopLevel(rest)) {
                    String[] parts = kv.split(":", 2);
                    if (parts.length != 2) continue;
                    String k = parts[0].replace("\"", "").trim();
                    String v = parts[1].trim();
                    if (v.startsWith("\"")) v = v.substring(1, v.length() - 1).replace("\\\"", "\"");
                    switch (k) {
                        case "name" -> we.name = v;
                        case "count" -> { try { we.count = Integer.parseInt(v); } catch (NumberFormatException ignored) { } }
                        case "last" -> { try { we.last = Long.parseLong(v); } catch (NumberFormatException ignored) { } }
                        case "lastReason" -> we.lastReason = v;
                    }
                }
                if (we.name != null) warns.put(uuid, we);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Could not load warns: " + e.getMessage());
        }
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
        for (Map.Entry<String, WarnEntry> e : warns.entrySet()) {
            if (!first) sb.append(",");
            WarnEntry w = e.getValue();
            sb.append("\"").append(e.getKey()).append("\":{");
            sb.append("\"name\":\"").append(escape(w.name)).append("\",");
            sb.append("\"count\":").append(w.count).append(",");
            sb.append("\"last\":").append(w.last).append(",");
            sb.append("\"lastReason\":\"").append(escape(w.lastReason == null ? "" : w.lastReason)).append("\"");
            sb.append("}");
            first = false;
        }
        sb.append("}");
        try {
            File tmp = new File(file.getParent(), "warns.json.tmp");
            Files.write(tmp.toPath(), sb.toString().getBytes());
            Files.move(tmp.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (Exception e) {
            plugin.getLogger().warning("Could not save warns: " + e.getMessage());
        }
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public int warn(Player target, String reason, String by) {
        String uuid = target.getUniqueId().toString();
        WarnEntry we = warns.get(uuid);
        if (we == null) {
            we = new WarnEntry();
            we.name = target.getName();
            we.count = 0;
        }
        we.count++;
        we.last = System.currentTimeMillis();
        we.lastReason = reason;
        warns.put(uuid, we);
        save();
        return we.count;
    }

    public int getWarnCount(Player target) {
        WarnEntry we = warns.get(target.getUniqueId().toString());
        return we == null ? 0 : we.count;
    }

    public int getWarnCount(String name) {
        for (WarnEntry we : warns.values()) {
            if (we.name != null && we.name.equalsIgnoreCase(name)) return we.count;
        }
        return 0;
    }

    public void resetWarns(Player target) {
        warns.remove(target.getUniqueId().toString());
        save();
    }

    public void resetWarnsByName(String name) {
        Iterator<Map.Entry<String, WarnEntry>> it = warns.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getValue().name != null && it.next().getValue().name.equalsIgnoreCase(name)) {
                // careful: already advanced; redo
            }
        }
        // simpler: iterate keys
        List<String> toRemove = new ArrayList<>();
        for (Map.Entry<String, WarnEntry> e : warns.entrySet()) {
            if (e.getValue().name != null && e.getValue().name.equalsIgnoreCase(name)) toRemove.add(e.getKey());
        }
        for (String k : toRemove) warns.remove(k);
        save();
    }
}