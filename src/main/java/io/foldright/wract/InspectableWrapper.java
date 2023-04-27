package io.foldright.wract;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;


/**
 * WrapperContext interface is used to be implemented by wrapper class,
 * provides the infos of wrapper instance.
 * <p>
 * All instance method names prefix "{@code wract}" to avoid potential name conflict.
 *
 * @param <T> the type of instance that be wrapped
 */
@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public interface InspectableWrapper<T> extends Wrapper<T> {
    /**
     * Returns the extra infos(attachments) of wrapper instance.
     *
     * @return extra infos of WrapperContext. Return {@code null} means no related extra infos.
     */
    @Nullable
    default Map<String, Object> wractAttachments() {
        return null;
    }

    /**
     * Retrieve the attachment from the wrapper(WrapperContext) chain.
     * <p>
     * if the key exists in multiple wrappers, outer wrapper win.
     *
     * @param wrapper wrapper instance
     * @param key     attachment key
     * @param <T>     the type of instance that be wrapped
     */
    @Nullable
    @SuppressWarnings("unchecked")
    static <T> Object getAttachment(T wrapper, String key) {
        while (wrapper instanceof InspectableWrapper) {
            Map<String, Object> attachments = ((InspectableWrapper<?>) wrapper).wractAttachments();
            if (attachments == null) continue;

            Object att = attachments.get(key);
            if (att != null) return att;

            wrapper = ((InspectableWrapper<T>) wrapper).wractUnwrap();
        }
        return null;
    }
}
