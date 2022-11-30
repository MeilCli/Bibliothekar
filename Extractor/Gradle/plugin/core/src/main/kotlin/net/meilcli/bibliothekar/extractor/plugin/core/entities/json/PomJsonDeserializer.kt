package net.meilcli.bibliothekar.extractor.plugin.core.entities.json

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.meilcli.bibliothekar.extractor.plugin.core.entities.Pom

object PomJsonDeserializer : IJsonDeserializer<Pom> {

    private val json = Json { ignoreUnknownKeys = true }

    override fun deserialize(jsonString: String): Pom {
        return json.decodeFromString(jsonString)
    }
}
