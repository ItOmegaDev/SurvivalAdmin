package net.omegasmp.survivaladmin;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Audit log for admin actions. Writes to plugins/SurvivalAdmin/audit.log
 */
public class AuditLog {

    private final JavaPlugin plugin;
    private final File file;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private FileWriter writer;

    public AuditLog(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "audit.log");
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            this.writer = new FileWriter(file, true);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not create audit log: " + e.getMessage());
        }
    }

    public void log(String actor, String action, String target, String details) {
        if (writer == null) return;
        String timestamp = dateFormat.format(new Date());
        String entry = String.format("[%s] %s | %s | %s | %s", timestamp, actor, action, target, details);
        try {
            writer.write(entry + "\n");
            writer.flush();
        } catch (IOException e) {
            plugin.getLogger().warning("Could not write to audit log: " + e.getMessage());
        }
        plugin.getLogger().info("[Audit] " + entry);
    }

    public void close() {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                plugin.getLogger().warning("Could not close audit log: " + e.getMessage());
            }
        }
    }
}
