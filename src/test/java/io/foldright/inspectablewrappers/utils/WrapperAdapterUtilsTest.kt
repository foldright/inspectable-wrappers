package io.foldright.inspectablewrappers.utils

import io.foldright.inspectablewrappers.*
import io.foldright.inspectablewrappers.Inspector.containsInstanceTypeOnWrapperChain
import io.foldright.inspectablewrappers.Inspector.getAttachmentFromWrapperChain
import io.foldright.inspectablewrappers.utils.WrapperAdapterUtils.createWrapperAdapter
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor

class WrapperAdapterUtilsTest : FunSpec({
    @Suppress("UNCHECKED_CAST")
    test("createWrapperAdapter with Attachable") {
        val chain: Executor = Executor { runnable -> runnable.run() }
            .let {
                val existed = ExistedExecutorWrapper(it)
                val adapter = createWrapperAdapter(
                    Executor::class.java,
                    it,
                    existed,
                    AttachableDelegate<String, String>(),
                )
                adapter.toString() shouldStartWith "[WrapperAdapter proxy created by WrapperAdapterUtils] "

                val attachable = adapter as Attachable<String, String>
                attachable.setAttachment(ADAPTED_MSG_KEY, ADAPTED_MSG_VALUE)
                attachable.getAttachment(ADAPTED_MSG_KEY) shouldBe ADAPTED_MSG_VALUE

                adapter
            }
            .let(::ChattyExecutorWrapper)

        containsInstanceTypeOnWrapperChain(chain, ExistedExecutorWrapper::class.java).shouldBeTrue()
        containsInstanceTypeOnWrapperChain(chain, ChattyExecutorWrapper::class.java).shouldBeTrue()
        containsInstanceTypeOnWrapperChain(chain, ExecutorService::class.java).shouldBeFalse()

        val value: String? = getAttachmentFromWrapperChain(chain, ADAPTED_MSG_KEY)
        value shouldBe ADAPTED_MSG_VALUE

        getAttachmentFromWrapperChain<Executor, String, String?>(chain, "not existed").shouldBeNull()

        // testing the proxy invocation
        chain.execute { println("I'm working.") }
    }

    test("createWrapperAdapter without Attachable") {
        val chain: Executor = Executor { runnable -> runnable.run() }
            .let {
                createWrapperAdapter(
                    Executor::class.java,
                    it,
                    ExistedExecutorWrapper(it),
                )
            }
            .let(::ChattyExecutorWrapper)

        containsInstanceTypeOnWrapperChain(chain, ExistedExecutorWrapper::class.java).shouldBeTrue()
        containsInstanceTypeOnWrapperChain(chain, ChattyExecutorWrapper::class.java).shouldBeTrue()
        containsInstanceTypeOnWrapperChain(chain, ExecutorService::class.java).shouldBeFalse()

        getAttachmentFromWrapperChain<Executor, String, String?>(chain, "not existed").shouldBeNull()

        // testing the proxy invocation
        chain.execute { println("I'm working.") }
    }

    test("adaptee contract") {
        val executor = Executor { it.run() }
        val wrongAdaptee = WrongWrapperAdapter(executor)
        shouldThrow<IllegalArgumentException> {
            createWrapperAdapter(Executor::class.java, executor, wrongAdaptee)
        }.message shouldBe "adaptee(io.foldright.inspectablewrappers.utils.WrongWrapperAdapter) is an instance of Wrapper," +
                " adapting a Wrapper to a Wrapper is UNNECESSARY"
    }

    @Suppress("UNCHECKED_CAST")
    test("checkTypeRequirements - bizInterface") {
        shouldThrow<IllegalArgumentException> {
            createWrapperAdapter(
                ThreadPoolExecutor::class.java,
                Executors.newCachedThreadPool() as ThreadPoolExecutor,
                Executors.newCachedThreadPool() as ThreadPoolExecutor
            )
        }.message shouldBe "bizInterface(java.util.concurrent.ThreadPoolExecutor) is not an interface"

        shouldThrow<IllegalArgumentException> {
            createWrapperAdapter(Wrapper::class.java as Class<Any>, "", "")
        }.message shouldBe "io.foldright.inspectablewrappers.Wrapper is auto implemented by proxy, not a valid biz interface"
        shouldThrow<IllegalArgumentException> {
            createWrapperAdapter(WrapperAdapter::class.java as Class<Any>, "", "")
        }.message shouldBe "io.foldright.inspectablewrappers.WrapperAdapter is auto implemented by proxy, not a valid biz interface"
        shouldThrow<IllegalArgumentException> {
            createWrapperAdapter(Attachable::class.java as Class<Any>, "", "")
        }.message shouldBe "io.foldright.inspectablewrappers.Attachable is auto implemented by proxy, not a valid biz interface"
    }

    @Suppress("UNCHECKED_CAST")
    test("checkTypeRequirements - underlying/adaptee") {
        shouldThrow<IllegalArgumentException> {
            createWrapperAdapter(
                Executor::class.java as Class<Any>,
                "", ""
            )
        }.message shouldBe "underlying(java.lang.String) is not an instance of java.util.concurrent.Executor"

        shouldThrow<IllegalArgumentException> {
            createWrapperAdapter(
                Executor::class.java as Class<Any>,
                Executor { it.run() }, ""
            )
        }.message shouldBe "adaptee(java.lang.String) is not an instance of java.util.concurrent.Executor"
    }
})

class WrongWrapperAdapter(private val executor: Executor) : WrapperAdapter<Executor>, Executor by executor {
    override fun unwrap(): Executor = executor
    override fun adaptee(): Executor = executor
}
