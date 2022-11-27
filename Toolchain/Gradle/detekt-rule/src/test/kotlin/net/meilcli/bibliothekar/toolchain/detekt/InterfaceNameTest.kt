package net.meilcli.bibliothekar.toolchain.detekt

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.Assert.assertEquals
import org.junit.Test

class InterfaceNameTest {

    @Test
    fun testNoWarning() {
        val findings = InterfaceName(Config.empty).compileAndLint(
            """
                interface ITest {}
                interface IValue<T>
                interface IStringValue : IValue<String>
            """.trimIndent()
        )
        assertEquals(0, findings.size)
    }

    @Test
    fun testWarning() {
        val findings = InterfaceName(Config.empty).compileAndLint(
            """
                interface Test {}
                interface Value<T>
                interface StringValue : Value<String>
                interface VALUE
                interface VAL_LUE
            """.trimIndent()
        )
        assertEquals(5, findings.size)
    }
}
