package io.foldright.inspectablewrappers;

import edu.umd.cs.findbugs.annotations.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;


/**
 * This {@code Wrapper} interface is used to be implemented by wrapper classes,
 * make {@code wrapper instances} as an <b>inspectable wrapper chain</b>(linked list).
 *
 * @param <T> the type of instances that be wrapped
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see Attachable
 * @see WrapperAdapter
 * @see Inspector
 */
@ParametersAreNonnullByDefault
public interface Wrapper<T> {
    /**
     * Returns the underlying instance that be wrapped.
     * <p>
     * this method also make the wrapper instances as a wrapper chain(linked list).
     */
    @NonNull
    T unwrap();


}
