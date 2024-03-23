package io.foldright.demo.integration;

import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;
import io.foldright.demo.ChattyExecutorWrapper;
import io.foldright.inspectablewrappers.Wrapper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.Executor;


@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public class IntegrationDemo {
    public static void main(String[] args) {
        final Executor executor = buildExecutorChain();

        ////////////////////////////////////////
        // inspect the executor(wrapper chain)
        ////////////////////////////////////////

        System.out.println("Is executor chatty? " +
                Wrapper.isInstanceOf(executor, ChattyExecutorWrapper.class));
        // print true
        System.out.println("Is executor IntegrateExistedExecutor? " +
                Wrapper.isInstanceOf(executor, IntegrateExistedExecutorWrapper.class));
        // print true

        ////////////////////////////////////////
        // call executor(wrapper chain)
        ////////////////////////////////////////

        System.out.println();
        executor.execute(() -> System.out.println("I'm working."));
    }

    private static Executor buildExecutorChain() {
        final Executor base = Runnable::run;

        final ExistedExecutorWrapper existed = new ExistedExecutorWrapper(base);
        final IntegrateExistedExecutorWrapper integrate = new IntegrateExistedExecutorWrapper(existed);

        return new ChattyExecutorWrapper(integrate);
    }

    /**
     * Integrate an existed executor wrapper(`ExistedExecutorWrapper`) without modification
     */
    private static class IntegrateExistedExecutorWrapper implements Executor, Wrapper<Executor> {
        private final ExistedExecutorWrapper existedExecutorWrapper;

        public IntegrateExistedExecutorWrapper(ExistedExecutorWrapper existedExecutorWrapper) {
            this.existedExecutorWrapper = existedExecutorWrapper;
        }

        @Override
        public Executor unwrap() {
            return existedExecutorWrapper.getExecutor();
        }

        @Override
        public void execute(Runnable command) {
            existedExecutorWrapper.execute(command);
        }
    }
}

/*
demo output:

Is executor chatty? true
Is executor IntegrateExistedExecutor? true

BlaBlaBla...
I'm existed executor, have nothing to do with ~inspectable~wrappers~.
I'm working.
 */
