package net.omegasmp.survivaladmin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class CommandRegistrar {

    private final SurvivalAdmin plugin;

    public CommandRegistrar(SurvivalAdmin plugin) {
        this.plugin = plugin;
    }

    public void registerAll() {
        // Sit command - available to all
        register("sit", new SitCommand(plugin));

        // Role management commands - owner only
        register("staff", new RoleCommand(plugin, "staff"));
        register("unstaff", new RoleCommand(plugin, "unstaff"));
        register("owner", new RoleCommand(plugin, "owner"));
        register("unowner", new RoleCommand(plugin, "unowner"));

        // Admin commands
        register("admin", new AdminCommand(plugin));
    }

    private void register(String name, CommandExecutor executor) {
        org.bukkit.command.PluginCommand cmd = plugin.getCommand(name);
        if (cmd != null) {
            cmd.setExecutor(executor);
            if (executor instanceof TabCompleter) {
                cmd.setTabCompleter((TabCompleter) executor);
            }
        }
    }
}
