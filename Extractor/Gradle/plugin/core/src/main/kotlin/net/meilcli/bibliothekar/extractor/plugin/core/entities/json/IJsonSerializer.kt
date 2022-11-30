package net.meilcli.bibliothekar.extractor.plugin.core.entities.json

interface IJsonSerializer<in T> {

    fun serialize(value: T): String
}
