package io.foldright.wrain

import io.foldright.demo.ChattyExecutorWrapper
import io.foldright.demo.LazyExecutorWrapper
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService

class WrapperTest : FunSpec({

    ////////////////////////////////////////
    // prepare executor instance and wrappers
    ////////////////////////////////////////

    val executor = Runnable::run

    val lazy = LazyExecutorWrapper(executor)
    lazy.wrainSetAttachment("busy", "very very busy!")

    val chatty: Executor = ChattyExecutorWrapper(lazy)

    test("wrapper") {
        Wrapper.isInstanceOf(chatty, LazyExecutorWrapper::class.java).shouldBeTrue()
        Wrapper.isInstanceOf(chatty, ExecutorService::class.java).shouldBeFalse()

        val value: String = Wrapper.getAttachment(chatty, "busy")!!
        value shouldBe "very very busy!"

        Wrapper.getAttachment<Executor, String>(chatty, "not existed").shouldBeNull()
    }
})
