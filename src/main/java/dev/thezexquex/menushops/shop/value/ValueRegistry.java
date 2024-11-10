package dev.thezexquex.menushops.shop.value;

import dev.thezexquex.menushops.shop.value.type.ValueType;

import java.util.HashSet;
import java.util.Set;

public class ValueRegistry {

    private final Set<ValueType> values = new HashSet<>();

    public void registerValue(ValueType value) {
        values.add(value);
    }

    public Set<ValueType> getRegistered() {
        return values;
    }
}
