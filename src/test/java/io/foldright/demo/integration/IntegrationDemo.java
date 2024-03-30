package io.foldright.demo.integration;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;
import io.foldright.demo.ChattyExecutorWrapper;
import io.foldright.inspectablewrappers.Attachable;
import io.foldright.inspectablewrappers.WrapperAdapter;
import io.foldright.inspectablewrappers.utils.AttachableDelegate;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.Executor;

import static io.foldright.inspectablewrappers.Inspector.containsInstanceOnWrapperChain;
import static io.foldright.inspectablewrappers.Inspector.getAttachmentFromWrapperChain;


@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public class IntegrationDemo {
    public static void main(String[] args) {
        final Executor executor = buildExecutorChain();

        ////////////////////////////////////////
        // inspect the executor(wrapper chain)
        ////////////////////////////////////////

        System.out.println("Is executor ExistedExecutorWrapper? " +
                containsInstanceOnWrapperChain(executor, ExistedExecutorWrapper.class));
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

        final ExistedExecutorWrapper existed = new ExistedExecutorWrapper(base);
        final ExistedExecutorWrapperAdapter adapter = new ExistedExecutorWrapperAdapter(existed);
        adapter.setAttachment("adapted-existed-executor-wrapper-msg", "I'm an adapter of an existed executor which have nothing to do with ~inspectable~wrappers~.");

        return new ChattyExecutorWrapper(adapter);
    }

    /**
     * Adaption an existed wrapper(`ExistedExecutorWrapper`) without modifying it.
     */
    private static class ExistedExecutorWrapperAdapter implements Executor, WrapperAdapter<Executor>, Attachable<String, String> {
        private final ExistedExecutorWrapper adaptee;

        public ExistedExecutorWrapperAdapter(ExistedExecutorWrapper adaptee) {
            this.adaptee = adaptee;
        }

        @Override
        public Executor unwrap() {
            return adaptee.getExecutor();
        }

        @Override
        public Executor adaptee() {
            return adaptee;
        }

        @Override
        public void execute(Runnable command) {
            adaptee.execute(command);
        }

        private final Attachable<String, String> attachable = new AttachableDelegate<>();

        @Override
        public void setAttachment(String key, String value) {
            attachable.setAttachment(key, value);
        }

        @Nullable
        @Override
        public String getAttachment(String key) {
            return attachable.getAttachment(key);
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
