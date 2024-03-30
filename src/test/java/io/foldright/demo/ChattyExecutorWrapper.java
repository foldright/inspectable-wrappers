package io.foldright.demo;

import io.foldright.inspectablewrappers.Wrapper;

import java.util.concurrent.Executor;


public class ChattyExecutorWrapper implements Executor, Wrapper<Executor> {
    private final Executor executor;

    public ChattyExecutorWrapper(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void execute(Runnable command) {
        System.out.println("BlaBlaBla...");
        executor.execute(command);
    }

    @Override
    public Executor unwrap() {
        return executor;
    }
}
