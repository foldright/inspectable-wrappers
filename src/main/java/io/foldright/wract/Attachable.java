package io.foldright.wract;

import edu.umd.cs.findbugs.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;


/**
 * Attachable.
 * <p>
 * All instance method names prefix "{@code wract}" to avoid potential name conflict with subclass method names.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 */
@ParametersAreNonnullByDefault
public interface Attachable {
    /**
     * Sets an attachment.
     *
     * @param key   attachment key
     * @param value attachment value
     */
    void wractSetAttachment(String key, Object value);

    /**
     * Get the attachment.
     *
     * @param key attachment key
     * @param <V> attachment value type
     * @return return attachment value, or {@code null} if contains no attachment for the key
     */
    @Nullable
    <V> V wractGetAttachment(String key);
}
