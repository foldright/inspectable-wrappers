package io.foldright.demo.integration;

import io.foldright.demo.ChattyExecutorWrapper;
import io.foldright.inspectablewrappers.Attachable;
import io.foldright.inspectablewrappers.utils.AttachableDelegate;
import io.foldright.inspectablewrappers.utils.WrapperAdapterUtils;

import java.util.concurrent.Executor;

import static io.foldright.inspectablewrappers.Inspector.containsInstanceOnWrapperChain;
import static io.foldright.inspectablewrappers.Inspector.getAttachmentFromWrapperChain;


public class IntegrationDemoUsingWrapperAdapterUtils {
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
        final Executor adapter = createExistedExecutorWrapperAdapter(base);
        return new ChattyExecutorWrapper(adapter);
    }

    private static Executor createExistedExecutorWrapperAdapter(Executor base) {
        final Executor existed = new ExistedExecutorWrapper(base);

        Attachable<String, String> attachable = new AttachableDelegate<>();
        attachable.setAttachment("adapted-existed-executor-wrapper-msg", "I'm an adapter of an existed executor which have nothing to do with ~inspectable~wrappers~.");

        return WrapperAdapterUtils.createWrapperAdapter(Executor.class, base, existed, attachable);
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
