package io.foldright.wrain;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;


/**
 * This {@code Attachable} interface is used to be implemented by wrapper classes,
 * provide the attachment storage ability.
 * <p>
 * Retrieve the attachment from wrapper chain(wrapper instances implement interface {@link Wrapper})
 * by static method {@link Wrapper#getAttachment(Object, String)}.
 * <p>
 * Provide {@link io.foldright.wrain.utils.AttachableDelegate AttachableDelegate} as a simple delegate implementation.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see Wrapper#getAttachment(Object, String)
 * @see io.foldright.wrain.utils.AttachableDelegate
 */
@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public interface Attachable {
    /**
     * Sets an attachment.
     *
     * @param key   the attachment key
     * @param value the attachment value
     */
    void setAttachment(String key, Object value);

    /**
     * Get the attachment of the given key.
     *
     * @param key the attachment key
     * @param <V> the attachment value type
     * @return return the attachment value, or {@code null} if contains no attachment for the key
     */
    @Nullable
    <V> V getAttachment(String key);
}
