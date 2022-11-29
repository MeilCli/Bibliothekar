package net.meilcli.bibliothekar.extractor.plugin.core.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PomDeveloper(
    @SerialName("name")
    val name: String?,

    @SerialName("organization")
    val organization: String?
)
