package io.foldright.demo;

import java.util.concurrent.Executor;

import static io.foldright.inspectablewrappers.Inspector.containsInstanceTypeOnWrapperChain;
import static io.foldright.inspectablewrappers.Inspector.getAttachmentFromWrapperChain;


public class Demo {
    public static void main(String[] args) {
        final Executor executor = buildExecutorChain();

        ////////////////////////////////////////
        // inspect the executor(wrapper chain)
        ////////////////////////////////////////

        System.out.println("Is executor lazy? " +
                containsInstanceTypeOnWrapperChain(executor, LazyExecutorWrapper.class));
        // print true

        String busy = getAttachmentFromWrapperChain(executor, "busy");
        System.out.println("Is executor busy? " + busy);
        // print "very, very busy!"

        ////////////////////////////////////////
        // call executor(wrapper chain)
        ////////////////////////////////////////

        System.out.println();
        executor.execute(() -> System.out.println("I'm working."));
    }

    /**
     * prepare executor instances/wrappers, build the executor/wrapper chain
     **/
    private static Executor buildExecutorChain() {
        final Executor base = Runnable::run;

        final LazyExecutorWrapper lazy = new LazyExecutorWrapper(base);
        lazy.setAttachment_("busy", "very, very busy!");

        return new ChattyExecutorWrapper(lazy);
    }
}

/*
demo output:

Is executor lazy? true
Is executor busy? very, very busy!

BlaBlaBla...
I'm lazy, sleep before work.
I'm working.
 */
