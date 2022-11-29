package net.meilcli.bibliothekar.extractor.plugin.core.entities.json

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.meilcli.bibliothekar.extractor.plugin.core.entities.PomProject

object PomProjectJsonDeserializer : IPomProjectJsonDeserializer {

    private val json = Json { ignoreUnknownKeys = true }

    override fun deserialize(jsonString: String): PomProject {
        return json.decodeFromString(jsonString)
    }
}
