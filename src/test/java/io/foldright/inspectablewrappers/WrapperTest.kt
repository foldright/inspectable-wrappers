package io.foldright.inspectablewrappers

import io.foldright.inspectablewrappers.Inspector.*
import io.foldright.inspectablewrappers.utils.AttachableDelegate
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class WrapperTest : FunSpec({
    // prepare executor instances/wrappers, build the executor/wrapper chain
    val baseExecutor = BaseExecutor()
    val lazyExecutorWrapper = LazyExecutorWrapper(baseExecutor)
        .apply { setAttachment("busy", "very, very busy!") }
    val executorChain: Executor = ChattyExecutorWrapper(lazyExecutorWrapper)

    test("wrapper chain") {
        containsInstanceTypeOnWrapperChain(executorChain, BaseExecutor::class.java).shouldBeTrue()
        containsInstanceTypeOnWrapperChain(executorChain, LazyExecutorWrapper::class.java).shouldBeTrue()
        containsInstanceTypeOnWrapperChain(executorChain, ChattyExecutorWrapper::class.java).shouldBeTrue()
        containsInstanceTypeOnWrapperChain(executorChain, ExecutorService::class.java).shouldBeFalse()

        val value: String? = getAttachmentFromWrapperChain(executorChain, "busy")
        value shouldBe "very, very busy!"

        getAttachmentFromWrapperChain<Executor, String, String?>(executorChain, "not existed").shouldBeNull()

        getInstancesOfWrapperChain(executorChain).shouldContainExactly(executorChain, lazyExecutorWrapper, baseExecutor)
        getInstancesOfWrapperChain(baseExecutor).shouldContainExactly(baseExecutor)

        getBaseOfWrapperChain(executorChain).shouldBeSameInstanceAs(baseExecutor)
        getBaseOfWrapperChain(baseExecutor).shouldBeSameInstanceAs(baseExecutor)

        unwrap(executorChain).shouldBeSameInstanceAs(lazyExecutorWrapper)
        unwrap(lazyExecutorWrapper).shouldBeSameInstanceAs(baseExecutor)
        unwrap(baseExecutor).shouldBeSameInstanceAs(baseExecutor)

        isWrapper(executorChain).shouldBeTrue()
        isWrapper(baseExecutor).shouldBeFalse()

        verifyWrapperChainContracts(lazyExecutorWrapper)
        verifyWrapperChainContracts(lazyExecutorWrapper, Executor::class.java)
        verifyWrapperChainContracts(baseExecutor)
        verifyWrapperChainContracts(baseExecutor, Executor::class.java)
    }

    test("ClassCastException") {
        shouldThrow<ClassCastException> {
            val value = getAttachmentFromWrapperChain<Executor, String, Int?>(executorChain, "busy")
            fail(value.toString())
        }
    }

    test("argument null") {
        shouldThrow<NullPointerException> {
            getAttachmentFromWrapperChain<Executor, String, String?>(null, "busy")
        }.message shouldBe "wrapper is null"

        shouldThrow<NullPointerException> {
            getAttachmentFromWrapperChain<Executor, String, String?>(executorChain, null)
        }.message shouldBe "key is null"
    }

    test("inspect last instance - containsInstanceTypeOnWrapperChain") {
        val pool = Executors.newCachedThreadPool()
        containsInstanceTypeOnWrapperChain(pool, ExecutorService::class.java).shouldBeTrue()

        val chatty = ChattyExecutorWrapper(pool)
        containsInstanceTypeOnWrapperChain(chatty, ExecutorService::class.java).shouldBeTrue()
    }

    test("inspect last instance - getAttachmentFromWrapperChain") {
        val attachable = AttachableDelegate<String, String>().apply { setAttachment("k1", "v1") }
        getAttachmentFromWrapperChain<Any, String, String?>(attachable, "k1") shouldBe "v1"

        val base = object : Executor, Attachable<String, String> by AttachableDelegate() {
            override fun execute(command: Runnable) {
                command.run()
            }
        }
        base.setAttachment("k1", "v1")
        getAttachmentFromWrapperChain<Any, String, String?>(base, "k1") shouldBe "v1"

        val c2 = ChattyExecutorWrapper(base)
        getAttachmentFromWrapperChain<Any, String, String?>(c2, "k1") shouldBe "v1"
    }
})

class BaseExecutor : Executor {
    override fun execute(command: Runnable) {
        command.run()
    }
}

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
