package dev.thezexquex.menushops.command;

import dev.thezexquex.menushops.MenuShopsPlugin;
import dev.thezexquex.menushops.command.core.BaseCommand;
import dev.thezexquex.menushops.shop.gui.MenuShopGui;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.spongepowered.configurate.NodePath;

import java.util.concurrent.CompletableFuture;

import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;

public class MenuShopCommand extends BaseCommand {
    public MenuShopCommand(MenuShopsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void register(CommandManager<CommandSender> commandManager) {
        commandManager.command(commandManager.commandBuilder("menushop")
                .permission("menushops.command.menushops.open")
                .literal("open")
                .senderType(Player.class)
                .required("shop-name", stringParser(), (context, input) ->
                        CompletableFuture.supplyAsync(() -> plugin.shopService().loadedShopNames().stream().map(Suggestion::simple).toList()))
                .handler(this::handleOpen)
        );

        commandManager.command(commandManager.commandBuilder("menushop")
                .permission("menushops.command.menushops.create")
                .literal("create")
                .senderType(Player.class)
                .required("shop-name", stringParser())
                .handler(this::handleCreate)
        );

        commandManager.command(commandManager.commandBuilder("menushop")
                .permission("menushops.command.menushops.delete")
                .literal("delete")
                .required("shop-name", stringParser(), (context, input) ->
                        CompletableFuture.supplyAsync(() -> plugin.shopService().loadedShopNames().stream().map(Suggestion::simple).toList()))
                .handler(this::handleDelete)
        );

        commandManager.command(commandManager.commandBuilder("menushop")
                .senderType(Player.class)
                .permission("menushops.command.menushops.edit.additem")
                .literal("edit")
                .literal("additem")
                .required("shop-name", stringParser(), (context, input) ->
                        CompletableFuture.supplyAsync(() -> plugin.shopService().loadedShopNames().stream().map(Suggestion::simple).toList()))
                .required("lower-bound", stringParser())
                .required("upper-bound", stringParser())
                .handler(this::handleEditAddItem)
        );

        commandManager.command(commandManager.commandBuilder("menushop")
                .senderType(Player.class)
                .literal("edit")
                .literal("removeitem")
                .required("shop-name", stringParser(), (context, input) ->
                        CompletableFuture.supplyAsync(() -> plugin.shopService().loadedShopNames().stream().map(Suggestion::simple).toList()))
                .required("id", integerParser())
                .handler(this::handleEditRemoveItem)

        );
    }

    private void handleOpen(CommandContext<Player> playerCommandContext) {
        var player = playerCommandContext.sender();

        MenuShopGui.constructGui(player).open();
    }

    private void handleCreate(CommandContext<Player> playerCommandContext) {
        var shopName = (String) playerCommandContext.get("shop-name");
        var player = playerCommandContext.sender();

        plugin.shopService().createShop(shopName);
        plugin.messenger().sendMessage(player, NodePath.path("command", "menushops", "delete", "error"));
    }

    private void handleDelete(CommandContext<CommandSender> commandSenderCommandContext) {
        var shopName = (String) commandSenderCommandContext.get("shop-name");
        var sender = commandSenderCommandContext.sender();

        if (plugin.shopService().deleteShop(shopName)) {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "delete", "success"));
            return;
        }
        plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "delete", "error"));
    }

    private void handleEditAddItem(CommandContext<Player> playerCommandContext) {
        var shopName = (String) playerCommandContext.get("shop-name");
        var sender = playerCommandContext.sender();

        var shopOpt = plugin.shopService().getShop(shopName);
        if (shopOpt.isEmpty()) {

        }
    }

    private void handleEditRemoveItem(CommandContext<Player> playerCommandContext) {
        var shopName = (String) playerCommandContext.get("shop-name");
        var sender = playerCommandContext.sender();

        var shopOpt = plugin.shopService().getShop(shopName);

    }
}
