package io.foldright.inspectablewrappers.utils;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.foldright.inspectablewrappers.Attachable;
import io.foldright.inspectablewrappers.Inspector;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.Objects.requireNonNull;


/**
 * A simple {@link Attachable} delegate implementation.
 * <p>
 * <strong>Note:</strong><br>
 * As the attachments are stored in {@code hash map}(threadsafe {@link ConcurrentHashMap}),
 * the implementation of the key type must meet the requirements of the {@code hash map}, which means that
 * a stable {@code hash code} and the ability to compare equality using {@code equals()} must be implemented.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @author Yang Fang (snoop dot fy at gmail dot com)
 * @author Zava Xu (zava dot kid at gmail dot com)
 * @see Attachable
 */
public class AttachableDelegate<K, V> implements Attachable<K, V> {
    private final ConcurrentMap<K, V> attachments = new ConcurrentHashMap<>();

    /**
     * Sets an attachment.
     *
     * @param key   the attachment key
     * @param value the attachment value
     * @throws NullPointerException if any arguments is null
     */
    @Override
    public void setAttachment_(@NonNull K key, @NonNull V value) {
        requireNonNull(key, "key is null");
        requireNonNull(value, "value is null");
        attachments.put(key, value);
    }

    /**
     * Gets the attachment value for the given key.
     *
     * @param key the attachment key
     * @return return the attachment value, or {@code null} if contains no attachment for the key
     * @throws NullPointerException if key argument is null
     * @throws ClassCastException   if the return value is not type {@code <V>}
     * @see Inspector#getAttachmentFromWrapperChain(Object, Object)
     */
    @Nullable
    @Override
    public V getAttachment_(@NonNull K key) {
        requireNonNull(key, "key is null");
        return attachments.get(key);
    }
}
