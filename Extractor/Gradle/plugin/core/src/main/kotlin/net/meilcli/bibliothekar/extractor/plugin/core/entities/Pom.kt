package net.meilcli.bibliothekar.extractor.plugin.core.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pom(
    @SerialName("group")
    val group: String,

    @SerialName("artifact")
    val artifact: String,

    @SerialName("version")
    val version: String,

    @SerialName("name")
    val name: String?,

    @SerialName("description")
    val description: String?,

    @SerialName("url")
    val url: String?,

    @SerialName("parent")
    val parent: PomParent?,

    @SerialName("organization")
    val organization: PomOrganization?,

    @SerialName("licenses")
    val licenses: List<PomLicense>,

    @SerialName("developers")
    val developers: List<PomDeveloper>
) {

    fun toDependency(): Dependency {
        return Dependency(
            group = group,
            name = artifact,
            version = version
        )
    }
}
