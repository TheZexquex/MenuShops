package dev.thezexquex.menushops.command;

import dev.thezexquex.menushops.MenuShopsPlugin;
import dev.thezexquex.menushops.command.core.BaseCommand;
import dev.thezexquex.menushops.shop.ItemEditInfo;
import dev.thezexquex.menushops.shop.MenuShop;
import dev.thezexquex.menushops.shop.ShopItem;
import dev.thezexquex.menushops.shop.gui.MenuShopGui;
import dev.thezexquex.menushops.shop.value.Value;
import dev.thezexquex.menushops.util.KeyValue;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.data.ProtoItemStack;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.suggestion.Suggestion;
import org.spongepowered.configurate.NodePath;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static dev.thezexquex.menushops.command.argument.ShopParser.shopParser;
import static dev.thezexquex.menushops.command.argument.ValueArgumentParser.valueParser;
import static org.incendo.cloud.bukkit.parser.ItemStackParser.itemStackParser;
import static org.incendo.cloud.bukkit.parser.PlayerParser.playerParser;
import static org.incendo.cloud.parser.standard.EnumParser.enumParser;
import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;
import static org.incendo.cloud.parser.standard.StringParser.quotedStringParser;
import static org.incendo.cloud.parser.standard.StringParser.stringParser;

@SuppressWarnings("SpellCheckingInspection")
public class MenuShopCommand extends BaseCommand {
    public MenuShopCommand(MenuShopsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void register(CommandManager<CommandSender> commandManager) {
        // /menushops open <shop>
        commandManager.command(commandManager.commandBuilder("menushops")
                .permission("menushops.command.menushops.open")
                .literal("open")
                .required("shop", shopParser(plugin.shopService()))
                .optional("target", playerParser())
                .handler(this::handleOpen)
        );

        // /menushops create <shop-id> <display-name>
        commandManager.command(commandManager.commandBuilder("menushops")
                .permission("menushops.command.menushops.create")
                .literal("create")
                .senderType(Player.class)
                .required("shop-name", stringParser())
                .required("shop-title", quotedStringParser())
                .handler(this::handleCreate)
        );

        // /menushops delete <shop-id>
        commandManager.command(commandManager.commandBuilder("menushops")
                .permission("menushops.command.menushops.delete")
                .literal("delete")
                .required("shop", shopParser(plugin.shopService()))
                .handler(this::handleDelete)
        );
        
        var addItemBuilder = commandManager.commandBuilder("menushops")
                .senderType(Player.class)
                .permission("menushops.command.menushops.edit.additem")
                .literal("edit")
                .required("shop", shopParser(plugin.shopService()))
                .literal("additem")
                .required("type", enumParser(ShopItem.ItemType.class))
                .required("lower-bound", valueParser(plugin.pluginHookService(), plugin.valueRegistry()))
                .required("upper-bound", valueParser(plugin.pluginHookService(), plugin.valueRegistry()));

        var setItemBuilder = commandManager.commandBuilder("menushops")
                .senderType(Player.class)
                .permission("menushops.command.menushops.edit.additem")
                .literal("edit")
                .required("shop", shopParser(plugin.shopService()))
                .literal("setitem")
                .required("type", enumParser(ShopItem.ItemType.class))
                .required("id", integerParser(), (context3, input3) -> suggestItems(context3))
                .required("lower-bound", valueParser(plugin.pluginHookService(), plugin.valueRegistry()))
                .required("upper-bound", valueParser(plugin.pluginHookService(), plugin.valueRegistry()));

        var insertItemBuilder = commandManager.commandBuilder("menushops")
                .senderType(Player.class)
                .permission("menushops.command.menushops.edit.additem")
                .literal("edit")
                .required("shop", shopParser(plugin.shopService()))
                .literal("insertitembefore")
                .required("type", enumParser(ShopItem.ItemType.class))
                .required("id", integerParser(), (context2, input2) -> suggestItems(context2))
                .required("lower-bound", valueParser(plugin.pluginHookService(), plugin.valueRegistry()))
                .required("upper-bound", valueParser(plugin.pluginHookService(), plugin.valueRegistry()));

        // /menushops edit <shop> setitem <type> <id> <lower-value> <upper-value> @hand
        commandManager.command(setItemBuilder
                .literal("@hand")
                .handler(commandContext -> handleEditSetItem(commandContext, true, true))
        );

        // /menushops edit <shop> setitem <type> <id> <lower-value> <upper-value> <item> <amount
        commandManager.command(setItemBuilder
                .required("itemstack", itemStackParser())
                .required("amount", integerParser(1, 64))
                .handler(commandContext -> handleEditSetItem(commandContext, false, true))

        );

        // /menushops edit <shop> insertitembefore <type> <id> <lower-value> <upper-value> @hand
        commandManager.command(insertItemBuilder
                .literal("@hand")
                .handler(commandContext -> handleEditSetItem(commandContext, true, false))
        );

        // /menushops edit <shop> insertitembefore <type> <id> <lower-value> <upper-value> <item> <amount
        commandManager.command(insertItemBuilder
                .required("itemstack", itemStackParser())
                .required("amount", integerParser(1, 64))
                .handler(commandContext -> handleEditSetItem(commandContext, false, false))

        );

        // /menushops edit <shop> additem <type> <lower-value> <upper-value> @hand
        commandManager.command(addItemBuilder
                .literal("@hand")
                .handler(commandContext -> handleEditAddItem(commandContext, true))
        );

        // /menushops edit <shop> additem <type> <lower-value> <upper-value> <item> <amount
        commandManager.command(addItemBuilder
                .required("itemstack", itemStackParser())
                .required("amount", integerParser(1, 64))
                .handler(commandContext -> handleEditAddItem(commandContext, false))

        );

        // /menushops edit <shop> removeittem <type> <id>
        commandManager.command(commandManager.commandBuilder("menushops")
                .senderType(Player.class)
                .literal("edit")
                .permission("menushops.command.menushops.edit.removeitem")
                .required("shop", shopParser(plugin.shopService()))
                .literal("removeitem")
                .required("type", enumParser(ShopItem.ItemType.class))
                .required("id", integerParser(), (context1, input1) -> suggestItems(context1))
                .handler(this::handleEditRemoveItem)
        );

        var editItemBuilder = commandManager.commandBuilder("menushops")
                .senderType(Player.class)
                .literal("edit")
                .permission("menushops.command.menushops.edit.edititem")
                .required("shop", shopParser(plugin.shopService()))
                .literal("edititem")
                .required("type", enumParser(ShopItem.ItemType.class))
                .required("id", integerParser(), (context, input) -> suggestItems(context));

        // /menushops edit <shop> additem <type> <lower-value> <upper-value>
        commandManager.command(editItemBuilder
                .literal("values")
                .required("lower-bound", valueParser(plugin.pluginHookService(), plugin.valueRegistry()))
                .required("upper-bound", valueParser(plugin.pluginHookService(), plugin.valueRegistry()))
                .handler(this::handleEditEditItemValues)
        );

        // /menushops edit <shop> additem <type> <lower-value> <upper-value> <item> <amount>
        commandManager.command(editItemBuilder
                .literal("item")
                .required("itemstack", itemStackParser())
                .required("amount", integerParser(1, 64))
                .handler(commandContext -> handleEditEditItemItem(commandContext, false))
        );

        // /menushops edit <shop> additem <type> <lower-value> <upper-value> @hand
        commandManager.command(editItemBuilder
                .literal("item")
                .literal("@hand")
                .handler(commandContext -> handleEditEditItemItem(commandContext, true))
        );

        // /menushops reload
        commandManager.command(commandManager.commandBuilder("menushops")
                .literal("reload")
                .permission("menushops.command.menushops.reload")
                .handler(this::handleReload)

        );
    }

    private void handleEditSetItem(@NonNull CommandContext<Player> context, boolean useItemInHand, boolean replace) {
        var shop = (MenuShop) context.get("shop");
        var sender = context.sender();
        var itemId = (int) context.get("id");
        var editType = (ShopItem.ItemType) context.get("type");

        if (!shop.hasItem(itemId, editType)) {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "edit", "remove", "no-such-item"));
            return;
        }

