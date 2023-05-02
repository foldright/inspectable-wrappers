package io.foldright.inspectablewrappers

import io.foldright.inspectablewrappers.utils.AttachableDelegate
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
        LazyExecutorWrapper(it).apply { setAttachment("busy", "very very busy!") }
    }.let(::ChattyExecutorWrapper)

    test("wrapper") {
        Wrapper.isInstanceOf(chatty, LazyExecutorWrapper::class.java).shouldBeTrue()
        Wrapper.isInstanceOf(chatty, ExecutorService::class.java).shouldBeFalse()

        val value: String = Wrapper.getAttachment(chatty, "busy")!!
        value shouldBe "very very busy!"

        Wrapper.getAttachment<Executor, String>(chatty, "not existed").shouldBeNull()
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
        Executor, Wrapper<Executor>, Attachable by AttachableDelegate() {
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
