package dev.thezexquex.menushops;

import dev.thezexquex.menushops.command.MenuShopCommand;
import dev.thezexquex.menushops.data.shop.ShopService;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;

import java.util.logging.Level;

public class MenuShopsPlugin extends JavaPlugin {
    private PaperCommandManager<CommandSender> commandManager;
    private ShopService shopService;

    @Override
    public void onEnable() {
        registerCommands();
    }

    private void registerCommands() {
        try {
            this.commandManager = new PaperCommandManager<>(
                    this,
                    ExecutionCoordinator.simpleCoordinator(),
                    SenderMapper.identity()
            );

            if (commandManager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
                commandManager.registerBrigadier();
            }
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "Failed to initialize command manager", e);
            this.getServer().getPluginManager().disablePlugin(this);
        }

        this.shopService = new ShopService(getDataFolder().toPath().resolve("shops"), getLogger());
        shopService.loadAllShops();

        new MenuShopCommand(this).register(commandManager);
    }

    public ShopService shopService() {
        return shopService;
    }
}
