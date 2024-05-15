package dev.thezexquex.menushops.util;

public class KeyValue<K, V> {
    private final K key;
    private final V value;

    private KeyValue(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K key() {
        return key;
    }

    public V value() {
        return value;
    }

    public static <K, V> KeyValue<K, V> of(K key, V value) {
        return new KeyValue<>(key, value);
    }

    public boolean equals(KeyValue<K, V> keyValue) {
        return key.equals(keyValue.key) && value.equals(keyValue.value);
    }
}
