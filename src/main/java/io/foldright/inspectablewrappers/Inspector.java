package io.foldright.inspectablewrappers;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static io.foldright.inspectablewrappers.InternalUtils.unwrapNonNull;
import static java.util.Objects.requireNonNull;


/**
 * This {@code Inspector} is used to inspect wrapper implementors:
 * <ul>
 *     <li>
 *          Reports whether any wrapper on the wrapper chain matches the given type by static method {@link #isInstanceOf(Object, Class)}
 *     </li>
 *     <li>
 *          Retrieve the attachment from wrapper chain(wrapper instances implement interface {@link Wrapper})
 *          by static method {@link #getAttachment(Object, Object)}.
 *     </li>
 * </ul>
 *
 * @see Wrapper
 */
public final class Inspector {

    // no need to create instance at all
    private Inspector() {
    }

    /**
     * Reports whether any wrapper on the wrapper chain matches the given type.
     * <p>
     * The wrapper chain consists of wrapper itself, followed by the wrappers
     * obtained by repeatedly calling {@link Wrapper#unwrap()}.
     *
     * @param wrapper wrapper instance/wrapper chain
     * @param clazz   target type
     * @param <W>     the type of instances that be wrapped
     * @return return {@code false} if no wrapper on the wrapper chain matches the given type,
     * otherwise return {@code true}
     * @throws NullPointerException if any arguments is null or any wrapper {{@link Wrapper#unwrap()}} returns null
     * @see WrapperAdapter#adaptee()
     */
    public static <W> boolean isInstanceOf(final W wrapper, final Class<?> clazz) {
        requireNonNull(wrapper, "wrapper is null");
        requireNonNull(clazz, "clazz is null");
        return inspect(wrapper, w -> clazz.isAssignableFrom(w.getClass()));
    }

    /**
     * Reports whether any wrapper on the wrapper chain satisfy the given {@code predicate}.
     * <p>
     * The wrapper chain consists of wrapper itself, followed by the wrappers
     * obtained by repeatedly calling {@link Wrapper#unwrap()}.
     *
     * @param wrapper   wrapper instance/wrapper chain
     * @param predicate inspect logic
     * @param <W>       the type of instances that be wrapped
     * @return return {@code false} if no wrapper on the wrapper chain satisfy the given {@code predicate},
     * otherwise return {@code true}
     * @throws NullPointerException if any arguments is null or any wrapper {{@link Wrapper#unwrap()}} returns null
     */
    static <W> boolean inspect(final W wrapper, final Predicate<? super W> predicate) {
        requireNonNull(wrapper, "wrapper is null");
        requireNonNull(predicate, "predicate is null");
        return travel(wrapper, w -> {
            if (predicate.test(w)) return Optional.of(true);
            else return Optional.empty();
        }).orElse(false);
    }

    /**
     * Retrieves the attachment of wrapper of given key on the wrapper chain
     * by calling {@link Attachable#getAttachment(Object)}.
     * <p>
     * The wrapper chain consists of wrapper itself, followed by the wrappers
     * obtained by repeatedly calling {@link Wrapper#unwrap()}.
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
     * @throws NullPointerException if any arguments is null or any wrapper {{@link Wrapper#unwrap()}} returns null
     * @throws ClassCastException   if the return value is not type {@code <V>}
     * @see Attachable#getAttachment(Object)
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <W, K, V> V getAttachment(final W wrapper, final K key) {
        requireNonNull(wrapper, "wrapper is null");
        requireNonNull(key, "key is null");
        return travel(wrapper, w -> {
            if (w instanceof Attachable) {
                V value = ((Attachable<K, V>) w).getAttachment(key);
                return Optional.ofNullable(value);
            } else {
                return Optional.empty();
            }
        }).orElse(null);
    }


    /**
     * Traverses the wrapper chain, and apply the given {@code process} function to each wrapper,
     * and returns the first non-empty({@link Optional#empty()}) result of the process function,
     * otherwise returns {@link Optional#empty()}.
     * <p>
     * The wrapper chain consists of wrapper itself, followed by the wrappers
     * obtained by repeatedly calling {@link Wrapper#unwrap()}.
     *
     * @param wrapper wrapper instance
     * @param process process function
     * @param <W>     the type of instances that be wrapped
     * @param <T>     the return data type of process function
     * @return the first non-empty({@link Optional#empty()}) result of the process function,
     * otherwise returns {@link Optional#empty()}.
     * @throws NullPointerException  if any arguments is null or any wrapper {{@link Wrapper#unwrap()}} returns null
     * @throws IllegalStateException if the adaptee of {@link WrapperAdapter} is a wrapper instance,
     *                               the use of WrapperAdapter is unnecessary!
     */
    @NonNull
    @SuppressWarnings("unchecked")
    static <W, T> Optional<T> travel(final W wrapper, final Function<W, Optional<T>> process) {
        requireNonNull(wrapper, "wrapper is null");
        requireNonNull(process, "process is null");

        Object w = wrapper;
        while (true) {
            // process the instance on wrapper chain
            Optional<T> result = process.apply((W) w);
            if (result.isPresent()) return result;

            // also process the adaptee if it's a WrapperAdapter
            if (w instanceof WrapperAdapter) {
                final Object adaptee = ((WrapperAdapter<?>) w).adaptee();
                if (adaptee instanceof Wrapper) {
                    throw new IllegalStateException("adaptee(" + adaptee.getClass().getName() + ") of WrapperAdapter(" + w.getClass().getName() + ") is a wrapper instance, the use of WrapperAdapter is unnecessary!");
                }

                Optional<T> r = process.apply((W) adaptee);
                if (r.isPresent()) return r;
            }

            if (!(w instanceof Wrapper)) return Optional.empty();
            w = unwrapNonNull(w);
        }
    }
}
