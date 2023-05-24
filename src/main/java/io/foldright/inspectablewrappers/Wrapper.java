package io.foldright.inspectablewrappers;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static io.foldright.inspectablewrappers.InternalUtils.unwrapNonNull;
import static java.util.Objects.requireNonNull;


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
     * @throws NullPointerException if any arguments is null
     */
    static <W> boolean isInstanceOf(W wrapper, Class<?> clazz) {
        requireNonNull(wrapper, "wrapper is null");
        requireNonNull(clazz, "clazz is null");
        return inspect(wrapper, w -> clazz.isAssignableFrom(w.getClass()));
    }

    /**
     * Returns the underlying instance of the wrapper chain.
     * <p>
     * The wrapper chain consists of wrapper itself, followed by the wrappers
     * obtained by repeatedly calling {@link #unwrap()}.
     *
     * @param wrapper wrapper instance/wrapper chain
     * @param <W>     the type of instances that be wrapped
     * @return the underlying instance that be wrapped
     */
    @SuppressWarnings("unchecked")
    static <W> W unwrapChain(W wrapper) {
        requireNonNull(wrapper, "wrapper is null");
        Object w = wrapper;
        while (w instanceof Wrapper) w = unwrapNonNull(w);
        return (W) w;
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
     * @throws NullPointerException if any arguments is null
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

    static <W> List<W> chainInstanceList(final W wrapper) {
        List<W> ret = new ArrayList<>();
        forEach(wrapper, ret::add);
        return ret;
    }

    /**
     * Performs the given action for each wrapper on the wrapper chain until all elements have been processed
     * or the action throws an exception. Actions are performed in the order of wrapper chain.
     * Exceptions thrown by the action are relayed to the caller.
     * <p>
     * The wrapper chain consists of wrapper itself, followed by the wrappers
     * obtained by repeatedly calling {@link #unwrap()}.
     *
     * @param wrapper wrapper instance/wrapper chain
     * @param action  The action to be performed for  wrapper on the wrapper chain
     * @param <W>     the type of instances that be wrapped
     */
    @SuppressWarnings("unchecked")
    static <W> void forEach(final W wrapper, final Consumer<? super W> action) {
        requireNonNull(wrapper, "wrapper is null");
        requireNonNull(action, "action is null");
        Object w = wrapper;
        while (w instanceof Wrapper) {
            action.accept((W) w);
            w = unwrapNonNull(w);
        }
        action.accept((W) w);
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
     * @return the attachment value of wrapper of given key on the wrapper chain
     * @throws NullPointerException if any arguments is null
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
