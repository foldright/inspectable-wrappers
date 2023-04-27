package io.foldright.wract;

import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Predicate;


/**
 * Wrapper interface is used to be implemented by wrapper class.
 * <p>
 * All instance method names prefix "{@code wract}" to avoid potential name conflict.
 *
 * @param <T> the type of instance that be wrapped
 */
@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public interface Wrapper<T> {
    /**
     * Returns the underlying instance that be wrapped.
     * <p>
     * this method also make the wrapper instances as a linked list.
     */
    T wractUnwrap();

    /**
     * A util method to check the wrapper chain.
     *
     * @param wrapper   wrapper instance
     * @param predicate check logic
     * @param <T>       the type of instance that be wrapped
     * @return return {@code false} if no wrapper satisfy {@code predicate}, otherwise return {@code true}
     */
    @SuppressWarnings("unchecked")
    static <T> boolean check(T wrapper, Predicate<? super T> predicate) {
        while (wrapper instanceof Wrapper) {
            if (predicate.test(wrapper)) {
                return true;
            }
            wrapper = ((Wrapper<T>) wrapper).wractUnwrap();
        }
        return false;
    }
}
