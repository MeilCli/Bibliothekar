package net.meilcli.bibliothekar.extractor.plugin.core.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PomLicense(
    @SerialName("name")
    val name: String?,

    @SerialName("url")
    val url: String?
)
