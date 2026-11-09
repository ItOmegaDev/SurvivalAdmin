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

    public Config(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.exists()) {
            plugin.saveResource("config.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public String get(String path, String def) {
        return config.getString(path, def);
    }

    public List<String> getList(String path) {
        return config.getStringList(path);
    }

    public int getInt(String path, int def) {
        return config.getInt(path, def);
    }

    public boolean getBool(String path, boolean def) {
        return config.getBoolean(path, def);
    }

    public String msg(String key) {
        String m = config.getString("messages." + key, "");
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
