package io.foldright.demo;

import io.foldright.inspectablewrappers.Inspector;

import java.util.concurrent.Executor;


public class Demo {
    public static void main(String[] args) {
        final Executor executor = buildExecutorChain();

        ////////////////////////////////////////
        // inspect the executor(wrapper chain)
        ////////////////////////////////////////

        System.out.println("Is executor lazy? " +
                Inspector.isInstanceOf(executor, LazyExecutorWrapper.class));
        // print true

        String busy = Inspector.getAttachment(executor, "busy");
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
        lazy.setAttachment("busy", "very, very busy!");

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
