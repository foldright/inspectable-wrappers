package io.foldright.demo.integration;

import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.Executor;


@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public class ExistedExecutorWrapper implements Executor {
    private final Executor executor;

    public ExistedExecutorWrapper(Executor executor) {
        this.executor = executor;
    }

    public Executor getExecutor() {
        return executor;
    }

    @Override
    public void execute(Runnable command) {
        System.out.println("I'm a adapter of an existed executor which have nothing to do with ~inspectable~wrappers~.");
        executor.execute(command);
    }
}
