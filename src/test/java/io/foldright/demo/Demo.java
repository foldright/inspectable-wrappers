package io.foldright.demo;

import io.foldright.inspectablewrappers.Wrapper;

import java.util.concurrent.Executor;


public class Demo {
    public static void main(String[] args) {
        ////////////////////////////////////////
        // prepare executor instance and wrappers
        ////////////////////////////////////////

        final Executor executor = Runnable::run;

        final LazyExecutorWrapper lazy = new LazyExecutorWrapper(executor);
        lazy.setAttachment("busy", "very, very busy!");

        final Executor chatty = new ChattyExecutorWrapper(lazy);

        ////////////////////////////////////////
        // inspect the wrapper chain
        ////////////////////////////////////////

        System.out.println("Is chatty executor LazyExecutor? " +
                Wrapper.isInstanceOf(chatty, LazyExecutorWrapper.class));
        // print true

        String busy = Wrapper.getAttachment(chatty, "busy");
        System.out.println("Is chatty executor busy? " + busy);
        // print "very, very busy!"

        ////////////////////////////////////////
        // call executor
        ////////////////////////////////////////

        System.out.println();
        chatty.execute(() -> System.out.println("work!"));
    }
}
