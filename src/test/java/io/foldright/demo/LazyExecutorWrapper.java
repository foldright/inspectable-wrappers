package io.foldright.demo;

import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;
import io.foldright.wract.Attachable;
import io.foldright.wract.Wrapper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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
    public Executor wractUnwrap() {
        return executor;
    }

    private final ConcurrentMap<String, Object> attachments = new ConcurrentHashMap<>();

    @Override
    public void wractSetAttachment(String key, Object value) {
        attachments.put(key, value);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <V> V wractGetAttachment(String key) {
        return (V) attachments.get(key);
    }
}
