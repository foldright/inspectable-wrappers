package io.foldright.inspectablewrappers;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;


/**
 * Internal use only.
 */
final class InternalUtils {
    /**
     * Unwraps the given wrapper instance with null check.
     */
    static Object unwrapNonNull(final Object wrapper) {
        Object unwrap = ((Wrapper<?>) wrapper).unwrap();
        Supplier<String> msg = () -> "unwrap of " + wrapper + "(class: " + wrapper.getClass().getName() + ") is null";
        return requireNonNull(unwrap, msg);
    }

    private InternalUtils() {
    }
}
