package jdep.concurrency;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Class allows to set a value once from different threads.
 * Once value is set, any other set is ignored.
 * @param <T>
 *     Object that should be set once.
 */
public class Once<T> {

    private final AtomicReference<Value<T>> value = new AtomicReference<>();

    public boolean set(T value) {
        return this.value.compareAndSet(null, new Value<>(value));
    }

    public T get() {
        Value<T> v = value.get();
        if (v == null) {
            return null;
        }
        return v.get();
    }

    private class Value<T> {
        private final T value;

        public Value(T value) {
            this.value = value;
        }

        public T get() {
            return value;
        }

    }

}
