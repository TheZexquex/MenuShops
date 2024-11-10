package dev.thezexquex.menushops.command;

import dev.thezexquex.menushops.message.Messenger;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.caption.CaptionProvider;
import org.incendo.cloud.caption.CaptionRegistry;
import org.incendo.cloud.caption.StandardCaptionKeys;
import org.spongepowered.configurate.NodePath;

public class CommandCaptions {

    public static void apply(CaptionRegistry<CommandSender> registry, Messenger messenger) {
        registry.registerProvider(
                CaptionProvider.forCaption(AdditionalCaptionKeys.ARGUMENT_PARSE_FAILURE_SHOP,
                        sender -> messenger.getString(NodePath.path("exception", "argument-parse", "shop")))
        );
        registry.registerProvider(
                CaptionProvider.forCaption(AdditionalCaptionKeys.ARGUMENT_PARSE_FAILURE_VALUE,
                        sender -> messenger.getString(NodePath.path("exception", "argument-parse", "value")))
        );
        registry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_CHAR,
                        sender -> messenger.getString(NodePath.path("exception", "argument-parse", "char")))
        );
        registry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_COLOR,
                        sender -> messenger.getString(NodePath.path("exception", "argument-parse", "color")))
        );
        registry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_DURATION,
                        sender -> messenger.getString(NodePath.path("exception", "argument-parse", "duration")))
        );
        registry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_ENUM,
                        sender -> messenger.getString(NodePath.path("exception", "argument-parse", "enum")))
        );
        registry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_NUMBER,
                        sender -> messenger.getString(NodePath.path("exception", "argument-parse", "number")))
        );
        registry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_STRING,
                        sender -> messenger.getString(NodePath.path("exception", "argument-parse", "string")))
        );
        registry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_REGEX,
                        sender -> messenger.getString(NodePath.path("exception", "argument-parse", "regex")))
        );
        registry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_UUID,
                        sender -> messenger.getString(NodePath.path("exception", "argument-parse", "uuid")))
        );

        registry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_BOOLEAN,
                        sender -> messenger.getString(NodePath.path("exception", "argument-parse", "boolean")))
        );
    }
}
