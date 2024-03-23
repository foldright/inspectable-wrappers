package io.foldright.inspectablewrappers;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;


/**
 * This {@code Attachable} interface is used to be implemented by wrapper classes,
 * provide the attachment storage ability.
 * <p>
 * Retrieves the attachment from wrapper chain the static method {@link Inspector#getAttachment(Object, Object)}.
 * <p>
 * Provided {@link io.foldright.inspectablewrappers.utils.AttachableDelegate AttachableDelegate}
 * as a simple delegate implementation.
 *
 * @param <K> the key type, requirements depending on which storage is used
 * @param <V> the value type to be stored
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @author Yang Fang (snoop dot fy at gmail dot com)
 * @see Inspector#getAttachment(Object, Object)
 * @see io.foldright.inspectablewrappers.utils.AttachableDelegate
 */
public interface Attachable<K, V> {
    /**
     * Sets an attachment.
     *
     * @param key   the attachment key
     * @param value the attachment value
     * @throws NullPointerException if any arguments is null
     */
    void setAttachment(@NonNull K key, @NonNull V value);

    /**
     * Gets the attachment value for the given key.
     *
     * @param key the attachment key
     * @return return the attachment value, or {@code null} if contains no attachment for the key
     * @throws NullPointerException if key argument is null
     * @throws ClassCastException   if the return value is not type {@code <V>}
     * @see Inspector#getAttachment(Object, Object)
     */
    @Nullable
    V getAttachment(@NonNull K key);
}
