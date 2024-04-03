package dev.thezexquex.menushops.shop;

import dev.thezexquex.menushops.shop.value.Value;

public record ItemEditInfo(
        Value newUpperBoundValue,
        Value newLowerBoundValue
) {
}
