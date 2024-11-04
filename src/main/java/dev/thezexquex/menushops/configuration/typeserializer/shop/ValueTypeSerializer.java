package dev.thezexquex.menushops.configuration.typeserializer.shop;

import dev.thezexquex.menushops.shop.value.Value;
import dev.thezexquex.menushops.shop.value.ValueParser;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Objects;

public class ValueTypeSerializer implements TypeSerializer<Value> {
    @Override
    public Value deserialize(@NotNull Type type, ConfigurationNode node) throws SerializationException {;
        return ValueParser.fromPattern(Objects.requireNonNull(node.getString()));
    }

    @Override
    public void serialize(@NotNull Type type, @Nullable Value value, ConfigurationNode node) throws SerializationException {
        if (value != null) {
            node.set(ValueParser.toPattern(value));
        }
    }
}
