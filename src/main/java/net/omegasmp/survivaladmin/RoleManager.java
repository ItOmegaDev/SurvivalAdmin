package net.omegasmp.survivaladmin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RoleManager {

    private final JavaPlugin plugin;
    private final File file;
    private final Map<String, String> roles = new HashMap<>(); // player name -> role

    public RoleManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "roles.json");
        load();
    }

    private void load() {
        if (!file.exists()) return;
        try {
            String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
            // Simple JSON parse: {"player":"role",...}
            content = content.trim();
            if (content.startsWith("{") && content.endsWith("}")) {
                content = content.substring(1, content.length() - 1);
                for (String entry : content.split(",")) {
                    String[] kv = entry.split(":");
                    if (kv.length == 2) {
                        String k = kv[0].replace("\"", "").trim();
                        String v = kv[1].replace("\"", "").trim();
                        roles.put(k, v);
                    }
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Could not load roles: " + e.getMessage());
        }
    }

    public void save() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> e : roles.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(e.getKey()).append("\":\"").append(e.getValue()).append("\"");
            first = false;
        }
        sb.append("}");
        try {
            java.nio.file.Files.write(file.toPath(), sb.toString().getBytes());
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save roles: " + e.getMessage());
        }
    }

    public String getRole(Player player) {
        String name = player.getName().toLowerCase();
        if (roles.containsKey(name)) return roles.get(name);
        // Check config owners/staff
        List<String> owners = plugin.getConfig().getStringList("roles.owner");
        List<String> staff = plugin.getConfig().getStringList("roles.staff");
        if (owners.contains(name)) return "owner";
        if (staff.contains(name)) return "staff";
        return plugin.getConfig().getString("roles.default", "user");
    }

    public String getRole(String name) {
        String lower = name.toLowerCase();
        if (roles.containsKey(lower)) return roles.get(lower);
        List<String> owners = plugin.getConfig().getStringList("roles.owner");
        List<String> staff = plugin.getConfig().getStringList("roles.staff");
        if (owners.contains(lower)) return "owner";
        if (staff.contains(lower)) return "staff";
        return plugin.getConfig().getString("roles.default", "user");
    }

    public void setRole(String player, String role) {
        roles.put(player.toLowerCase(), role);
        save();
    }

    public void removeRole(String player) {
        roles.remove(player.toLowerCase());
        save();
    }

    public boolean isOwner(Player p) {
        return getRole(p).equals("owner");
    }

    public boolean isStaff(Player p) {
        String r = getRole(p);
        return r.equals("staff") || r.equals("owner");
    }

    public boolean isUser(Player p) {
        return getRole(p).equals("user");
    }
}
