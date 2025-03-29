package io.foldright.demo;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.foldright.inspectablewrappers.Attachable;
import io.foldright.inspectablewrappers.Wrapper;
import io.foldright.inspectablewrappers.utils.AttachableDelegate;

import java.util.concurrent.Executor;


public class LazyExecutorWrapper implements Executor, Wrapper<Executor>, Attachable<String, String> {
    private final Executor executor;

    public LazyExecutorWrapper(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void execute(Runnable command) {
        System.out.println("I'm lazy, sleep before work.");
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
    @NonNull
    public Executor unwrap_() {
        return executor;
    }

    private final Attachable<String, String> attachable = new AttachableDelegate<>();

    @Override
    public void setAttachment_(@NonNull String key, @NonNull String value) {
        attachable.setAttachment_(key, value);
    }

    @Nullable
    @Override
    public String getAttachment_(@NonNull String key) {
        return attachable.getAttachment_(key);
    }
}
