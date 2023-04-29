package io.foldright.wrain;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;
import io.foldright.wrain.utils.AttachableDelegate;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Proxy;
import java.util.function.Predicate;


/**
 * This {@code Wrapper} interface is used to be implemented by wrapper classes,
 * make {@code wrapper instances} as an <b>inspectable wrapper chain</b>(linked list).
 * <p>
 * All instance method names prefix "{@code wrain}" to avoid potential name conflict with subclass method names.
 *
 * @param <T> the type of instances that be wrapped
 * @author Jerry Lee (oldratlee at gmail dot com)
 */
@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
@FunctionalInterface
public interface Wrapper<T> {
    /**
     * Returns the underlying instance that be wrapped.
     * <p>
     * this method also make the wrapper instances as a wrapper chain(linked list).
     */
    T wrainUnwrap();

    /**
     * Reports whether any wrapper on the wrapper chain matches the given type.
     * <p>
     * The wrapper chain consists of wrapper itself, followed by the wrappers
     * obtained by repeatedly calling {@link #wrainUnwrap()}.
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
     * obtained by repeatedly calling {@link #wrainUnwrap()}.
     *
     * @param wrapper   wrapper instance/wrapper chain
     * @param predicate check logic
     * @param <W>       the type of instances that be wrapped
     * @return return {@code false} if no wrapper on the wrapper chain satisfy {@code predicate},
     * otherwise return {@code true}
     */
    @SuppressWarnings("unchecked")
    static <W> boolean check(final W wrapper, final Predicate<? super W> predicate) {
        for (Object w = wrapper; w instanceof Wrapper; w = ((Wrapper<?>) w).wrainUnwrap()) {
            if (predicate.test((W) w)) return true;
        }
        return false;
    }

    /**
     * Retrieves the attachment of wrapper of given key on the wrapper chain
     * by calling {@link Attachable#wrainGet(String)}.
     * <p>
     * The wrapper chain consists of wrapper itself, followed by the wrappers
     * obtained by repeatedly calling {@link #wrainUnwrap()}.
     * <p>
     * If the key exists in multiple wrappers, outer wrapper win.
     *
     * @param wrapper wrapper instance
     * @param key     attachment key
     * @param <W>     the type of instances that be wrapped
     */
    @Nullable
    @SuppressWarnings("unchecked")
    static <W, V> V getAttachment(final W wrapper, final String key) {
        for (Object w = wrapper; w instanceof Wrapper; w = ((Wrapper<W>) w).wrainUnwrap()) {
            if (!(w instanceof Attachable)) continue;

            V value = ((Attachable) w).wrainGet(key);
            if (value != null) return value;
        }
        return null;
    }

    /**
     * Adapt an existed wrapper with {@link Wrapper} interface.
     *
     * @param instance         the adapted existed wrapper
     * @param underlying       the underlying wrapper of the adapted existed wrapper
     * @param wrapperInterface the interface type of wrapper
     * @param <T>              the type of instances that be wrapped
     * @return adapted wrapper
     * @see #dynamicAdaptWithAttachable(Object, Object, Class)
     */
    @SuppressWarnings("unchecked")
    static <T> T dynamicAdapt(T instance, T underlying, Class<T> wrapperInterface) {
        Attachable attachable = new AttachableDelegate();
        return (T) Proxy.newProxyInstance(instance.getClass().getClassLoader(), new Class[]{wrapperInterface, Wrapper.class},
                (proxy, method, args) -> {
                    final Class<?> declaringClass = method.getDeclaringClass();
                    // contains only one method wrainUnwrap
                    if (declaringClass == Attachable.class) return method.invoke(attachable, args);
                    method.invoke(instance, args);
                });
    }

    /**
     * Adapt an existed wrapper with {@link Wrapper} interface with the {@link Attachable} ability.
     *
     * @param instance         the adapted existed wrapper
     * @param underlying       the underlying wrapper of the adapted existed wrapper
     * @param wrapperInterface the interface type of wrapper
     * @param <T>              the type of instances that be wrapped
     * @return adapted wrapper
     * @see #dynamicAdapt(Object, Object, Class)
     */
    @SuppressWarnings("unchecked")
    static <T> T dynamicAdaptWithAttachable(T instance, T underlying, Class<T> wrapperInterface) {
        Attachable attachable = new AttachableDelegate();
        return (T) Proxy.newProxyInstance(
                instance.getClass().getClassLoader(),
                new Class[]{wrapperInterface, Wrapper.class, Attachable.class},
                (proxy, method, args) -> {
                    final Class<?> declaringClass = method.getDeclaringClass();
                    // contains only one method wrainUnwrap
                    if (declaringClass == Wrapper.class) return underlying;
                    if (declaringClass == Attachable.class) return method.invoke(attachable, args);
                    return method.invoke(instance, args);
                });
    }
}