        ItemStack itemStack;
        if (!useItemInHand) {
            var protoItemStack = (ProtoItemStack) context.get("itemstack");
            var amount = (int) context.get("amount");
            itemStack = protoItemStack.createItemStack(amount, true);
        } else {
            itemStack = sender.getInventory().getItemInMainHand();
        }

        if (itemStack.getType() == Material.AIR) {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "edit", "add", "item-null"));
            return;
        }

        var values = handleValueParsing(sender, context);
        if (values == null) {
            return;
        }

        var lowerBoundValue = values.value();
        var upperBoundValue = values.key();

        shop.insertItem(itemId, new ShopItem(itemStack, lowerBoundValue, upperBoundValue), editType, replace);

        if (!plugin.shopService().saveShop(shop)) {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "edit", "error"));
            return;
        }
        plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "edit", "edit-item", "success"));
    }

    private void handleEditEditItemItem(@NonNull CommandContext<Player> context, boolean useItemInHand) {
        var shop = (MenuShop) context.get("shop");
        var sender = context.sender();
        var itemId = (int) context.get("id");
        var editType = (ShopItem.ItemType) context.get("type");

        if (!shop.hasItem(itemId, editType)) {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "edit", "remove", "no-such-item"));
            return;
        }

        ItemStack itemStack;
        if (!useItemInHand) {
            var protoItemStack = (ProtoItemStack) context.get("itemstack");
            var amount = (int) context.get("amount");
            itemStack = protoItemStack.createItemStack(amount, true);
        } else {
            itemStack = sender.getInventory().getItemInMainHand();
        }

        if (itemStack.getType() == Material.AIR) {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "edit", "add", "item-null"));
            return;
        }

        var itemEdiInfo = new ItemEditInfo(null, null, itemStack);
        shop.editItem(itemId, itemEdiInfo, editType);

        if (!plugin.shopService().saveShop(shop)) {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "edit", "error"));
            return;
        }
        plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "edit", "edit-item", "success"));
    }

    private void handleEditEditItemValues(@NonNull CommandContext<Player> context) {
        var shop = (MenuShop) context.get("shop");
        var sender = context.sender();
        var itemId = (int) context.get("id");
        var editType = (ShopItem.ItemType) context.get("type");

        if (!shop.hasItem(itemId, editType)) {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "edit", "remove", "no-such-item"));
            return;
        }

        var values = handleValueParsing(sender, context);
        if (values == null) {
            return;
        }

        var lowerBoundValue = values.value();
        var upperBoundValue = values.key();

        var itemEditInfo = new ItemEditInfo(lowerBoundValue, upperBoundValue, null);

        shop.editItem(itemId, itemEditInfo, editType);

        if (!plugin.shopService().saveShop(shop)) {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "edit", "error"));
            return;
        }
        plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "edit", "edit-values", "success"));
    }

    private void handleReload(CommandContext<CommandSender> context) {
        plugin.messenger().sendMessage(context.sender(),
                NodePath.path("command", "menushops", "reload", "attempt"));
        plugin.reload();
        plugin.messenger().sendMessage(context.sender(),
                NodePath.path("command", "menushops", "reload", "success"));
    }

    private void handleOpen(CommandContext<CommandSender> context) {
        var sender = context.sender();
        Player playerToOpenFor;

        if (context.contains("target")) {
            playerToOpenFor = context.get("target");
        } else if (sender instanceof Player player) {
            playerToOpenFor = player;
        } else {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "no-target"));
            return;
        }

        var shop = (MenuShop) context.get("shop");

        MenuShopGui.constructGui(playerToOpenFor, shop, plugin.messenger(), plugin.icons()).open();
    }

    private void handleCreate(CommandContext<Player> context) {
        var shopName = (String) context.get("shop-name");
        var shopTitle = (String) context.get("shop-title");
        var player = context.sender();

        plugin.shopService().createShop(shopName, shopTitle);
        plugin.messenger().sendMessage(
                player,
                NodePath.path("command", "menushops", "create", "success"),
                Placeholder.parsed("shop-name", shopName),
                Placeholder.parsed("shop-title", shopTitle)
        );
    }

    private void handleDelete(CommandContext<CommandSender> context) {
        var shop = (MenuShop) context.get("shop");
        var sender = context.sender();

        if (plugin.shopService().deleteShop(shop.identifier())) {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "delete", "success"), Placeholder.parsed("shop-name", shop.identifier()));
            return;
        }
        plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "delete", "error"), Placeholder.parsed("shop-name", shop.identifier()));
    }

    private void handleEditAddItem(CommandContext<Player> context, boolean useItemInHand) {
        var shop = (MenuShop) context.get("shop");
        var sender = context.sender();
        var editType = (ShopItem.ItemType) context.get("type");

        var values = handleValueParsing(sender, context);
        if (values == null) {
            return;
        }

        var lowerBoundValue = values.value();
        var upperBoundValue = values.key();

        ItemStack itemStack;
        if (!useItemInHand) {
            var protoItemStack = (ProtoItemStack) context.get("itemstack");
            var amount = (int) context.get("amount");
            itemStack = protoItemStack.createItemStack(amount, true);
        } else {
            itemStack = sender.getInventory().getItemInMainHand();
        }
        if (itemStack.getType() == Material.AIR) {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "edit", "add", "item-null"));
            return;
        }

        shop.addItem(new ShopItem(itemStack, lowerBoundValue, upperBoundValue), editType);

        if (!plugin.shopService().saveShop(shop)) {
            plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "edit", "error"));
            return;
        }
        plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "edit", "add", "success"));
    }

    private void handleEditRemoveItem(CommandContext<Player> context) {
        var shop = (MenuShop) context.get("shop");
        var sender = context.sender();
        var itemId = (int) context.get("id");

        var editType = (ShopItem.ItemType) context.get("type");

        if (editType == ShopItem.ItemType.SHOP_SELLS) {
            if (itemId > shop.shopSellsItems().size() - 1 || !shop.removeItem(itemId, ShopItem.ItemType.SHOP_SELLS)) {
                plugin.messenger().sendMessage(sender, NodePath.path("command", "menushops", "edit", "remove", "no-such-item"));
                return;
            }
        } else {
            if (itemId > shop.shopBuysItems().size() - 1 || !shop.removeItem(itemId, ShopItem.ItemType.SHOP_BUYS)) {
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

    public KeyValue<Value, Value> handleValueParsing(CommandSender sender, CommandContext<Player> context) {
        var lowerBoundValue = (Value) context.get("lower-bound");
        var upperBoundValue = (Value) context.get("upper-bound");

        var lowerAmount = lowerBoundValue.amount();
        var higherAmount = upperBoundValue.amount();

        if (lowerAmount > higherAmount) {
            plugin.messenger().sendMessage(sender, NodePath.path("exception", "value-parser", "lower-higher-than-upper"));
            return null;
        }
        return KeyValue.of(lowerBoundValue, upperBoundValue);
    }

    public CompletableFuture<List<Suggestion>> suggestItems(CommandContext<Player> context) {
        if (!context.contains("shop")) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
        var shop = (MenuShop) context.get("shop");
        var type = (ShopItem.ItemType) context.get("type");
        if (type == ShopItem.ItemType.SHOP_BUYS) {
            return CompletableFuture.completedFuture(
                    shop.shopBuysItems().stream()
                            .map(shopItem -> shop.shopBuysItems().indexOf(shopItem))
                            .map(integer -> Suggestion.suggestion(String.valueOf(integer))).toList());
        }
        return CompletableFuture.completedFuture(shop.shopSellsItems().stream()
                .map(shopItem -> shop.shopSellsItems().indexOf(shopItem))
                .map(integer -> Suggestion.suggestion(String.valueOf(integer))).toList());
    }
}