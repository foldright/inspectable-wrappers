package io.foldright.inspectablewrappers;

import edu.umd.cs.findbugs.annotations.DefaultAnnotationForParameters;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;


/**
 * This {@code Inspector} class is used to inspect the wrapper chain.
 *
 * <h2>Common simple usages</h2>
 *
 * <ul>
 * <li>Reports whether any instance on the wrapper chain matches the given type
 *     by static method {@link #containsInstanceTypeOnWrapperChain(Object, Class)}
 * <li>Gets the base of the wrapper chain, aka. the last instance of the wrapper chain
 *     by static method {@link #getBaseOfWrapperChain(Object)}
 * <li>Gets the wrapper chain, aka. the list of all instances on the wrapper chain
 *     by static method {@link #getInstancesOfWrapperChain(Object)}
 * <li>Retrieves the attachment of instance on the wrapper chain
 *     by static method {@link #getAttachmentFromWrapperChain(Object, Object)}
 * <li>Verifies the compliance of wrapper chain with the specification contracts
 *     by static method {@link #verifyWrapperChainContracts(Object)}
 *     or {@link #verifyWrapperChainContracts(Object, Class)}
 * </ul>
 *
 * <h3>Convenience methods for <code>Wrapper</code> interface</h3>
 *
 * <ul>
 * <li>Unwraps {@link Wrapper} to the underlying instance
 *     by static method {@link #unwrap(Object)}
 * <li>Checks the input object is an instance of {@link Wrapper} or not
 *     by static method {@link #isWrapper(Object)}
 * </ul>
 *
 * <h2>Advanced usages</h2>
 *
 * <ul>
 * <li>Reports whether any instance on the wrapper chain satisfy the given {@link Predicate}
 *     by static method {@link #testWrapperChain(Object, Predicate)}
 * <li>Performs the given {@code action} for each instance on the wrapper chain
 *     by static method {@link #forEachOnWrapperChain(Object, Consumer)}
 * <li>Traverses the wrapper chain and applies the given {@link Function} to each instance on the wrapper chain
 *     by static method {@link #travelWrapperChain(Object, Function)}
 * </ul>
 * <p>
 * You can implement your own inspection logic using above advanced methods.
 *
 * <h2>Note about usage and methods naming</h2>
 * <p>
 * All method names contain the word "wrapper chain",
 * so the usage code is easily recognizable as related to {@code inspectable wrappers}.
 * <p>
 * Because the method names are long and informative,
 * it's recommended to {@code static import} these methods.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @author Zava Xu (zava dot kid at gmail dot com)
 * @see Wrapper
 * @see Attachable
 * @see WrapperAdapter
 */
