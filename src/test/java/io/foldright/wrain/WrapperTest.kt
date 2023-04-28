package io.foldright.wrain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService

class WrapperTest : FunSpec({

    // prepare executor instance and wrappers

    val executor = Executor { it.run() }
    val lazy: Executor = LazyExecutorWrapper(executor).apply { wrainSet("busy", "very very busy!") }
    val chatty: Executor = ChattyExecutorWrapper(lazy)

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

    override fun wrainUnwrap(): Executor {
        return executor
    }
}

class LazyExecutorWrapper(private val executor: Executor) : Executor, Wrapper<Executor>, Attachable {
    override fun execute(command: Runnable) {
        println("I'm lazy, sleep before work")
        sleep()
        executor.execute(command)
    }

    override fun wrainUnwrap(): Executor {
        return executor
    }

    private val attachments: ConcurrentMap<String, Any> = ConcurrentHashMap()

    override fun wrainSet(key: String, value: Any) {
        attachments[key] = value
    }

    @Suppress("UNCHECKED_CAST")
    override fun <V> wrainGet(key: String): V {
        return attachments[key] as V
    }

    private fun sleep() {
        Thread.sleep(100)
    }
}
