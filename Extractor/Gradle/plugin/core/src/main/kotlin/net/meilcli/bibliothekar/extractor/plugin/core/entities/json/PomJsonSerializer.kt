package net.meilcli.bibliothekar.extractor.plugin.core.entities.json

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.meilcli.bibliothekar.extractor.plugin.core.entities.Pom

object PomJsonSerializer : IJsonSerializer<Pom> {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    override fun serialize(value: Pom): String {
        return json.encodeToString(value)
    }
}
