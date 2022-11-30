package net.meilcli.bibliothekar.extractor.plugin.core.entities.json

import net.meilcli.bibliothekar.extractor.plugin.core.entities.Pom

interface IPomJsonSerializer {

    fun serialize(pom: Pom): String
}
