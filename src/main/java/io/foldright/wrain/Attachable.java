package io.foldright.wrain;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;


/**
 * This {@code Attachable} interface is used to be implemented by wrapper classes,
 * provide the attachment storage ability.
 * <p>
 * All instance method names prefix "{@code wrain}" to avoid potential name conflict with subclass method names.
 * <p>
 * Retrieve the attachment from wrapper chain(wrapper instances implement interface {@link Wrapper})
 * by static method {@link Wrapper#getAttachment(Object, String)}.
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
     * @param key   attachment key
     * @param value attachment value
     */
    void wrainSet(String key, Object value);

    /**
     * Get the attachment of the given key.
     *
     * @param key attachment key
     * @param <V> attachment value type
     * @return return attachment value, or {@code null} if contains no attachment for the key
     */
    @Nullable
    <V> V wrainGet(String key);
}
