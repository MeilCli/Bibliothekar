package net.meilcli.bibliothekar.extractor.plugin.core.pom

import net.meilcli.bibliothekar.extractor.plugin.core.entities.Pom

interface IPomWriter {

    fun write(pom: Pom)
}
