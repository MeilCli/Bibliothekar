package net.meilcli.bibliothekar.extractor.plugin.core.entities.json

import net.meilcli.bibliothekar.extractor.plugin.core.entities.PomProject

interface IPomProjectJsonDeserializer {

    fun deserialize(jsonString: String): PomProject
}
