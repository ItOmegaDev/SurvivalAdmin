package net.omegasmp.survivaladmin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Config {

    private final JavaPlugin plugin;
    private final File file;
    private FileConfiguration config;
    private FileConfiguration messages;

    public Config(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.exists()) {
            plugin.saveResource("config.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(file);
        loadMessages();
    }

    private void loadMessages() {
        File mf = new File(plugin.getDataFolder(), "messages.yml");
        if (!mf.exists()) {
            try { plugin.saveResource("messages.yml", false); } catch (Exception ignored) { }
        }
        if (mf.exists()) {
            messages = YamlConfiguration.loadConfiguration(mf);
        } else {
            messages = config;
        }
    }

    public String get(String path, String def) {
        return config.getString(path, def);
    }

    public List<String> getList(String path) {
        return config.getStringList(path);
    }

    public List<String> getPermissions(String role) {
        return config.getStringList("permissions." + role);
    }

    public boolean hasPermission(String role, String perm) {
        if ("owner".equals(role)) return true;
        List<String> perms = getPermissions(role);
        if (perms.contains("*") || perms.contains("survivaladmin.*")) return true;
        return perms.contains(perm);
    }

    public int getInt(String path, int def) {
        return config.getInt(path, def);
    }

    public boolean getBool(String path, boolean def) {
        return config.getBoolean(path, def);
    }

    public String prefix() {
        String p = config.getString("prefix", "&aOmegaSMP");
        return p.replace("&", "§");
    }

    public String msg(String key) {
        String m = "";
        if (messages != null) {
            m = messages.getString(key, "");
        }
        if (m == null || m.isEmpty()) {
            m = config.getString("messages." + key, "");
        }
        if (m == null) m = "";
        String colored = m.replace("&", "§");
        if (colored.isEmpty()) return colored;
        return prefix() + " §7- " + colored;
    }

    public String msgRaw(String key) {
        String m = "";
        if (messages != null) {
            m = messages.getString(key, "");
        }
        if (m == null || m.isEmpty()) {
            m = config.getString("messages." + key, "");
        }
        if (m == null) m = "";
        return m.replace("&", "§");
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save config: " + e.getMessage());
        }
    }

    public FileConfiguration raw() {
        return config;
    }
}