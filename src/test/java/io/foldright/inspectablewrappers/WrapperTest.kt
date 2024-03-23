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

class WrapperTest : FunSpec({
    // prepare executor instances/wrappers, build the executor/wrapper chain
    val executorChain: Executor = LazyExecutorWrapper { runnable -> runnable.run() }
        .apply { setAttachment("busy", "very, very busy!") }
        .let(::ChattyExecutorWrapper)

    test("wrapper") {
        Wrapper.isInstanceOf(executorChain, LazyExecutorWrapper::class.java).shouldBeTrue()
        Wrapper.isInstanceOf(executorChain, ChattyExecutorWrapper::class.java).shouldBeTrue()
        Wrapper.isInstanceOf(executorChain, ExecutorService::class.java).shouldBeFalse()

        val value: String? = Wrapper.getAttachment(executorChain, "busy")
        value shouldBe "very, very busy!"

        Wrapper.getAttachment<Executor, String, String?>(executorChain, "not existed").shouldBeNull()
    }

    test("ClassCastException") {
        shouldThrow<ClassCastException> {
            val value = Wrapper.getAttachment<Executor, String, Int?>(executorChain, "busy")
            fail(value.toString())
        }
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    test("argument null") {
        shouldThrow<NullPointerException> {
            Wrapper.getAttachment<Executor, String, String?>(null, "busy")
        }.message shouldBe "wrapper is null"

        shouldThrow<NullPointerException> {
            Wrapper.getAttachment<Executor, String, String?>(executorChain, null)
        }.message shouldBe "key is null"
    }
})

class ChattyExecutorWrapper(private val executor: Executor) : Executor, Wrapper<Executor> {
    override fun execute(command: Runnable) {
        println("BlaBlaBla...")
        executor.execute(command)
    }

    override fun unwrap(): Executor = executor
}

class LazyExecutorWrapper(private val executor: Executor) :
        Executor, Wrapper<Executor>, Attachable<String, String> by AttachableDelegate() {
    override fun execute(command: Runnable) {
        println("I'm lazy, sleep before work.")
        sleep()
        executor.execute(command)
    }

    override fun unwrap(): Executor = executor

    private fun sleep() {
        Thread.sleep(100)
    }
}
