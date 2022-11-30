package net.meilcli.bibliothekar.extractor.plugin.core.entities.json

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.meilcli.bibliothekar.extractor.plugin.core.entities.Pom

object PomJsonSerializer : IPomJsonSerializer {

    private val json = Json { ignoreUnknownKeys = true }

    override fun serialize(pom: Pom): String {
        return json.encodeToString(pom)
    }
}
