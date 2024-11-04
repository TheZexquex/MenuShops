package dev.thezexquex.menushops.shop.value;

public class Amount<T> {
    private final T amount;
    private Amount(T amount) {
        this.amount = amount;
    }

    public T amount() {
        return amount;
    }

    public static <T> Amount<T> of(T t) {
        return new Amount<>(t);
    }
}
