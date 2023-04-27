package io.foldright.demo;

import io.foldright.wract.Wrapper;

import java.util.concurrent.Executor;


public class Demo {
    public static void main(String[] args) {
        ////////////////////////////////////////
        // prepare executor instance and wrappers
        ////////////////////////////////////////

        final Executor executor = Runnable::run;

        final LazyExecutorWrapper lazy = new LazyExecutorWrapper(executor);
        lazy.wractSetAttachment("busy", "very very busy!");

        final Executor chatty = new ChattyExecutorWrapper(lazy);

        ////////////////////////////////////////
        // inspect the wrapper chain
        ////////////////////////////////////////

        System.out.printf("chatty executor is LazyExecutor? %s\n",
                Wrapper.isInstanceOf(chatty, LazyExecutorWrapper.class));
        // print true

        String busy = Wrapper.getAttachment(chatty, "busy");
        System.out.printf("chatty executor is busy? %s\n", busy);
        // print true

        ////////////////////////////////////////
        // call executor
        ////////////////////////////////////////

        System.out.println();
        chatty.execute(() -> System.out.println("work!"));
    }
}
