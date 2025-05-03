package io.foldright.inspectablewrappers.study;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SuppressWarnings({"ConstantValue", "RedundantClassCall", "UnnecessaryBoxing", "DataFlowIssue", "SimplifiableAssertion"})
public class InstanceOfCheckDemoTest {
    @Test
    void isInstance() {
        // === trivial case: String is instance of Object ===

        assertTrue(Object.class.isInstance("foo"));

        assertTrue(Object.class.isAssignableFrom(String.class));

        assertTrue("foo" instanceof Object);

        // === is Integer instance of int? ===

        assertFalse(int.class.isInstance(1)); // autoboxing int -> Integer, same as below code
        assertFalse(int.class.isInstance(Integer.valueOf(1)));

        assertFalse(int.class.isAssignableFrom(Integer.class));

        // assertFalse(Integer.valueOf(1) instanceof int);
        // checking primitive int type by instanceof is syntax error: unexpected type, require class or array

        // === is int of Integer? ===

        // assertTrue(Integer.class.isInstance(primitive int)); // impossible pass primitive int to Object parameter

        assertFalse(Integer.class.isAssignableFrom(int.class));

        // assertFalse(1 instanceof Integer);
        // checking primitive int value by instanceof is syntax error: unexpected type, require reference
    }
}
