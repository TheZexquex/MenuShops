package dev.thezexquex.menushops.data.shop.typeserializer;

import dev.thezexquex.menushops.shop.value.Value;
import dev.thezexquex.menushops.shop.value.ValueParser;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class ValueTypeSerializer implements TypeSerializer<Value> {
    @Override
    public Value deserialize(Type type, ConfigurationNode node) throws SerializationException {;
        return ValueParser.fromPattern(node.getString());
    }

    @Override
    public void serialize(Type type, @Nullable Value value, ConfigurationNode node) throws SerializationException {
        if (value != null) {
            node.set(ValueParser.toPattern(value));
        }
    }
}
