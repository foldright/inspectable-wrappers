package io.foldright.demo;

import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;
import io.foldright.wract.Wrapper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.Executor;


@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
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
    public Executor wractUnwrap() {
        return executor;
    }
}
