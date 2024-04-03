package dev.thezexquex.menushops.command.core;

import dev.thezexquex.menushops.MenuShopsPlugin;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.CommandManager;

public abstract class BaseCommand {
    protected final MenuShopsPlugin plugin;

    public BaseCommand(MenuShopsPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract void register(CommandManager<CommandSender> commandManager);
}
