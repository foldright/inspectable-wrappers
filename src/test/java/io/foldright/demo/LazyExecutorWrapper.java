package io.foldright.demo;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;
import io.foldright.wrain.Attachable;
import io.foldright.wrain.Wrapper;
import io.foldright.wrain.utils.AttachableDelegate;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.Executor;


@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public class LazyExecutorWrapper implements Executor, Wrapper<Executor>, Attachable {
    private final Executor executor;

    public LazyExecutorWrapper(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void execute(Runnable command) {
        System.out.println("I'm lazy, sleep before work");
        sleep();

        executor.execute(command);
    }

    private static void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Executor wrainUnwrap() {
        return executor;
    }

    private final Attachable attachable = new AttachableDelegate();

    @Override
    public void wrainSet(String key, Object value) {
        attachable.wrainSet(key, value);
    }

    @Nullable
    @Override
    public <V> V wrainGet(String key) {
        return attachable.wrainGet(key);
    }
}
