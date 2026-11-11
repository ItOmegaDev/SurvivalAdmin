package net.omegasmp.survivaladmin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * SurvivalAdmin — Professional administration plugin for Minecraft Survival servers.
 *
 * Features:
 * - Role-based access control (user, staff, owner) with permission inheritance
 * - UUID-based role storage (immune to name changes)
- Atomic file writes (crash-safe)
- Granular permissions (survivaladmin.kick, survivaladmin.ban, etc.)
- Namespace/alias bypass prevention
- Command blocking for regular users
- Custom messages (join, quit, death, kill)
- Admin commands: heal, feed, tp, gamemode, time, weather, fly, god, invsee
- Audit log for all admin actions
- YAML config
 *
 * Made by Omega (@ItOmega)
 * Version: 2.0.0
 */
public final class SurvivalAdmin extends JavaPlugin {

    private static SurvivalAdmin instance;
    private RoleManager roleManager;
    private CommandBlocker commandBlocker;
    private Config config;
    private AuditLog auditLog;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        this.config = new Config(this);
        this.roleManager = new RoleManager(this);
        this.commandBlocker = new CommandBlocker(this);
        this.auditLog = new AuditLog(this);

        Bukkit.getPluginManager().registerEvents(new EventListener(this), this);
        Bukkit.getPluginManager().registerEvents(this.commandBlocker, this);

        new CommandRegistrar(this).registerAll();

        getLogger().info("SurvivalAdmin v2.0.0 enabled — Made by Omega (@ItOmega)");
    }

    @Override
    public void onDisable() {
        if (this.roleManager != null) this.roleManager.save();
        if (this.auditLog != null) this.auditLog.close();
        getLogger().info("SurvivalAdmin disabled.");
    }

    public static SurvivalAdmin getInstance() { return instance; }
    public RoleManager getRoleManager() { return roleManager; }
    public CommandBlocker getCommandBlocker() { return commandBlocker; }
    public Config getConfigManager() { return config; }
    public AuditLog getAuditLog() { return auditLog; }
}
