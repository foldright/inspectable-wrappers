package io.foldright.inspectablewrappers;

import edu.umd.cs.findbugs.annotations.NonNull;


/**
 * This {@code WrapperAdapter} interface is used to
 * adapt an existed wrapper instance to type {@link Wrapper} without modifying it.
 * <p>
 * The existed wrapper may not be able to be modified to integrate with {@code inspectable wrappers},
 * aka. implements the {@link Wrapper} interface and/or {@link Attachable} interface.
 *
 * @param <T> the type of instances that be wrapped
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @author Zava (zava dot kid at gmail dot com)
 * @see Wrapper
 */
public interface WrapperAdapter<T> extends Wrapper<T> {
    /**
     * Returns the adapted/existed wrapper.
     * <p>
     * <b><i>Note:<br></i></b>
     * <ul>
     *   <li>The adaptee MUST not a {@link Wrapper},
     *       since adapting a {@link Wrapper} to a {@link Wrapper} is unnecessary.
     *   <li>If adapting a {@link Wrapper}, the inspection operations of {@link Inspector} will
     *       throw {@link IllegalStateException} when touch the {@link Wrapper} type adaptee.
     * </ul>
     *
     * @return the adapted wrapper.
     * @see Inspector
     */
    @NonNull
    T adaptee();
}
