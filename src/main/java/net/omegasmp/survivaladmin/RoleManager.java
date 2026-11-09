package net.omegasmp.survivaladmin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * Role storage keyed by UUID (not player name).
 * Stores a mapping UUID -> role, plus a name cache for lookups.
 * All writes are atomic (temp file + move).
 */
public class RoleManager {

    private final JavaPlugin plugin;
    private final File file;
    // UUID string -> role
    private final Map<String, String> roles = new HashMap<>();
    // UUID string -> last known name (for lookup by name)
    private final Map<String, String> nameCache = new HashMap<>();

    public RoleManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "roles.json");
        load();
    }

    private void load() {
        if (!file.exists()) return;
        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            // Parse: {"uuid":{"role":"staff","name":"PlayerName"},...}
            content = content.trim();
            if (content.startsWith("{") && content.endsWith("}")) {
                content = content.substring(1, content.length() - 1);
                // Simple parser for nested objects
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
                    // e = "uuid":{"role":"x","name":"y"}
                    int colonIdx = e.indexOf(':');
                    if (colonIdx < 0) continue;
                    String uuid = e.substring(0, colonIdx).replace("\"", "").trim();
                    String rest = e.substring(colonIdx + 1).trim();
                    if (rest.startsWith("{") && rest.endsWith("}")) {
                        rest = rest.substring(1, rest.length() - 1);
                        String role = null, name = null;
                        for (String kv : rest.split(",")) {
                            String[] parts = kv.split(":", 2);
                            if (parts.length == 2) {
                                String k = parts[0].replace("\"", "").trim();
                                String v = parts[1].replace("\"", "").trim();
                                if (k.equals("role")) role = v;
                                if (k.equals("name")) name = v;
                            }
                        }
                        if (role != null) {
                            roles.put(uuid, role);
                            if (name != null) nameCache.put(uuid, name);
                        }
                    } else {
                        // Legacy format: "uuid":"role"
                        roles.put(uuid, rest.replace("\"", "").trim());
                    }
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Could not load roles: " + e.getMessage());
            // Backup corrupt file
            try {
                Files.copy(file.toPath(), new File(file.getParent(), "roles.json.corrupt").toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception ignored) { }
        }
    }

    public void save() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> e : roles.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(e.getKey()).append("\":{\"role\":\"").append(e.getValue()).append("\"");
            String name = nameCache.get(e.getKey());
            if (name != null) {
                sb.append(",\"name\":\"").append(name.replace("\"", "\\\"")).append("\"");
            }
            sb.append("}");
            first = false;
        }
        sb.append("}");
        // Atomic write
        try {
            File tmp = new File(file.getParent(), "roles.json.tmp");
            Files.write(tmp.toPath(), sb.toString().getBytes());
            Files.move(tmp.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (Exception e) {
            plugin.getLogger().warning("Could not save roles: " + e.getMessage());
        }
    }

    public String getRole(Player player) {
        if (player == null) return plugin.getConfig().getString("roles.default", "user");
        String uuid = player.getUniqueId().toString();
        // Update name cache
        nameCache.put(uuid, player.getName());
        if (roles.containsKey(uuid)) return roles.get(uuid);
        // Check config owners/staff by name
        List<String> owners = plugin.getConfig().getStringList("roles.owner");
        List<String> staff = plugin.getConfig().getStringList("roles.staff");
        String name = player.getName().toLowerCase();
        if (owners.contains(name)) return "owner";
        if (staff.contains(name)) return "staff";
        return plugin.getConfig().getString("roles.default", "user");
    }

    public String getRole(String name) {
        if (name == null) return plugin.getConfig().getString("roles.default", "user");
        String lower = name.toLowerCase();
        // Search by name in cache
        for (Map.Entry<String, String> e : nameCache.entrySet()) {
            if (e.getValue().equalsIgnoreCase(lower)) {
                return roles.getOrDefault(e.getKey(), plugin.getConfig().getString("roles.default", "user"));
            }
        }
        // Check config
        List<String> owners = plugin.getConfig().getStringList("roles.owner");
        List<String> staff = plugin.getConfig().getStringList("roles.staff");
        if (owners.contains(lower)) return "owner";
        if (staff.contains(lower)) return "staff";
        return plugin.getConfig().getString("roles.default", "user");
    }

    public void setRole(String playerName, String role) {
        // Find UUID by name
        String uuid = findUuidByName(playerName);
        if (uuid != null) {
            roles.put(uuid, role);
            nameCache.put(uuid, playerName);
        } else {
            // Store by name as fallback (will be migrated on next login)
            roles.put("name:" + playerName.toLowerCase(), role);
            nameCache.put("name:" + playerName.toLowerCase(), playerName);
        }
        save();
    }

    public void setRole(Player player, String role) {
        if (player == null) return;
        String uuid = player.getUniqueId().toString();
        roles.put(uuid, role);
        nameCache.put(uuid, player.getName());
        save();
    }

    public void removeRole(String playerName) {
        String uuid = findUuidByName(playerName);
        if (uuid != null) {
            roles.remove(uuid);
            nameCache.remove(uuid);
        }
        roles.remove("name:" + playerName.toLowerCase());
        nameCache.remove("name:" + playerName.toLowerCase());
        save();
    }

    public void removeRole(Player player) {
        if (player == null) return;
        String uuid = player.getUniqueId().toString();
        roles.remove(uuid);
        nameCache.remove(uuid);
        save();
    }

    private String findUuidByName(String name) {
        if (name == null) return null;
        String lower = name.toLowerCase();
        for (Map.Entry<String, String> e : nameCache.entrySet()) {
            if (e.getValue().equalsIgnoreCase(lower)) {
                return e.getKey();
            }
        }
        // Check online players
        Player p = plugin.getServer().getPlayerExact(name);
        if (p != null) return p.getUniqueId().toString();
        return null;
    }

    public boolean isOwner(Player p) {
        return "owner".equals(getRole(p));
    }

    public boolean isStaff(Player p) {
        String r = getRole(p);
        return "staff".equals(r) || "owner".equals(r);
    }

    public boolean isUser(Player p) {
        return "user".equals(getRole(p));
    }

    public boolean hasPermission(Player p, String perm) {
        if (p == null) return false;
        String role = getRole(p);
        if ("owner".equals(role)) return true;
        return ((SurvivalAdmin) plugin).getConfigManager().hasPermission(role, perm);
    }

    // Migrate legacy name-based roles on player join
    public void migrateOnJoin(Player player) {
        String nameKey = "name:" + player.getName().toLowerCase();
        if (roles.containsKey(nameKey)) {
            String role = roles.remove(nameKey);
            nameCache.remove(nameKey);
            roles.put(player.getUniqueId().toString(), role);
            nameCache.put(player.getUniqueId().toString(), player.getName());
            save();
            plugin.getLogger().info("Migrated role for " + player.getName() + " to UUID-based storage.");
        }
    }
}
