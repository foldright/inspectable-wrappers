package io.foldright.wrain.utils;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;
import io.foldright.wrain.Attachable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * {@link Attachable} delegate/implementation.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @author Attachable
 */
@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public class AttachableDelegate implements Attachable {
    private final ConcurrentMap<String, Object> attachments = new ConcurrentHashMap<>();

    @Override
    public void wrainSet(String key, Object value) {
        attachments.put(key, value);
    }

    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T wrainGet(String key) {
        return (T) attachments.get(key);
    }
}
