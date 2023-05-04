package io.foldright.inspectablewrappers;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Predicate;


/**
 * This {@code Wrapper} interface is used to be implemented by wrapper classes,
 * make {@code wrapper instances} as an <b>inspectable wrapper chain</b>(linked list).
 *
 * @param <T> the type of instances that be wrapped
 * @author Jerry Lee (oldratlee at gmail dot com)
 */
@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public interface Wrapper<T> {
    /**
     * Returns the underlying instance that be wrapped.
     * <p>
     * this method also make the wrapper instances as a wrapper chain(linked list).
     */
    T unwrap();

    /**
     * Reports whether any wrapper on the wrapper chain matches the given type.
     * <p>
     * The wrapper chain consists of wrapper itself, followed by the wrappers
     * obtained by repeatedly calling {@link #unwrap()}.
     *
     * @param wrapper wrapper instance/wrapper chain
     * @param clazz   target type
     * @param <W>     the type of instances that be wrapped
     * @return return {@code false} if no wrapper on the wrapper chain matches the given type,
     * otherwise return {@code true}
     */
    static <W> boolean isInstanceOf(W wrapper, Class<?> clazz) {
        return check(wrapper, w -> clazz.isAssignableFrom(w.getClass()));
    }

    /**
     * Reports whether any wrapper on the wrapper chain satisfy {@code predicate}.
     * <p>
     * The wrapper chain consists of wrapper itself, followed by the wrappers
     * obtained by repeatedly calling {@link #unwrap()}.
     *
     * @param wrapper   wrapper instance/wrapper chain
     * @param predicate check logic
     * @param <W>       the type of instances that be wrapped
     * @return return {@code false} if no wrapper on the wrapper chain satisfy {@code predicate},
     * otherwise return {@code true}
     */
    @SuppressWarnings("unchecked")
    static <W> boolean check(final W wrapper, final Predicate<? super W> predicate) {
        for (Object w = wrapper; w instanceof Wrapper; w = ((Wrapper<?>) w).unwrap()) {
            if (predicate.test((W) w)) return true;
        }
        return false;
    }

    /**
     * Retrieves the attachment of wrapper of given key on the wrapper chain
     * by calling {@link Attachable#getAttachment(Object)}.
     * <p>
     * The wrapper chain consists of wrapper itself, followed by the wrappers
     * obtained by repeatedly calling {@link #unwrap()}.
     * <p>
     * If the key exists in multiple wrappers, outer wrapper win.
     *
     * @param wrapper wrapper instance
     * @param key     the attachment key
     * @param <W>     the type of instances that be wrapped
     * @param <K>     the type of attachment key
     * @param <V>     the type of attachment value
     */
    @Nullable
    @SuppressWarnings("unchecked")
    static <W, K, V> V getAttachment(final W wrapper, final K key) {
        for (Object w = wrapper; w instanceof Wrapper; w = ((Wrapper<W>) w).unwrap()) {
            if (!(w instanceof Attachable)) continue;

            V value = ((Attachable<K, V>) w).getAttachment(key);
            if (value != null) return value;
        }
        return null;
    }
}
