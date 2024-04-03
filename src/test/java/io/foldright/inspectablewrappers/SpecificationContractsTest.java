package io.foldright.inspectablewrappers;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Executor;

import static io.foldright.inspectablewrappers.Inspector.verifyWrapperChainContracts;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;


class SpecificationContractsTest {

    private static final Executor DUMMY = command -> {
    };

    @Test
    void test_null_unwrap() {
        Executor w = new WrapperImpl(null);

        NullPointerException e = assertThrowsExactly(NullPointerException.class, () -> verifyWrapperChainContracts(w));
        String expected = "unwrap of Wrapper(io.foldright.inspectablewrappers.SpecificationContractsTest$WrapperImpl) is null";
        assertEquals(expected, e.getMessage());
    }

    @Test
    void test_null_adaptee() {
        Executor w = new WrapperAdapterImpl(DUMMY, null);

        NullPointerException e = assertThrowsExactly(NullPointerException.class, () -> verifyWrapperChainContracts(w));
        String expected = "adaptee of WrapperAdapter(io.foldright.inspectablewrappers.SpecificationContractsTest$WrapperAdapterImpl) is null";
        assertEquals(expected, e.getMessage());
    }

    @Test
    void test_Wrap_type_adaptee() {
        Executor w = new WrapperAdapterImpl(DUMMY, new WrapperImpl(null));

        IllegalStateException e = assertThrowsExactly(IllegalStateException.class, () -> verifyWrapperChainContracts(w));
        String expected = "adaptee(io.foldright.inspectablewrappers.SpecificationContractsTest$WrapperImpl)" +
                " of WrapperAdapter(io.foldright.inspectablewrappers.SpecificationContractsTest$WrapperAdapterImpl)" +
                " is an instance of Wrapper, adapting a Wrapper to a Wrapper is unnecessary!";
        assertEquals(expected, e.getMessage());
    }

    private static class WrapperImpl implements Wrapper<Executor>, Executor {
        private final Executor instance;

        WrapperImpl(Executor instance) {
            this.instance = instance;
        }

        @Override
        public Executor unwrap() {
            return instance;
        }

        @Override
        public void execute(Runnable command) {
        }
    }

    private static class WrapperAdapterImpl implements WrapperAdapter<Executor>, Executor {
        private final Executor wrapper;
        private final Executor adaptee;

        WrapperAdapterImpl(Executor wrapper, Executor adaptee) {
            this.wrapper = wrapper;
            this.adaptee = adaptee;
        }

        @Override
        public Executor unwrap() {
            return wrapper;
        }

        @Override
        public Executor adaptee() {
            return adaptee;
        }

        @Override
        public void execute(Runnable command) {
        }
    }
}
