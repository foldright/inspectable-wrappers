package io.foldright.demo.integration;

import java.util.concurrent.Executor;


public class ExistedExecutorWrapper implements Executor {
    private final Executor executor;

    public ExistedExecutorWrapper(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void execute(Runnable command) {
        System.out.println("I'm existed executor, have nothing to do with ~inspectable~wrappers~.");
        executor.execute(command);
    }
}
