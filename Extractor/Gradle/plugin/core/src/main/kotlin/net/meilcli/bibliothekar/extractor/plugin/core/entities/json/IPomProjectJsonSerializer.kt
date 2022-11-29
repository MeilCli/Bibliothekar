package net.meilcli.bibliothekar.extractor.plugin.core.entities.json

import net.meilcli.bibliothekar.extractor.plugin.core.entities.PomProject

interface IPomProjectJsonSerializer {

    fun serialize(pomProject: PomProject): String
}
