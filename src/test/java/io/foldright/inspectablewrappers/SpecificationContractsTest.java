package io.foldright.inspectablewrappers;

import edu.umd.cs.findbugs.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import static io.foldright.inspectablewrappers.Inspector.verifyWrapperChainContracts;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;


class SpecificationContractsTest {

    private static final Executor DUMMY = command -> {
    };

    @Test
    void test_null_unwrap() {
        Executor w = new WrapperImpl(null);

        NullPointerException e = assertThrowsExactly(NullPointerException.class,
                () -> verifyWrapperChainContracts(w));
        String expected = "unwrap of Wrapper(io.foldright.inspectablewrappers.SpecificationContractsTest$WrapperImpl) is null";
        assertEquals(expected, e.getMessage());
    }

    @Test
    void test_not_biz_interface() {
        Executor w = new WrapperImpl(DUMMY);
        @SuppressWarnings({"unchecked", "rawtypes"})
        IllegalStateException e = assertThrowsExactly(IllegalStateException.class,
                () -> verifyWrapperChainContracts(w, (Class) ExecutorService.class));
        String expected = "the instance(io.foldright.inspectablewrappers.SpecificationContractsTest$WrapperImpl" +
                ") on wrapper chain is not an instance of java.util.concurrent.ExecutorService";
        assertEquals(expected, e.getMessage());
    }

    @Test
    void test_null_adaptee() {
        Executor w = new WrapperAdapterImpl(DUMMY, null);

        NullPointerException e = assertThrowsExactly(NullPointerException.class,
                () -> verifyWrapperChainContracts(w));

        String expected = "adaptee of WrapperAdapter(io.foldright.inspectablewrappers.SpecificationContractsTest$WrapperAdapterImpl) is null";
        assertEquals(expected, e.getMessage());
    }

    @Test
    void test_wrap_type_adaptee() {
        Executor w = new WrapperAdapterImpl(DUMMY, new WrapperImpl(null));

        IllegalStateException e = assertThrowsExactly(IllegalStateException.class,
                () -> verifyWrapperChainContracts(w));

        String expected = "adaptee(io.foldright.inspectablewrappers.SpecificationContractsTest$WrapperImpl)" +
                " of WrapperAdapter(io.foldright.inspectablewrappers.SpecificationContractsTest$WrapperAdapterImpl)" +
                " is an instance of Wrapper, adapting a Wrapper to a Wrapper is UNNECESSARY";
        assertEquals(expected, e.getMessage());
    }

    @Test
    void testCyclicWrapperChain() {
        MutableWrapperImpl w1 = new MutableWrapperImpl();
        MutableWrapperImpl w2 = new MutableWrapperImpl();
        w1.instance = w2;
        w2.instance = w1;

        IllegalStateException e = assertThrowsExactly(IllegalStateException.class,
                () -> verifyWrapperChainContracts(w2));

        String expected = "CYCLIC wrapper chain, duplicate instance of" +
                " io.foldright.inspectablewrappers.SpecificationContractsTest$MutableWrapperImpl";
        assertEquals(expected, e.getMessage());
    }

    private static class WrapperImpl implements Wrapper<Executor>, Executor {
        private final Executor instance;

        WrapperImpl(Executor instance) {
            this.instance = instance;
        }

        @Override
        @NonNull
        public Executor unwrap_() {
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
        @NonNull
        public Executor unwrap_() {
            return wrapper;
        }

        @Override
        @NonNull
        public Executor adaptee_() {
            return adaptee;
        }

        @Override
        public void execute(Runnable command) {
        }
    }

    private static class MutableWrapperImpl implements Wrapper<Executor>, Executor {
        Executor instance;

        @Override
        @NonNull
        public Executor unwrap_() {
            return instance;
        }

        @Override
        public void execute(Runnable command) {
        }
    }
}
