package cc.kako.examples.rest.api.utils;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Functional wrapper around checked/unchecked Exception handling.
 * Useful for stream/builder-esque chains.
 *
 * @author Yalda Kako {@literal <yalda@kako.cc>}
 */
public class Try {
    @FunctionalInterface
    public interface ThrowingRunnable<E extends Exception> {
        void run() throws E;
    }

    @FunctionalInterface
    public interface ThrowingSupplier<T, E extends Exception> {
        T get() throws E;
    }

    public static void run(final ThrowingRunnable runnable) {
        Objects.requireNonNull(runnable);

        try {
            runnable.run();
        } catch (Exception e) {
            /* no-op */
        }
    }

    public static void run(final ThrowingRunnable runnable,
            final Consumer<Exception> onException) {
        Objects.requireNonNull(runnable);
        Objects.requireNonNull(onException);

        try {
            runnable.run();
        } catch (Exception e) {
            onException.accept(e);
        }
    }

    public static <T> Optional<T> supply(final ThrowingSupplier<T, ?> supplier,
            final Consumer<Exception> onException) {
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(onException);

        try {
            return Optional.of(supplier.get());
        } catch (Exception e) {
            onException.accept(e);

            return Optional.empty();
        }
    }

    public static <T> Optional<T> supplyNullable(final ThrowingSupplier<T, ?> supplier,
            final Consumer<Exception> onException) {
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(onException);

        try {
            return Optional.ofNullable(supplier.get());
        } catch (Exception e) {
            onException.accept(e);

            return Optional.empty();
        }
    }
}
