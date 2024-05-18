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
import static org.incendo.cloud.parser.standard.EnumParser.enumParser;
import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;
import static org.incendo.cloud.parser.standard.StringParser.*;

public class MenuShopCommand extends BaseCommand {
    public MenuShopCommand(MenuShopsPlugin plugin) {
        super(plugin);
    }

    private enum EditType {
        BUY, SELL
    }

    @Override
    public void register(CommandManager<CommandSender> commandManager) {
        commandManager.command(commandManager.commandBuilder("menushops")
                .permission("menushops.command.menushops.open")
                .literal("open")
                .senderType(Player.class)
                .required("shop-name", stringParser(), (context, input) ->
                        CompletableFuture.supplyAsync(() -> plugin.shopService().loadedShopNames().stream().map(Suggestion::suggestion).toList()))
                .handler(this::handleOpen)
        );

        commandManager.command(commandManager.commandBuilder("menushops")
                .permission("menushops.command.menushops.create")
                .literal("create")
                .senderType(Player.class)
                .required("shop-name", stringParser())
                .required("shop-title", quotedStringParser())
                .handler(this::handleCreate)
        );

        commandManager.command(commandManager.commandBuilder("menushops")
                .permission("menushops.command.menushops.delete")
                .literal("delete")
                .required("shop-name", stringParser(), (context, input) ->
                        CompletableFuture.supplyAsync(() -> plugin.shopService().loadedShopNames().stream().map(Suggestion::suggestion).toList()))
                .handler(this::handleDelete)
        );

        commandManager.command(commandManager.commandBuilder("menushops")
                .senderType(Player.class)
                .permission("menushops.command.menushops.edit.additem")
                .literal("edit")
                .required("shop-name", stringParser(), (context, input) ->
                        CompletableFuture.supplyAsync(() -> plugin.shopService().loadedShopNames().stream().map(Suggestion::suggestion).toList()))
                .literal("additem")
                .required("type", enumParser(EditType.class))
                .required("lower-bound", quotedStringParser())
                .required("upper-bound", quotedStringParser())
                .handler(this::handleEditAddItem)
        );

        commandManager.command(commandManager.commandBuilder("menushops")
                .senderType(Player.class)
                .literal("edit")
                .permission("menushops.command.menushops.edit.removeitem")
                .required("shop-name", stringParser(), (context, input) ->
                        CompletableFuture.supplyAsync(() -> plugin.shopService().loadedShopNames().stream().map(Suggestion::suggestion).toList()))
                .literal("removeitem")
                .required("type", enumParser(EditType.class))
                .required("id", integerParser())
                .handler(this::handleEditRemoveItem)

        );

        commandManager.command(commandManager.commandBuilder("menushops")
                .literal("reload")
                .permission("menushops.command.menushops.reload")
                .handler(this::handleReload)

        );
    }

    private void handleReload(CommandContext<CommandSender> commandSenderCommandContext) {
        plugin.messenger().sendMessage(commandSenderCommandContext.sender(),
                NodePath.path("command", "menushops", "reload", "attempt"));
        plugin.reload();
        plugin.messenger().sendMessage(commandSenderCommandContext.sender(),
                NodePath.path("command", "menushops", "reload", "success"));
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

        MenuShopGui.constructGui(player, shop, plugin.messenger()).open();
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

        var editType = (EditType) playerCommandContext.get("type");

        var shopOpt = plugin.shopService().getShop(shopName);
        if (shopOpt.isEmpty()) {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "not-found"));
            return;
        }

        var shop = shopOpt.get();

        var lowerBoundValueParserResult = ValueParser.validate(lowerBoundPattern);
        var upperBoundValueParserResult = ValueParser.validate(upperBoundPattern);

        if (lowerBoundValueParserResult.valueParserResultType() != ValueParser.ValueParserResultType.VALID) {
            sender.sendRichMessage("<red>Error while parsing lower-bound-value: " + lowerBoundValueParserResult.valueParserResultType());
            sender.sendRichMessage("<red>Invalid Pattern: " + ValueArgumentParser.buildErrorMark(lowerBoundPattern, lowerBoundValueParserResult));
            return;
        }

        if (upperBoundValueParserResult.valueParserResultType() != ValueParser.ValueParserResultType.VALID) {
            sender.sendRichMessage("<red>Error while parsing upper-bound-value: " + upperBoundValueParserResult.valueParserResultType());
            sender.sendRichMessage("<red>Invalid Pattern: " + ValueArgumentParser.buildErrorMark(upperBoundPattern, upperBoundValueParserResult));
            return;
        }

        var lowerBoundValue = ValueParser.fromPattern(lowerBoundPattern);
        var upperBoundValue = ValueParser.fromPattern(upperBoundPattern);

        if (lowerBoundValue.amount() > upperBoundValue.amount()) {
            plugin.messenger().sendMessage(sender, NodePath.path("exception", "value-parser", "lower-higher-than-upper"));
            return;
        }

        var itemStack = sender.getInventory().getItemInMainHand();
        if (itemStack.getType() == Material.AIR) {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "edit", "add", "item-null"));
            return;
        }

        if (editType == EditType.SELL) {
            shop.addSellsItem(new ShopItem(itemStack, lowerBoundValue, upperBoundValue));
        } else {
            shop.addBuysItem(new ShopItem(itemStack, lowerBoundValue, upperBoundValue));
        }

        if (!plugin.shopService().saveShop(shop)) {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "edit", "error"));
            return;
        }
        plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "edit", "add", "success"));
    }

    private void handleEditRemoveItem(CommandContext<Player> playerCommandContext) {
        var shopName = (String) playerCommandContext.get("shop-name");
        var sender = playerCommandContext.sender();
        var itemId = (int) playerCommandContext.get("id");

        var editType = (EditType) playerCommandContext.get("type");

        var shopOpt = plugin.shopService().getShop(shopName);
        if (shopOpt.isEmpty()) {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "not-found"));
            return;
        }

        var shop = shopOpt.get();

        if (editType == EditType.SELL) {
            if (itemId > shop.shopSellsItems().size() - 1) {
                plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "edit", "remove", "no-such-item"));
                return;
            }
            if (!shop.removeSellsItem(itemId)) {
                plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "edit", "remove", "no-such-item"));
                return;
            }
        } else {
            if (itemId > shop.shopBuysItems().size() - 1) {
                plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "edit", "remove", "no-such-item"));
                return;
            }
            if (!shop.removeBuysItem(itemId)) {
                plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "edit", "remove", "no-such-item"));
                return;
            }
        }

        if (!plugin.shopService().saveShop(shop)) {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "edit", "error"));
            return;
        }
        plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "edit", "remove", "success"));
    }
}
