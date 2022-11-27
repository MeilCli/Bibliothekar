package net.meilcli.bibliothekar.toolchain.detekt

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault

@ActiveByDefault(since = "1.20.0")
class RuleProvider : RuleSetProvider {

    override val ruleSetId = "bibliothekar"

    override fun instance(config: Config): RuleSet {
        return RuleSet(
            ruleSetId,
            listOf(
                InterfaceName(config),
                TypeParameterName(config)
            )
        )
    }
}
