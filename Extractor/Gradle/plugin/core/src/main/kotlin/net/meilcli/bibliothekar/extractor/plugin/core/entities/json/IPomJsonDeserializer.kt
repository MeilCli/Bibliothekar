package net.meilcli.bibliothekar.extractor.plugin.core.entities.json

import net.meilcli.bibliothekar.extractor.plugin.core.entities.Pom

interface IPomJsonDeserializer {

    fun deserialize(jsonString: String): Pom
}
