package net.meilcli.bibliothekar.toolchain.detekt

import io.gitlab.arturbosch.detekt.api.*
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import org.jetbrains.kotlin.psi.KtTypeParameter

@ActiveByDefault(since = "1.20.0")
class TypeParameterName(config: Config) : Rule(config) {

    @Configuration("type parameter regex pattern")
    private val typeParameterPattern by config("^[A-Z]|(T([A-Z][a-z0-9]+)+)\$") { Regex(it) }

    override val issue = Issue(
        "TypeParameterNaming",
        Severity.Style,
        "A type parameter's name must fit the naming pattern defined in the projects configurations",
        debt = Debt.FIVE_MINS
    )

    override fun visitTypeParameter(parameter: KtTypeParameter) {
        super.visitTypeParameter(parameter)

        if (parameter.name?.matches(typeParameterPattern) != true) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(parameter),
                    message = "type parameter ${parameter.name} must match the pattern: $typeParameterPattern"
                )
            )
        }
    }
}
