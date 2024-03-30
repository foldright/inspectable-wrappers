package io.foldright.inspectablewrappers;

import edu.umd.cs.findbugs.annotations.NonNull;


/**
 * This {@code WrapperAdapter} interface is used to
 * adapt an existed wrapper instance to type {@link Wrapper} without modifying it.
 * <p>
 * The existed wrapper may not be able to be modified to integrate with {@code inspectable wrappers};
 * Integration with {@code inspectable wrappers} means implementation the {@link Wrapper} interface.
 * <p>
 * The wrapper chain contained {@code WrapperAdapter} looks like:
 * <p>
 * <img src="https://github.com/foldright/inspectable-wrappers/assets/1063891/eeaf02a9-9887-4ed1-a160-a12f92338d7b"
 * width="350" alt="Wrapper Chain contains WrapperAdapter">
 * <p>
 * Provided {@link io.foldright.inspectablewrappers.utils.WrapperAdapterUtils WrapperAdapterUtils}
 * to create {@link WrapperAdapter} instances of the given biz interface type by the underlying
 * and adaptee instances without writing boilerplate codes of creating new adapter classes.
 *
 * @param <T> the type of instances that be wrapped
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @author Zava Xu (zava dot kid at gmail dot com)
 * @see Wrapper
 * @see io.foldright.inspectablewrappers.utils.WrapperAdapterUtils
 */
public interface WrapperAdapter<T> extends Wrapper<T> {
    /**
     * Returns the adapted/existed wrapper.
     * <p>
     * <strong>Specification contracts:</strong>
     * <ul>
     * <li>Do NOT return {@code null} which makes no sense.<br>
     *     If returns {@code null}, the inspection operations of {@link Inspector} will
     *     throw {@link NullPointerException} when touch adaptee.
     * <li>The adaptee MUST NOT an instance of {@link Wrapper},
     *     since adapting a {@link Wrapper} to a {@link Wrapper} is UNNECESSARY.<br>
     *     If adapting a {@link Wrapper}, the inspection operations of {@link Inspector} will
     *     throw {@link IllegalStateException} when touch the {@link Wrapper} type adaptee.
     * </ul>
     *
     * @return the adapted wrapper
     * @see Inspector
     */
    @NonNull
    T adaptee();
}
