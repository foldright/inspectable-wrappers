package io.foldright.inspectablewrappers

import io.foldright.inspectablewrappers.Inspector.containsInstanceOnWrapperChain
import io.foldright.inspectablewrappers.Inspector.getAttachmentFromWrapperChain
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


const val ADAPTED_MSG_KEY = "adapted-existed-executor-wrapper-msg"
const val ADAPTED_MSG_VALUE =
    "I'm a adapter of an existed executor which have nothing to do with ~inspectable~wrappers~."

class WrapperAdapterTest : FunSpec({
    // prepare executor instances/wrappers, build the executor/wrapper chain
    val executorChain: Executor = Executor { runnable -> runnable.run() }
        .let(ExistedExecutorWrapperAdapter::createExistedExecutorWrapperAdapter)
        .let(::ChattyExecutorWrapper)

    test("WrapperAdapter") {
        containsInstanceOnWrapperChain(executorChain, ExistedExecutorWrapper::class.java).shouldBeTrue()
        containsInstanceOnWrapperChain(executorChain, ExistedExecutorWrapperAdapter::class.java).shouldBeTrue()
        containsInstanceOnWrapperChain(executorChain, ChattyExecutorWrapper::class.java).shouldBeTrue()
        containsInstanceOnWrapperChain(executorChain, ExecutorService::class.java).shouldBeFalse()

        val value: String? = getAttachmentFromWrapperChain(executorChain, ADAPTED_MSG_KEY)
        value shouldBe ADAPTED_MSG_VALUE

        getAttachmentFromWrapperChain<Executor, String, String?>(executorChain, "not existed").shouldBeNull()
    }
    test("ClassCastException") {
        shouldThrow<ClassCastException> {
            val value = getAttachmentFromWrapperChain<Executor, String, Int?>(executorChain, ADAPTED_MSG_KEY)
            fail(value.toString())
        }
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    test("argument null") {
        shouldThrow<NullPointerException> {
            getAttachmentFromWrapperChain<Executor, String, String?>(null, ADAPTED_MSG_KEY)
        }.message shouldBe "wrapper is null"

        shouldThrow<NullPointerException> {
            getAttachmentFromWrapperChain<Executor, String, String?>(executorChain, null)
        }.message shouldBe "key is null"
    }

    test("travelWrapperChain IllegalStateException - the adaptee of WrapperAdapter is an instance of Wrapper") {
        val chain: Executor = ChattyExecutorWrapper { runnable -> runnable.run() }
            .let(::ChattyExecutorWrapperAdapter)

        val errMsg = "adaptee(io.foldright.inspectablewrappers.ChattyExecutorWrapper)" +
                " of WrapperAdapter(io.foldright.inspectablewrappers.ChattyExecutorWrapperAdapter)" +
                " is an instance of Wrapper, adapting a Wrapper to a Wrapper is unnecessary!"

        shouldThrow<IllegalStateException> {
            containsInstanceOnWrapperChain(chain, ExecutorService::class.java)
        }.message shouldBe errMsg
        // first instance is ok, not trigger the check logic yet...
        containsInstanceOnWrapperChain(chain, Executor::class.java).shouldBeTrue()
        containsInstanceOnWrapperChain(chain, ChattyExecutorWrapperAdapter::class.java).shouldBeTrue()

        shouldThrow<IllegalStateException> {
            getAttachmentFromWrapperChain(chain, "k1")
        }.message shouldBe errMsg
    }
})

/**
 * Adaption an existed wrapper([ExistedExecutorWrapper]) without modifying it.
 */
private class ExistedExecutorWrapperAdapter(private val unwrap: Executor, private val adaptee: Executor) :
        Executor by adaptee, WrapperAdapter<Executor>, Attachable<String, String> by AttachableDelegate() {
    override fun unwrap(): Executor = unwrap
    override fun adaptee(): Executor = adaptee

    companion object {
        fun createExistedExecutorWrapperAdapter(base: Executor): ExistedExecutorWrapperAdapter {
            val existed = ExistedExecutorWrapper(base)
            return ExistedExecutorWrapperAdapter(base, existed).apply {
                setAttachment(ADAPTED_MSG_KEY, ADAPTED_MSG_VALUE)
            }
        }
    }
}

/**
 * An existed executor which have nothing to do with `Inspectable Wrappers`.
 */
class ExistedExecutorWrapper(private val executor: Executor) : Executor {
    override fun execute(command: Runnable) {
        println(ADAPTED_MSG_VALUE)
        executor.execute(command)
    }
}

/**
 * WRONG use the [WrapperAdapter], the adaptee is already [Wrapper]!
 */
private class ChattyExecutorWrapperAdapter(private val adaptee: ChattyExecutorWrapper) :
        Executor by adaptee, WrapperAdapter<Executor>, Attachable<String, String> by AttachableDelegate() {
    override fun unwrap(): Executor = adaptee.unwrap()
    override fun adaptee(): Executor = adaptee
}

