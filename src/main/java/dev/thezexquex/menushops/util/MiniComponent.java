package dev.thezexquex.menushops.util;

import net.kyori.adventure.text.minimessage.MiniMessage;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;

public class MiniComponent {

    public static AdventureComponentWrapper of(String minimessageFormatted) {
        return new AdventureComponentWrapper(MiniMessage.miniMessage().deserialize(minimessageFormatted));
    }
}
