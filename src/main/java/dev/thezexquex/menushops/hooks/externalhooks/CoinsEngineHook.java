package dev.thezexquex.menushops.hooks.externalhooks;

import dev.thezexquex.menushops.MenuShopsPlugin;
import dev.thezexquex.menushops.hooks.Hook;
import dev.thezexquex.menushops.shop.value.type.CoinsEngineValueType;

public class CoinsEngineHook extends Hook {
    @Override
    protected String getIdentifier() {
        return "CoinsEngine";
    }

    @Override
    public void setup(MenuShopsPlugin plugin) {
        plugin.valueRegistry().registerValue(new CoinsEngineValueType());
    }
}
