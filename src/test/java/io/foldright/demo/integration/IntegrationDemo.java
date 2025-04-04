package io.foldright.demo.integration;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.foldright.demo.ChattyExecutorWrapper;
import io.foldright.inspectablewrappers.Attachable;
import io.foldright.inspectablewrappers.WrapperAdapter;
import io.foldright.inspectablewrappers.utils.AttachableDelegate;

import java.util.concurrent.Executor;

import static io.foldright.inspectablewrappers.Inspector.containsInstanceTypeOnWrapperChain;
import static io.foldright.inspectablewrappers.Inspector.getAttachmentFromWrapperChain;


public class IntegrationDemo {
    public static void main(String[] args) {
        final Executor executor = buildExecutorChain();

        ////////////////////////////////////////
        // inspect the executor(wrapper chain)
        ////////////////////////////////////////

        System.out.println("Is executor ExistedExecutorWrapper? " +
                containsInstanceTypeOnWrapperChain(executor, ExistedExecutorWrapper.class));
        // print true
        String adaptAttachment = getAttachmentFromWrapperChain(executor, "adapted-existed-executor-wrapper-msg");
        System.out.println("Adapted existed executor wrapper msg: " + adaptAttachment);
        // print "I'm an adapter of an existed executor which have nothing to do with ~inspectable~wrappers~."

        ////////////////////////////////////////
        // call executor(wrapper chain)
        ////////////////////////////////////////

        System.out.println();
        executor.execute(() -> System.out.println("I'm working."));
    }

    private static Executor buildExecutorChain() {
        final Executor base = Runnable::run;
        final Executor adapter = createExistedExecutorWrapperAdapter(base);
        return new ChattyExecutorWrapper(adapter);
    }

    private static Executor createExistedExecutorWrapperAdapter(Executor base) {
        final Executor existed = new ExistedExecutorWrapper(base);
        final ExistedExecutorWrapperAdapter adapter = new ExistedExecutorWrapperAdapter(base, existed);
        adapter.setAttachment_("adapted-existed-executor-wrapper-msg", "I'm an adapter of an existed executor which have nothing to do with ~inspectable~wrappers~.");
        return adapter;
    }

    /**
     * Adaption an existed wrapper(`ExistedExecutorWrapper`) without modifying it.
     */
    private static class ExistedExecutorWrapperAdapter implements Executor, WrapperAdapter<Executor>, Attachable<String, String> {
        private final Executor base;
        private final Executor adaptee;

        public ExistedExecutorWrapperAdapter(Executor base, Executor adaptee) {
            this.base = base;
            this.adaptee = adaptee;
        }

        @Override
        @NonNull
        public Executor unwrap_() {
            return base;
        }

        @Override
        @NonNull
        public Executor adaptee_() {
            return adaptee;
        }

        @Override
        public void execute(Runnable command) {
            adaptee.execute(command);
        }

        private final Attachable<String, String> attachable = new AttachableDelegate<>();

        @Override
        public void setAttachment_(@NonNull String key, @NonNull String value) {
            attachable.setAttachment_(key, value);
        }

        @Nullable
        @Override
        public String getAttachment_(@NonNull String key) {
            return attachable.getAttachment_(key);
        }
    }
}

/*
demo output:

Is executor ExistedExecutorWrapper? true
Adapted existed executor wrapper msg: I'm an adapter of an existed executor which have nothing to do with ~inspectable~wrappers~.

BlaBlaBla...
I'm an adapter of an existed executor which have nothing to do with ~inspectable~wrappers~.
I'm working.
 */
