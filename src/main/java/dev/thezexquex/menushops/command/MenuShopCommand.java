package dev.thezexquex.menushops.command;

import dev.thezexquex.menushops.MenuShopsPlugin;
import dev.thezexquex.menushops.command.core.BaseCommand;
import dev.thezexquex.menushops.shop.ShopItem;
import dev.thezexquex.menushops.shop.gui.MenuShopGui;
import dev.thezexquex.menushops.shop.value.ValueParser;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.spongepowered.configurate.NodePath;

import java.util.concurrent.CompletableFuture;

import static dev.thezexquex.menushops.command.ValueArgumentParser.valueParser;
import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;
import static org.incendo.cloud.parser.standard.StringParser.*;

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
                .required("shop-title", quotedStringParser())
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
                .required("lower-bound", quotedStringParser())
                .required("upper-bound", quotedStringParser())
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
        var shopName = (String) playerCommandContext.get("shop-name");

        var shopOpt = plugin.shopService().getShop(shopName);
        if (shopOpt.isEmpty()) {
            plugin.messenger().sendMessage(player, NodePath.path("command", "menushops", "not-found"));
            return;
        }

        var shop = shopOpt.get();

        MenuShopGui.constructGui(player, shop).open();
    }

    private void handleCreate(CommandContext<Player> playerCommandContext) {
        var shopName = (String) playerCommandContext.get("shop-name");
        var shopTitle = (String) playerCommandContext.get("shop-title");
        var player = playerCommandContext.sender();

        plugin.shopService().createShop(shopName, shopTitle);
        plugin.messenger().sendMessage(player, NodePath.path("command", "menushops", "create", "success"));
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

        var lowerBoundPattern = (String) playerCommandContext.get("lower-bound");
        var upperBoundPattern = (String) playerCommandContext.get("upper-bound");


        var shopOpt = plugin.shopService().getShop(shopName);
        if (shopOpt.isEmpty()) {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "not-found"));
            return;
        }

        var shop = shopOpt.get();

        var lowerBoundValueParserResult = ValueParser.validate(lowerBoundPattern);
        var upperBoundValueParserResult = ValueParser.validate(lowerBoundPattern);

        if (lowerBoundValueParserResult.valueParserResultType() != ValueParser.ValueParserResultType.VALID) {
            sender.sendRichMessage("<red>Error while parsing lower-bound-value: " + lowerBoundValueParserResult.valueParserResultType());
            sender.sendRichMessage("<red>Invalid Pattern: " + ValueArgumentParser.buildErrorMark(lowerBoundPattern, lowerBoundValueParserResult));
            return;
        }

        if (upperBoundValueParserResult.valueParserResultType() != ValueParser.ValueParserResultType.VALID) {
            sender.sendRichMessage("<red>Error while parsing upper-bound-value: " + lowerBoundValueParserResult.valueParserResultType());
            sender.sendRichMessage("<red>Invalid Pattern: " + ValueArgumentParser.buildErrorMark(upperBoundPattern, upperBoundValueParserResult));
            return;
        }

        var lowerBoundValue = ValueParser.fromPattern(lowerBoundPattern);
        var upperBoundValue = ValueParser.fromPattern(upperBoundPattern);

        if (lowerBoundValue.amount() > upperBoundValue.amount()) {
            plugin.messenger().sendMessage(sender, NodePath.path("exception", "parser", "lower-higher-than-upper"));
            return;
        }

        var itemStack = sender.getInventory().getItemInMainHand();
        if (itemStack.getType() == Material.AIR) {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "edit", "add", "item-null"));
            return;
        }

        shop.addItem(new ShopItem(itemStack, lowerBoundValue, upperBoundValue));
        plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "edit", "add", "success"));
    }

    private void handleEditRemoveItem(CommandContext<Player> playerCommandContext) {
        var shopName = (String) playerCommandContext.get("shop-name");
        var sender = playerCommandContext.sender();
        var itemId = (int) playerCommandContext.get("id");

        var shopOpt = plugin.shopService().getShop(shopName);
        if (shopOpt.isEmpty()) {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "not-found"));
            return;
        }

        var shop = shopOpt.get();

        if (itemId > shop.items().size() - 1) {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "edit", "remove", "no-such-item"));
            return;
        }
        shop.removeItem(itemId);

        plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "edit", "remove", "success"));
    }
}
