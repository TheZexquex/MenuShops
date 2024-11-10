package dev.thezexquex.menushops.command;

import dev.thezexquex.menushops.command.argument.ValueArgumentParser;
import dev.thezexquex.menushops.message.Messenger;
import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.arguments.NbtPathArgument;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.incendo.cloud.minecraft.extras.caption.ComponentCaptionFormatter;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.spongepowered.configurate.NodePath;

public class CommandCapabilities {

    public static void register(LegacyPaperCommandManager<CommandSender> commandManager, Messenger messenger) {
        if (commandManager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            commandManager.registerBrigadier();
            commandManager.brigadierManager().registerMapping(new TypeToken<ValueArgumentParser<CommandSender>>() {},
                    builder -> builder.toConstant(NbtPathArgument.nbtPath()).cloudSuggestions()
            );
        }

        MinecraftExceptionHandler.<CommandSender>createNative()
                .captionFormatter(ComponentCaptionFormatter.miniMessage())
                .decorator(component -> messenger.component(NodePath.path("prefix"))
                        .append(Component.text(" "))
                        .append(component)
                ).registerTo(commandManager);
    }
}
