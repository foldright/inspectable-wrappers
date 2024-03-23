package io.foldright.inspectablewrappers;

import edu.umd.cs.findbugs.annotations.NonNull;


/**
 * This {@code Wrapper} interface is used to be implemented by wrapper classes,
 * make an <strong>inspectable wrapper chain</strong>(linked list).
 * <p>
 * <strong>Note about wrapper chain:</strong>
 * <ul>
 *   <li>The wrapper chain consists of wrapper itself, followed by the wrappers
 *       obtained by repeatedly calling {@link Wrapper#unwrap()}
 *   <li>The last instance of wrapper chain is never type {@link Wrapper}
 *   <li>Uses the static methods of {@link Inspector} to inspect the wrapper chain
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
     * <strong>Note:</strong> Do NOT return {@code null} which makes no sense.
     */
    @NonNull
    T unwrap();
}
