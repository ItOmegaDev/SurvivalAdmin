package net.omegasmp.survivaladmin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * SurvivalAdmin — Professional administration plugin for Survival servers.
 * Made by Omega (@ItOmega)
 * Version: 1.0.0
 */
public final class SurvivalAdmin extends JavaPlugin {

    private static SurvivalAdmin instance;
    private RoleManager roleManager;
    private CommandBlocker commandBlocker;
    private Config config;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        this.config = new Config(this);
        this.roleManager = new RoleManager(this);
        this.commandBlocker = new CommandBlocker(this);

        // Register event listeners
        Bukkit.getPluginManager().registerEvents(new EventListener(this), this);
        Bukkit.getPluginManager().registerEvents(this.commandBlocker, this);

        // Register commands
        new CommandRegistrar(this).registerAll();

        getLogger().info("SurvivalAdmin v1.1.0 enabled — Made by Omega (@ItOmega)");
    }

    @Override
    public void onDisable() {
        if (this.roleManager != null) {
            this.roleManager.save();
        }
        getLogger().info("SurvivalAdmin disabled.");
    }

    public static SurvivalAdmin getInstance() {
        return instance;
    }

    public RoleManager getRoleManager() {
        return roleManager;
    }

    public CommandBlocker getCommandBlocker() {
        return commandBlocker;
    }

    public Config getConfigManager() {
        return config;
    }
}
