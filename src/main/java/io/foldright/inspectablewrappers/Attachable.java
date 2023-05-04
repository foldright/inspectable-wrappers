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
 * Provide {@link io.foldright.inspectablewrappers.utils.AttachableDelegate AttachableDelegate} as a simple delegate implementation.
 *
 * @param <Key> the key type, requirements depending on which storage you're using
 * @param <Value> the value type you want to store
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see Wrapper#getAttachment(Object, Object)
 * @see io.foldright.inspectablewrappers.utils.AttachableDelegate
 */
@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public interface Attachable<Key, Value> {
    /**
     * Sets an attachment.
     *
     * @param key   the attachment key
     * @param value the attachment value
     */
    void setAttachment(Key key, Value value);

    /**
     * Get the attachment of the given key.
     *
     * @param key the attachment key
     * @return return the attachment value, or {@code null} if contains no attachment for the key
     */
    @Nullable
    Value getAttachment(Key key);
}
