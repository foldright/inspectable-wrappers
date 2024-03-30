package io.foldright.inspectablewrappers;

import edu.umd.cs.findbugs.annotations.NonNull;


/**
 * This {@code Wrapper} interface is used to be implemented by wrapper classes,
 * make an <strong>inspectable wrapper chain</strong>(linked list).
 * <p>
 * <strong>Note about wrapper chain:</strong>
 * <ul>
 * <li>The wrapper chain consists of wrapper itself, followed by the wrappers
 *     obtained by repeatedly calling {@link Wrapper#unwrap()}<br>
 *     <img src="https://github.com/foldright/inspectable-wrappers/assets/1063891/7bb7db14-2dee-44e6-b843-9817a94eef44"
 *      width="350" alt="Wrapper Chain">
 * <li>The last instance of wrapper chain is NEVER an instance of {@link Wrapper}
 * <li>Uses the static methods of {@link Inspector} to inspect the wrapper chain
 * </ul>
 *
 * @param <T> the type of instances that be wrapped
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @author Zava Xu (zava dot kid at gmail dot com)
 * @see Attachable
 * @see WrapperAdapter
 * @see Inspector
 */
public interface Wrapper<T> {
    /**
     * Returns the underlying instance that be wrapped.
     * This method also make the wrapper instances as a wrapper chain(linked list).
     * <p>
     * <strong>Specification contracts:</strong>
     * <p>
     * Do NOT return {@code null} which makes no sense.<br>
     * If returns {@code null}, the inspection operations of {@link Inspector} will
     * throw {@link NullPointerException} when touch the {@code unwrap}.
     */
    @NonNull
    T unwrap();
}
