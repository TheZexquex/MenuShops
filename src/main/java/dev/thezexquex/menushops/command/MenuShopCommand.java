package dev.thezexquex.menushops.command;

import dev.thezexquex.menushops.MenuShopsPlugin;
import dev.thezexquex.menushops.command.core.BaseCommand;
import dev.thezexquex.menushops.shop.ShopItem;
import dev.thezexquex.menushops.shop.gui.MenuShopGui;
import dev.thezexquex.menushops.shop.value.ValueParser;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.suggestion.Suggestion;
import org.spongepowered.configurate.NodePath;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import static org.incendo.cloud.bukkit.parser.PlayerParser.playerParser;
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
                .required("shop-name", stringParser(), (context, input) ->
                        CompletableFuture.supplyAsync(() -> plugin.shopService().loadedShopNames().stream().map(Suggestion::suggestion).toList()))
                .optional("target", playerParser())
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
                .required("id", integerParser(), (context, input) ->
                        CompletableFuture.supplyAsync(() -> {
                           var shop = plugin.shopService().getShop(context.get("shop-name"));
                           if (shop.isEmpty()) {
                               return Collections.emptyList();
                           }
                           var type = (EditType) context.get("type");
                           if (type == EditType.BUY) {
                               return shop.get().shopBuysItems().keySet().stream().map(integer -> Suggestion.suggestion(String.valueOf(integer))).toList();
                           }
                           return shop.get().shopSellsItems().keySet().stream().map(integer -> Suggestion.suggestion(String.valueOf(integer))).toList();
                        }))
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

    private void handleOpen(CommandContext<CommandSender> playerCommandContext) {
        var sender = playerCommandContext.sender();
        Player playerToOpenFor;

        if (playerCommandContext.contains("target")) {
            playerToOpenFor = playerCommandContext.get("target");
        } else if (sender instanceof Player player){
            playerToOpenFor = player;
        } else {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "no-target"));
            return;
        }

        var shopName = (String) playerCommandContext.get("shop-name");

        var shopOpt = plugin.shopService().getShop(shopName);
        if (shopOpt.isEmpty()) {
            if (!sender.equals(playerToOpenFor)) {
                plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "not-found"));
            }
            plugin.messenger().sendMessage(playerToOpenFor, NodePath.path("command", "menushops", "not-found"));
            return;
        }

        var shop = shopOpt.get();

        MenuShopGui.constructGui(playerToOpenFor, shop, plugin.messenger(), plugin.icons()).open();
    }

    private void handleCreate(CommandContext<Player> playerCommandContext) {
        var shopName = (String) playerCommandContext.get("shop-name");
        var shopTitle = (String) playerCommandContext.get("shop-title");
        var player = playerCommandContext.sender();

        plugin.shopService().createShop(shopName, shopTitle);
        plugin.messenger().sendMessage(
                player,
                NodePath.path("command", "menushops", "create", "success"),
                Placeholder.parsed("shop-name", shopName),
                Placeholder.parsed("shop-title", shopTitle)
        );
    }

    private void handleDelete(CommandContext<CommandSender> commandSenderCommandContext) {
        var shopName = (String) commandSenderCommandContext.get("shop-name");
        var sender = commandSenderCommandContext.sender();

        if (plugin.shopService().deleteShop(shopName)) {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "delete", "success"), Placeholder.parsed("shop-name", shopName));
            return;
        }
        plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "delete", "error"), Placeholder.parsed("shop-name", shopName));
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

        var lowerBoundValueParserResult = ValueParser.validate(lowerBoundPattern, plugin.pluginHookService());
        var upperBoundValueParserResult = ValueParser.validate(upperBoundPattern, plugin.pluginHookService());

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

        var lowerAmount = lowerBoundValue.amount();
        var higherAmount = upperBoundValue.amount();

        if (lowerAmount instanceof Integer lowerIntAmount && higherAmount instanceof Integer higherIntAmount) {
            if (lowerIntAmount > higherIntAmount) {
                plugin.messenger().sendMessage(sender, NodePath.path("exception", "value-parser", "lower-higher-than-upper"));
                return;
            }
        } else if (lowerAmount instanceof Double lowerDoubleAmount && higherAmount instanceof Double higherDoubleAmount) {
            if (lowerDoubleAmount > higherDoubleAmount) {
                plugin.messenger().sendMessage(sender, NodePath.path("exception", "value-parser", "lower-higher-than-upper"));
                return;
            }
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
