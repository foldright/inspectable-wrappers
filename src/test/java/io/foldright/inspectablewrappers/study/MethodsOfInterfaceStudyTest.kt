package io.foldright.inspectablewrappers.study

import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import java.lang.reflect.Method
import java.lang.reflect.Modifier

/**
 * a test case for studying the reflection methods: [Class.getMethods] and [Class.getDeclaredMethods]
 */
class MethodsOfInterfaceStudyTest : FunSpec({
    test("Methods of Interface") {
        // methods of interface do not contain the methods of Object
        I0::class.java.methods.shouldBeEmpty()
        I0::class.java.declaredMethods.shouldBeEmpty()

        listOf(I1::class.java.methods, I1::class.java.declaredMethods).shouldForAll { methods ->
            methods.toNames().shouldContainExactlyInAnyOrder("foo")
            methods.filter { it.isAbstract }.toNames().shouldContainExactlyInAnyOrder("foo")
        }

        listOf(I2::class.java.methods, I2::class.java.declaredMethods).shouldForAll { methods ->
            methods.toNames().shouldContainExactlyInAnyOrder("foo", "bar")

            // default methods is NOT abstract, and vice versa.
            methods.filter { it.isAbstract }.toNames().shouldContainExactlyInAnyOrder("foo")
            methods.filter { it.isDefault }.toNames().shouldContainExactlyInAnyOrder("bar")
        }

        // `getMethods()` CONTAIN the method of base interface
        I12::class.java.methods.apply {
            toNames().shouldContainExactlyInAnyOrder("foo", "bar", "bar2")
            filter { it.isAbstract }.toNames().shouldContainExactlyInAnyOrder("foo", "bar2")
            filter { it.isDefault }.toNames().shouldContainExactlyInAnyOrder("bar")
        }
        // `getDeclaredMethods()` do NOT CONTAIN the method of base interface
        I12::class.java.declaredMethods.apply {
            toNames().shouldContainExactlyInAnyOrder("bar", "bar2")
            filter { it.isAbstract }.toNames().shouldContainExactlyInAnyOrder("bar2")
            filter { it.isDefault }.toNames().shouldContainExactlyInAnyOrder("bar")
        }
    }
})


interface I0

@Suppress("unused")
interface I1 {
    fun foo()
}

@Suppress("unused")
interface I2 {
    fun foo()
    fun bar() = 42
}

@Suppress("unused")
interface I12 : I1 {
    fun bar() = 42
    fun bar2()
}

fun Array<Method>.toNames() = map { it.name }
fun Iterable<Method>.toNames() = map { it.name }
val Method.isAbstract get() = Modifier.isAbstract(modifiers)
