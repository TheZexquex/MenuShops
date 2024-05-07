package dev.thezexquex.menushops.hooks;

import dev.thezexquex.menushops.MenuShopsPlugin;
import org.bukkit.Server;

public abstract class Hook {
    protected abstract String getIdentifier();
    public boolean isAvailable(Server server) {
        return server.getPluginManager().isPluginEnabled(getIdentifier());
    }

    public void setup(MenuShopsPlugin plugin) {

    }
}
