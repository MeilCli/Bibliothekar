package net.meilcli.bibliothekar.extractor.plugin.core.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PomParentProject(
    @SerialName("group")
    val group: String,

    @SerialName("artifact")
    val artifact: String,

    @SerialName("version")
    val version: String
)
