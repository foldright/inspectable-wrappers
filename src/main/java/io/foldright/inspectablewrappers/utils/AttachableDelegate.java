package io.foldright.inspectablewrappers.utils;

import edu.umd.cs.findbugs.annotations.Nullable;
import io.foldright.inspectablewrappers.Attachable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.Objects.requireNonNull;


/**
 * A simple {@link Attachable} delegate implementation.
 * <p>
 * <strong>Note:</strong><br>
 * As the attachments are stored in {@code hash map}(threadsafe {@link ConcurrentHashMap}), the implementation of
 * the key type must meet the requirements of the {@code hash map}, which means that
 * a stable {@code hash code} and the ability to compare equality using {@code equals()} must be implemented.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @author Yang Fang (snoop dot fy at gmail dot com)
 * @author Zava Xu (zava dot kid at gmail dot com)
 * @see Attachable
 */
@ParametersAreNonnullByDefault
public class AttachableDelegate<K, V> implements Attachable<K, V> {
    private final ConcurrentMap<K, V> attachments = new ConcurrentHashMap<>();

    /**
     * Sets an attachment.
     */
    @Override
    public void setAttachment(final K key, final V value) {
        requireNonNull(key, "key is null");
        requireNonNull(value, "value is null");
        attachments.put(key, value);
    }

    /**
     * Gets the attachment of the given key.
     */
    @Nullable
    @Override
    public V getAttachment(final K key) {
        requireNonNull(key, "key is null");
        return attachments.get(key);
    }
}
