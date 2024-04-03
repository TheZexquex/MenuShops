package dev.thezexquex.menushops.command;

import dev.thezexquex.menushops.MenuShopsPlugin;
import dev.thezexquex.menushops.command.core.BaseCommand;
import dev.thezexquex.menushops.shop.gui.MenuShopGui;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;

public class MenuShopCommand extends BaseCommand {
    public MenuShopCommand(MenuShopsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void register(CommandManager<CommandSender> commandManager) {
        commandManager.command(commandManager.commandBuilder("menushop")
                .senderType(Player.class)
                .handler(this::handle)
        );
    }

    private void handle(CommandContext<Player> playerCommandContext) {
        var player = playerCommandContext.sender();

        MenuShopGui.constructGui(player).open();
    }
}
