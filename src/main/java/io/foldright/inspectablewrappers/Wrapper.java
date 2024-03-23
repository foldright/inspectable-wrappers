package io.foldright.inspectablewrappers;

import edu.umd.cs.findbugs.annotations.NonNull;


/**
 * This {@code Wrapper} interface is used to be implemented by wrapper classes,
 * make an <b>inspectable wrapper chain</b>(linked list).
 * <p>
 * <b><i>Note about wrapper chain:<br></i></b>
 * <ul>
 *   <li>The wrapper chain consists of wrapper itself, followed by the wrappers
 *       obtained by repeatedly calling {@link Wrapper#unwrap()}
 *   <li>The last instance of wrapper chain is never type {@link Wrapper}
 *   <li>Uses the static methods in {@link Inspector} to inspect the wrapper chain
 * </ul>
 *
 * @param <T> the type of instances that be wrapped
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @author Zava (zava dot kid at gmail dot com)
 * @see Attachable
 * @see WrapperAdapter
 * @see Inspector
 */
public interface Wrapper<T> {
    /**
     * Returns the underlying instance that be wrapped.
     * <p>
     * this method also make the wrapper instances as a wrapper chain(linked list).
     */
    @NonNull
    T unwrap();
}
