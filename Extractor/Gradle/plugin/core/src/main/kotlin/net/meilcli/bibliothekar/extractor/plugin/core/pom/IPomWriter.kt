package net.meilcli.bibliothekar.extractor.plugin.core.pom

import net.meilcli.bibliothekar.extractor.plugin.core.entities.PomProject

interface IPomWriter {

    fun write(pomProject: PomProject)
}
