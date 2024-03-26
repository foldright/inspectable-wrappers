package io.foldright.inspectablewrappers;

import edu.umd.cs.findbugs.annotations.NonNull;


/**
 * Adaption an existed wrapper without modifying it.
 * <p>
 * The existed wrapper may not be able to be modified to integrate with {@code inspectable wrappers},
 * aka. implements the {@link Wrapper} interface and/or {@link Attachable} interface.
 *
 * @param <T> the type of instances that be wrapped
 * @see Wrapper
 */
public interface WrapperAdapter<T> extends Wrapper<T> {
    /**
     * Returns the adapted/existed wrapper.
     *
     * @return the adapted wrapper.
     * @see Inspector#isInstanceOf(Object, Class)
     */
    @NonNull
    T adaptee();
}
