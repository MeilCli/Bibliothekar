package net.meilcli.bibliothekar.extractor.plugin.core.entities.json

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.meilcli.bibliothekar.extractor.plugin.core.entities.PomProject

object PomProjectJsonSerializer : IPomProjectJsonSerializer {

    private val json = Json { ignoreUnknownKeys = true }

    override fun serialize(pomProject: PomProject): String {
        return json.encodeToString(pomProject)
    }
}
