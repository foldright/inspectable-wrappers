package io.foldright.inspectablewrappers.utils;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;
import io.foldright.inspectablewrappers.Attachable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * A simple {@link Attachable} delegate implementation.
 *
 * <p>
 * <strong>Note:</strong>
 * </p>
 * As we store the attachments in hash map. The implementation of the key type
 * must meet the requirements of the hash map, which means that a stable hash
 * code and the ability to compare equality using equals() must be implemented.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @author Yang Fang (snoop dot fy at gmail dot com)
 * @see Attachable
 */
@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public class AttachableDelegate<K, V> implements Attachable<K, V> {
    private final ConcurrentMap<Object, Object> attachments = new ConcurrentHashMap<>();

    @Override
    public void setAttachment(K key, V value) {
        attachments.put(key, value);
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public V getAttachment(K key) {
        return (V) attachments.get(key);
    }
}