@DefaultAnnotationForParameters(NonNull.class)
public final class Inspector {
    ////////////////////////////////////////////////////////////////////////////////
    // region# Common simple usages
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Reports whether any instance on the wrapper chain matches the given type.
     * <p>
     * The wrapper chain consists of wrapper itself, followed by the wrappers
     * obtained by repeatedly calling {@link Wrapper#unwrap_()}.
     *
     * @param wrapper      wrapper instance/wrapper chain
     * @param instanceType target type
     * @param <W>          the type of instances that be wrapped
     * @return {@code false} if no wrapper on the wrapper chain matches the given type,
     * otherwise return {@code true}
     * @throws NullPointerException  if any arguments is null,
     *                               or any wrapper {@link Wrapper#unwrap_()} returns null,
     *                               or the adaptee of {@link WrapperAdapter} is null
     * @throws IllegalStateException if the adaptee of {@link WrapperAdapter} is an instance of {@link Wrapper}
     *                               or CYCLIC wrapper chain
     */
    @Contract(pure = true)
    public static <W> boolean containsInstanceTypeOnWrapperChain(final W wrapper, final Class<?> instanceType) {
        requireNonNull(wrapper, "wrapper is null");
        requireNonNull(instanceType, "instanceType is null");
        return testWrapperChain(wrapper, instanceType::isInstance);
    }

    /**
     * Gets the instance on the wrapper chain matches the given type.
     * <p>
     * The wrapper chain consists of wrapper itself, followed by the wrappers
     * obtained by repeatedly calling {@link Wrapper#unwrap_()}.
     * <p>
     * If multiple instances matches, outer wrapper instance win.
     *
     * @param wrapper      wrapper instance/wrapper chain
     * @param instanceType target type
     * @param <W>          the type of instances that be wrapped
     * @return an {@link Optional} containing the first instance on the wrapper chain that matches the given type,
     * or an empty {@code Optional} if no instance matches the given type
     * @throws NullPointerException  if any arguments is null,
     *                               or any wrapper {@link Wrapper#unwrap_()} returns null,
     *                               or the adaptee of {@link WrapperAdapter} is null
     * @throws IllegalStateException if the adaptee of {@link WrapperAdapter} is an instance of {@link Wrapper}
     *                               or CYCLIC wrapper chain
     */
    @NonNull
    @Contract(pure = true)
    public static <W> Optional<W> getFirstInstanceOfTypeOnWrapperChain(final W wrapper, final Class<?> instanceType) {
        requireNonNull(wrapper, "wrapper is null");
        requireNonNull(instanceType, "instanceType is null");
        return findFirstInstanceOnWrapperChain(wrapper, instanceType::isInstance);
    }

    /**
     * Gets the base of the wrapper chain, aka. the last instance of the wrapper chain.
     * <p>
     * The wrapper chain consists of wrapper itself, followed by the wrappers
     * obtained by repeatedly calling {@link Wrapper#unwrap_()}.
     *
     * @param wrapper wrapper instance
     * @param <W>     the type of instances that be wrapped
     * @throws NullPointerException  if wrapped argument is null,
     *                               or any wrapper {@link Wrapper#unwrap_()} returns null,
     *                               or the adaptee of {@link WrapperAdapter} is null
     * @throws IllegalStateException if the adaptee of {@link WrapperAdapter} is an instance of {@link Wrapper}
     *                               or CYCLIC wrapper chain
     */
    @NonNull
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <W> W getBaseOfWrapperChain(final W wrapper) {
        Object[] holder = new Object[1];
        forEachOnWrapperChain(wrapper, w -> holder[0] = w);
        return (W) holder[0];
    }

    /**
     * Gets the wrapper chain, aka. the list of all instances on the wrapper chain.
     * <p>
     * The wrapper chain consists of wrapper itself, followed by the wrappers
     * obtained by repeatedly calling {@link Wrapper#unwrap_()}.
     *
     * @param wrapper wrapper instance
     * @param <W>     the type of instances that be wrapped
     * @throws NullPointerException  if wrapped argument is null,
     *                               or any wrapper {@link Wrapper#unwrap_()} returns null,
     *                               or the adaptee of {@link WrapperAdapter} is null
     * @throws IllegalStateException if the adaptee of {@link WrapperAdapter} is an instance of {@link Wrapper}
     *                               or CYCLIC wrapper chain
     */
    @NonNull
    @Contract(pure = true)
    public static <W> List<W> getInstancesOfWrapperChain(final W wrapper) {
        List<W> ret = new ArrayList<>();
        forEachOnWrapperChain(wrapper, ret::add);
        return ret;
    }

    /**
     * Retrieves the attachment of instance on the wrapper chain for the given key
     * by calling {@link Attachable#getAttachment_(Object)}.
     * <p>
     * The wrapper chain consists of wrapper itself, followed by the wrappers
     * obtained by repeatedly calling {@link Wrapper#unwrap_()}.
     * <p>
     * If the same key exists in multiple wrappers, outer wrapper win.
     *
     * @param wrapper wrapper instance
     * @param key     the attachment key
     * @param <W>     the type of instances that be wrapped
     * @param <K>     the type of attachment key
     * @param <V>     the type of attachment value
     * @return the attachment value of wrapper for given key on the wrapper chain,
     * or null if the attachment is absent
     * @throws NullPointerException  if any arguments is null,
     *                               or any wrapper {@link Wrapper#unwrap_()} returns null,
     *                               or the adaptee of {@link WrapperAdapter} is null
     * @throws ClassCastException    if the return value is not type {@code <V>}
     * @throws IllegalStateException if the adaptee of {@link WrapperAdapter} is an instance of {@link Wrapper}
     *                               or CYCLIC wrapper chain
     * @see Attachable#getAttachment_(Object)
     */
    @Nullable
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <W, K, V> V getAttachmentFromWrapperChain(final W wrapper, final K key) {
        requireNonNull(wrapper, "wrapper is null");
        requireNonNull(key, "key is null");
        return travelWrapperChain(wrapper, w -> {
            if (w instanceof Attachable) {
                V value = ((Attachable<K, V>) w).getAttachment_(key);
                return Optional.ofNullable(value);
            } else {
                return Optional.empty();
            }
        }).orElse(null);
    }

    /**
     * Verifies the compliance of wrapper chain with the specification contracts.
     * <p>
     * The wrapper chain consists of wrapper itself, followed by the wrappers
     * obtained by repeatedly calling {@link Wrapper#unwrap_()}.
     * <p>
     * more about the specification contracts see the doc of below methods:
     * <ul>
     * <li>{@link Wrapper#unwrap_()}
     * <li>{@link WrapperAdapter#adaptee_()}
     * </ul>
     *
     * @param wrapper wrapper instance
     * @param <W>     the type of instances that be wrapped
     * @throws NullPointerException  if wrapper argument is null,
     *                               or any wrapper {@link Wrapper#unwrap_()} returns null,
     *                               or the adaptee of {@link WrapperAdapter} is null
     * @throws IllegalStateException if the adaptee of {@link WrapperAdapter} is an instance of {@link Wrapper}
     *                               or CYCLIC wrapper chain
     */
    public static <W> void verifyWrapperChainContracts(final W wrapper) {
        travelWrapperChain(wrapper, w -> Optional.empty());
    }

    /**
     * Verifies the compliance of wrapper chain with the specification contracts,
     * and checks all instances on wrapper chain is an instance of the given {@code bizInterface}.
     * <p>
     * The wrapper chain consists of wrapper itself, followed by the wrappers
     * obtained by repeatedly calling {@link Wrapper#unwrap_()}.
     * <p>
     * more about the specification contracts see the doc of below methods:
     * <ul>
     * <li>{@link Wrapper#unwrap_()}
     * <li>{@link WrapperAdapter#adaptee_()}
     * </ul>
     *
     * @param wrapper wrapper instance
     * @param <W>     the type of instances that be wrapped
     * @throws NullPointerException  if any arguments is null,
     *                               or any wrapper {@link Wrapper#unwrap_()} returns null,
     *                               or the adaptee of {@link WrapperAdapter} is null
     * @throws IllegalStateException if any instance on the wrapper chain is not an instance of {@code bizInterface},
     *                               or the adaptee of {@link WrapperAdapter} is an instance of {@link Wrapper}
     *                               or CYCLIC wrapper chain
     */
    public static <W> void verifyWrapperChainContracts(final W wrapper, final Class<W> bizInterface) {
        requireNonNull(wrapper, "wrapper is null");
        requireNonNull(bizInterface, "bizInterface is null");
        forEachOnWrapperChain(wrapper, w -> {
            if (!bizInterface.isInstance(w)) {
                throw new IllegalStateException("the instance(" + w.getClass().getName() +
                        ") on wrapper chain is not an instance of " + bizInterface.getName());
            }
        });
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Convenience methods for Wrapper interface
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Unwraps {@link Wrapper} to the underlying instance if input is a {@link Wrapper} instance.
     * <p>
     * This method is {@code null}-safe, return {@code null} iff input parameter is {@code null};
     * If input parameter is not a {@link Wrapper} just return input.
     * <p>
     * A convenience method for {@link Wrapper#unwrap_()}
     *
     * @param obj wrapper instance
     * @param <W> the type of instances that be wrapped
     * @throws NullPointerException if {@link Wrapper#unwrap_()} returns null
     * @see Wrapper#unwrap_()
     * @see #isWrapper(Object)
     */
    @Nullable
    @Contract(value = "null -> null; !null -> !null", pure = true)
    @SuppressWarnings("unchecked")
    public static <W> W unwrap(@Nullable final W obj) {
        if (!isWrapper(obj)) return obj;
        return (W) unwrapNonNull(obj);
    }

    /**
     * Checks the input object is an instance of {@link Wrapper} or not,
     * return {@code false} if input {@code null}.
     * <p>
     * A convenience method for {@link Wrapper} interface.
     *
     * @see #unwrap(Object)
     */
    @Contract(value = "null -> false", pure = true)
    public static boolean isWrapper(@Nullable Object obj) {
        return obj instanceof Wrapper;
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Advanced methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Reports whether any instance on the wrapper chain satisfies the given {@code predicate}.
     * Exceptions thrown by the {@code predicate} are relayed to the caller.
     * <p>
     * The wrapper chain consists of wrapper itself, followed by the wrappers
     * obtained by repeatedly calling {@link Wrapper#unwrap_()}.
     *
     * @param wrapper   wrapper instance/wrapper chain
     * @param predicate inspect logic
     * @param <W>       the type of instances that be wrapped
     * @return return {@code false} if no wrapper on the wrapper chain satisfy the given {@code predicate},
     * otherwise {@code true}
     * @throws NullPointerException  if any arguments is null,
     *                               or any wrapper {@link Wrapper#unwrap_()} returns null,
     *                               or the adaptee of {@link WrapperAdapter} is null
     * @throws IllegalStateException if the adaptee of {@link WrapperAdapter} is an instance of {@link Wrapper}
     *                               or CYCLIC wrapper chain
     */
    public static <W> boolean testWrapperChain(final W wrapper, final Predicate<? super W> predicate) {
        requireNonNull(wrapper, "wrapper is null");
        requireNonNull(predicate, "predicate is null");
        return travelWrapperChain(wrapper, w -> {
            if (predicate.test(w)) return Optional.of(true);
            else return Optional.empty();
        }).orElse(false);
    }

    /**
     * Gets the first instance on the wrapper chain satisfies the given {@code predicate}.
     * Exceptions thrown by the {@code predicate} are relayed to the caller.
     * <p>
     * The wrapper chain consists of wrapper itself, followed by the wrappers
     * obtained by repeatedly calling {@link Wrapper#unwrap_()}.
     * <p>
     * If multiple instances matches, outer wrapper instance win.
     *
     * @param wrapper   wrapper instance/wrapper chain
     * @param predicate inspect logic
     * @param <W>       the type of instances that be wrapped
     * @return an {@link Optional} containing the first instance on the wrapper chain that satisfy the given {@code predicate},
     * or an empty {@code Optional} if no instance satisfy the given {@code predicate}
     * @throws NullPointerException  if any arguments is null,
     *                               or any wrapper {@link Wrapper#unwrap_()} returns null,
     *                               or the adaptee of {@link WrapperAdapter} is null
     * @throws IllegalStateException if the adaptee of {@link WrapperAdapter} is an instance of {@link Wrapper}
     *                               or CYCLIC wrapper chain
     */
    @NonNull
    public static <W> Optional<W> findFirstInstanceOnWrapperChain(final W wrapper, final Predicate<? super W> predicate) {
        requireNonNull(wrapper, "wrapper is null");
        requireNonNull(predicate, "predicate is null");
        return travelWrapperChain(wrapper, w -> {
            if (predicate.test(w)) return Optional.of(w);
            else return Optional.empty();
        });
    }

    /**
     * Performs the given {@code action} for each instance on the wrapper chain
     * until all elements have been processed or the action throws an exception.
     * Exceptions thrown by the {@code action} are relayed to the caller.
     * <p>
     * The wrapper chain consists of wrapper itself, followed by the wrappers
     * obtained by repeatedly calling {@link Wrapper#unwrap_()}.
     *
     * @param wrapper wrapper instance/wrapper chain
     * @param action  The action to be performed for each instance
     * @param <W>     the type of instances that be wrapped
     * @throws NullPointerException  if any arguments is null,
     *                               or any wrapper {@link Wrapper#unwrap_()} returns null,
     *                               or the adaptee of {@link WrapperAdapter} is null
     * @throws IllegalStateException if the adaptee of {@link WrapperAdapter} is an instance of {@link Wrapper}
     *                               or CYCLIC wrapper chain
     * @see #travelWrapperChain(Object, Function)
     */
    public static <W> void forEachOnWrapperChain(final W wrapper, final Consumer<? super W> action) {
        requireNonNull(wrapper, "wrapper is null");
        requireNonNull(action, "action is null");
        travelWrapperChain(wrapper, w -> {
            action.accept(w);
            return Optional.empty();
        });
    }

    /**
     * Traverses the wrapper chain and applies the given {@code process} function to
     * each instance on the wrapper chain, returns the first non-empty({@link Optional#empty()}) result
     * of the process function, otherwise returns {@link Optional#empty()}.
     * Exceptions thrown by the process function are relayed to the caller.
     * <p>
     * The wrapper chain consists of wrapper itself, followed by the wrappers
     * obtained by repeatedly calling {@link Wrapper#unwrap_()}.
     *
     * @param wrapper wrapper instance
     * @param process process function
     * @param <W>     the type of instances that be wrapped
     * @param <T>     the return data type of process function
     * @return the first non-empty({@link Optional#empty()}) result of the process function,
     * otherwise an empty {@code Optional}
     * @throws NullPointerException  if any arguments is null,
     *                               or any wrapper {@link Wrapper#unwrap_()} returns null,
     *                               or the adaptee of {@link WrapperAdapter} is null
     * @throws IllegalStateException if the adaptee of {@link WrapperAdapter} is an instance of {@link Wrapper}
     *                               or CYCLIC wrapper chain
     * @see #forEachOnWrapperChain(Object, Consumer)
     * @see <a href="https://guava.dev/releases/33.4.6-jre/api/docs/com/google/common/base/Throwables.html#getRootCause(java.lang.Throwable)">
     * Guava method <code>Throwables#getRootCause(Throwable)</code></a>, the loop detection code using fast and slow pointers is adapted from it
     */
    @NonNull
    @SuppressWarnings("unchecked")
    public static <W, T> Optional<T> travelWrapperChain(
            final W wrapper, final Function<? super W, Optional<T>> process) {
        requireNonNull(wrapper, "wrapper is null");
        requireNonNull(process, "process is null");

        // keep a slow pointer that slowly walks the wrapper chain.
        // if the fast pointer ever catches the slower pointer, then there's a loop.
        Object fastPointer = wrapper, slowPointer = wrapper;
        boolean advanceSlowPointer = false;
        while (true) {
            // process the instance on wrapper chain
            Optional<T> result = process.apply((W) fastPointer);
            if (result.isPresent()) return result;

            // also process the adaptee for WrapperAdapter
            if (fastPointer instanceof WrapperAdapter) {
                Optional<T> r = process.apply((W) adapteeNonWrapper(fastPointer));
                if (r.isPresent()) return r;
            }

            if (!isWrapper(fastPointer)) return Optional.empty();
            fastPointer = unwrapNonNull(fastPointer);
            if (fastPointer == slowPointer) {
                throw new IllegalStateException("CYCLIC wrapper chain" +
                        ", duplicate instance of " + fastPointer.getClass().getName());
            }

            if (advanceSlowPointer) slowPointer = ((Wrapper<?>) slowPointer).unwrap_();
            advanceSlowPointer = !advanceSlowPointer; // only advance every other iteration
        }
    }

    /**
     * Gets adaptee of the given WrapperAdapter instance with {@code null} check and non-{@link Wrapper} type check.
     */
    @Contract(pure = true)
    private static Object adapteeNonWrapper(final Object wrapper) {
        final Object adaptee = ((WrapperAdapter<?>) wrapper).adaptee_();

        Supplier<String> msg = () -> "adaptee of WrapperAdapter(" + wrapper.getClass().getName() + ") is null";
        requireNonNull(adaptee, msg);

        if (isWrapper(adaptee)) {
            throw new IllegalStateException("adaptee(" + adaptee.getClass().getName() +
                    ") of WrapperAdapter(" + wrapper.getClass().getName() +
                    ") is an instance of Wrapper, adapting a Wrapper to a Wrapper is UNNECESSARY");
        }

        return adaptee;
    }

    /**
     * Unwraps the given wrapper instance with {@code null} check.
     */
    @Contract(pure = true)
    private static Object unwrapNonNull(final Object wrapper) {
        Object unwrap = ((Wrapper<?>) wrapper).unwrap_();
        Supplier<String> msg = () -> "unwrap of Wrapper(" + wrapper.getClass().getName() + ") is null";
        return requireNonNull(unwrap, msg);
    }

    private Inspector() {}
}
