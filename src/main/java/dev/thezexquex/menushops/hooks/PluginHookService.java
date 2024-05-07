package dev.thezexquex.menushops.hooks;

import dev.thezexquex.menushops.MenuShopsPlugin;
import dev.thezexquex.menushops.command.MenuShopCommand;
import org.bukkit.Server;

import java.util.HashSet;
import java.util.Set;

public class PluginHookService {
    private final Server server;
    private final Set<Hook> hooks;

    public PluginHookService(Server server) {
        this.server = server;
        this.hooks = new HashSet<>();
    }

    public void register(MenuShopsPlugin plugin, Hook hook) {
        if (!hook.isAvailable(plugin.getServer())) {
            return;
        }
        plugin.getLogger().info("Registered plugin hook: " + hook.getIdentifier());
        this.hooks.add(hook);
        hook.setup(plugin);
    }

    public boolean isAvailable(Class<? extends Hook> hookClass) {
        return hooks.stream().anyMatch(hookClass::isInstance);
    }
}
