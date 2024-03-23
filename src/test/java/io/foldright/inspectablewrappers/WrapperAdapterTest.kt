package io.foldright.inspectablewrappers

import io.foldright.inspectablewrappers.utils.AttachableDelegate
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService


private const val ADAPTED_MSG_KEY = "adapted-existed-executor-wrapper-msg"
private const val ADAPTED_MSG_VALUE =
    "I'm a adapter of an existed executor which have nothing to do with ~inspectable~wrappers~."

class WrapperAdapterTest : FunSpec({
    // prepare executor instances/wrappers, build the executor/wrapper chain
    val executorChain: Executor = ExistedExecutorWrapper { runnable -> runnable.run() }.let {
        ExistedExecutorWrapperAdapter(it).apply {
            setAttachment(ADAPTED_MSG_KEY, ADAPTED_MSG_VALUE)
        }
    }.let(::ChattyExecutorWrapper)

    test("WrapperAdapter") {
        Wrapper.isInstanceOf(executorChain, ExistedExecutorWrapper::class.java).shouldBeTrue()
        Wrapper.isInstanceOf(executorChain, ExistedExecutorWrapperAdapter::class.java).shouldBeTrue()
        Wrapper.isInstanceOf(executorChain, ChattyExecutorWrapper::class.java).shouldBeTrue()
        Wrapper.isInstanceOf(executorChain, ExecutorService::class.java).shouldBeFalse()

        val value: String? = Wrapper.getAttachment(executorChain, ADAPTED_MSG_KEY)
        value shouldBe ADAPTED_MSG_VALUE

        Wrapper.getAttachment<Executor, String, String?>(executorChain, "not existed").shouldBeNull()
    }

    test("ClassCastException") {
        shouldThrow<ClassCastException> {
            val value = Wrapper.getAttachment<Executor, String, Int?>(executorChain, ADAPTED_MSG_KEY)
            fail(value.toString())
        }
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    test("argument null") {
        shouldThrow<NullPointerException> {
            Wrapper.getAttachment<Executor, String, String?>(null , ADAPTED_MSG_KEY)
        }.message shouldBe "wrapper is null"

        shouldThrow<NullPointerException> {
            Wrapper.getAttachment<Executor, String, String?>(executorChain, null)
        }.message shouldBe "key is null"
    }

})

/**
 * Adaption an existed wrapper(`ExistedExecutorWrapper`) without modifying it.
 */
private class ExistedExecutorWrapperAdapter(private val adaptee: ExistedExecutorWrapper) :
        Executor by adaptee, WrapperAdapter<Executor>, Attachable<String, String> by AttachableDelegate() {
    override fun unwrap(): Executor = adaptee.executor

    override fun adaptee(): Executor = adaptee
}

class ExistedExecutorWrapper(val executor: Executor) : Executor {
    override fun execute(command: Runnable) {
        println(ADAPTED_MSG_VALUE)
        executor.execute(command)
    }
}
