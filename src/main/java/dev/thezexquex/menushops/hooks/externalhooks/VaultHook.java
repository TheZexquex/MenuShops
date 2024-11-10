package dev.thezexquex.menushops.hooks.externalhooks;

import dev.thezexquex.menushops.MenuShopsPlugin;
import dev.thezexquex.menushops.hooks.Hook;
import dev.thezexquex.menushops.shop.value.type.VaultValueType;
import net.milkbowl.vault.economy.Economy;

public class VaultHook extends Hook {
    @Override
    protected String getIdentifier() {
        return "Vault";
    }

    @Override
    public void setup(MenuShopsPlugin plugin) {
        plugin.valueRegistry().registerValue(new VaultValueType());


        var registeredServiceProvider = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (registeredServiceProvider == null) {
            return;
        }
        var economy = registeredServiceProvider.getProvider();
        plugin.vaultEconomy(economy);
    }


}
