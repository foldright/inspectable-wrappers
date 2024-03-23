package io.foldright.inspectablewrappers;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Predicate;

import static io.foldright.inspectablewrappers.InternalUtils.unwrapNonNull;
import static java.util.Objects.requireNonNull;


/**
 * This {@code Wrapper} interface is used to be implemented by wrapper classes,
 * make {@code wrapper instances} as an <b>inspectable wrapper chain</b>(linked list).
 *
 * @param <T> the type of instances that be wrapped
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see Attachable
 * @see WrapperAdapter
 */
@ParametersAreNonnullByDefault
public interface Wrapper<T> {
    /**
     * Returns the underlying instance that be wrapped.
     * <p>
     * this method also make the wrapper instances as a wrapper chain(linked list).
     */
    @NonNull
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
     * @throws NullPointerException if any arguments is null or any wrapper {{@link #unwrap()}} returns null
     * @see WrapperAdapter#adaptee()
     */
    static <W> boolean isInstanceOf(final W wrapper, final Class<?> clazz) {
        requireNonNull(wrapper, "wrapper is null");
        requireNonNull(clazz, "clazz is null");
        return inspect(wrapper, w -> {
            if (w instanceof WrapperAdapter) {
                Object adaptee = ((WrapperAdapter<?>) w).adaptee();
                if (clazz.isAssignableFrom(adaptee.getClass())) return true;
            }
            return clazz.isAssignableFrom(w.getClass());
        });
    }

    /**
     * Reports whether any wrapper on the wrapper chain satisfy the given {@code predicate}.
     * <p>
     * The wrapper chain consists of wrapper itself, followed by the wrappers
     * obtained by repeatedly calling {@link #unwrap()}.
     *
     * @param wrapper   wrapper instance/wrapper chain
     * @param predicate inspect logic
     * @param <W>       the type of instances that be wrapped
     * @return return {@code false} if no wrapper on the wrapper chain satisfy the given {@code predicate},
     * otherwise return {@code true}
     * @throws NullPointerException if any arguments is null or any wrapper {{@link #unwrap()}} returns null
     */
    @SuppressWarnings("unchecked")
    static <W> boolean inspect(final W wrapper, final Predicate<? super W> predicate) {
        requireNonNull(wrapper, "wrapper is null");
        requireNonNull(predicate, "predicate is null");
        for (Object w = wrapper; w instanceof Wrapper; w = unwrapNonNull(w)) {
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
     * @return the attachment value of wrapper of given key on the wrapper chain,
     * or null if the attachment is absent
     * @throws NullPointerException if any arguments is null or any wrapper {{@link #unwrap()}} returns null
     * @throws ClassCastException   if the return value is not type {@code <V>}
     * @see Attachable#getAttachment(Object)
     */
    @Nullable
    @SuppressWarnings("unchecked")
    static <W, K, V> V getAttachment(final W wrapper, final K key) {
        requireNonNull(wrapper, "wrapper is null");
        requireNonNull(key, "key is null");
        for (Object w = wrapper; w instanceof Wrapper; w = unwrapNonNull(w)) {
            if (!(w instanceof Attachable)) continue;

            V value = ((Attachable<K, V>) w).getAttachment(key);
            if (value != null) return value;
        }
        return null;
    }
}
