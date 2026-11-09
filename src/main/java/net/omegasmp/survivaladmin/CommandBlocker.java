package net.omegasmp.survivaladmin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.List;

public class CommandBlocker implements Listener {

    private final SurvivalAdmin plugin;

    public CommandBlocker(SurvivalAdmin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCommandSend(PlayerCommandSendEvent event) {
        if (plugin.getRoleManager().isStaff(event.getPlayer())) return;

        List<String> allowed = plugin.getConfig().getStringList("user-commands");
        event.getCommands().removeIf(cmd -> !allowed.contains(cmd.toLowerCase()));
    }
}
