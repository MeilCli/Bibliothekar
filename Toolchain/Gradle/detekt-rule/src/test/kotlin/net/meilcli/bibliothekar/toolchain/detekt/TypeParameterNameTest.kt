package net.meilcli.bibliothekar.toolchain.detekt

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.Assert.assertEquals
import org.junit.Test

class TypeParameterNameTest {

    @Test
    fun testNoWarning() {
        val findings = TypeParameterName(Config.empty).compileAndLint(
            """
                class Value<T, R> : IValue<T, R>
                interface IValue<T, R>
                class Action<TSource, TResult> : IAction<TSource, TResult>
                interface IAction<TSource, TResult>
            """.trimIndent()
        )
        assertEquals(0, findings.size)
    }

    @Test
    fun testWarning() {
        val findings = TypeParameterName(Config.empty).compileAndLint(
            """
                class Action<Result> : IAction<Result>
                interface IAction<Result>
            """.trimIndent()
        )
        assertEquals(2, findings.size)
    }
}
