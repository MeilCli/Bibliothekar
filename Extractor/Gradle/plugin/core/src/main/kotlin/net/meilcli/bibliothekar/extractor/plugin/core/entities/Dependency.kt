package net.meilcli.bibliothekar.extractor.plugin.core.entities

data class Dependency(
    val group: String,
    val name: String,
    val version: String
) {

    override fun toString(): String {
        return "$group:$name:$version"
    }
}
