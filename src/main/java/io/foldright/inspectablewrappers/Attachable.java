package io.foldright.inspectablewrappers;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;


/**
 * This {@code Attachable} interface is used to be implemented by wrapper classes,
 * provide the attachment storage ability.
 * <p>
 * Retrieve the attachment from wrapper chain(wrapper instances implement interface {@link Wrapper})
 * by static method {@link Wrapper#getAttachment(Object, Object)}.
 * <p>
 * Provide {@link io.foldright.inspectablewrappers.utils.AttachableDelegate AttachableDelegate}
 * as a simple delegate implementation.
 *
 * @param <K> the key type, requirements depending on which storage is used
 * @param <V> the value type to be stored
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @author Yang Fang (snoop dot fy at gmail dot com)
 * @see Wrapper#getAttachment(Object, Object)
 * @see io.foldright.inspectablewrappers.utils.AttachableDelegate
 */
@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public interface Attachable<K, V> {
    /**
     * Sets an attachment.
     *
     * @param key   the attachment key
     * @param value the attachment value
     */
    void setAttachment(K key, V value);

    /**
     * Gets the attachment of the given key.
     *
     * @param key the attachment key
     * @return return the attachment value, or {@code null} if contains no attachment for the key
     */
    @Nullable
    V getAttachment(K key);
}
