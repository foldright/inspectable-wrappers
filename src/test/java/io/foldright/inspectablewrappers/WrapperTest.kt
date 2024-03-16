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
    // prepare executor instance and wrappers
    val chatty: Executor = Executor { runnable -> runnable.run() }.let {
        LazyExecutorWrapper(it).apply { setAttachment("busy", "very, very busy!") }
    }.let(::ChattyExecutorWrapper)

    test("wrapper") {
        Wrapper.isInstanceOf(chatty, LazyExecutorWrapper::class.java).shouldBeTrue()
        Wrapper.isInstanceOf(chatty, ExecutorService::class.java).shouldBeFalse()

        val value: String = Wrapper.getAttachment(chatty, "busy")!!
        value shouldBe "very, very busy!"

        Wrapper.getAttachment<Executor, String, String>(chatty, "not existed").shouldBeNull()
    }

    test("ClassCastException") {
        shouldThrow<ClassCastException> {
            val value = Wrapper.getAttachment<Executor, String, Int?>(chatty, "busy")
            fail(value.toString())
        }
    }

    test("argument null") {
        shouldThrow<NullPointerException> {
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "CAST_NEVER_SUCCEEDS")
            Wrapper.getAttachment<Executor, String, String>(null as? Executor, "busy")
        }.message shouldBe "wrapper is null"

        shouldThrow<NullPointerException> {
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "CAST_NEVER_SUCCEEDS")
            Wrapper.getAttachment<Executor, String, String>(chatty, null as? String)
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
        println("I'm lazy, sleep before work")
        sleep()
        executor.execute(command)
    }

    override fun unwrap(): Executor = executor

    private fun sleep() {
        Thread.sleep(100)
    }
}
