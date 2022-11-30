package net.meilcli.bibliothekar.extractor.plugin.core.entities.json

interface IJsonDeserializer<out T> {

    fun deserialize(jsonString: String): T
}
