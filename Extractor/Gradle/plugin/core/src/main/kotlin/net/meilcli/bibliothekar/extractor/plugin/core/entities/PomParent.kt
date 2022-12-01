package net.meilcli.bibliothekar.extractor.plugin.core.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PomParent(
    @SerialName("group")
    val group: String,

    @SerialName("artifact")
    val artifact: String,

    @SerialName("version")
    val version: String
) {

    fun toDependency(): Dependency {
        return Dependency(
            group = group,
            name = artifact,
            version = version
        )
    }
}
