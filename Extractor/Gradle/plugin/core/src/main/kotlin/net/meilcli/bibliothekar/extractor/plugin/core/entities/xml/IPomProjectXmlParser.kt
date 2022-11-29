package net.meilcli.bibliothekar.extractor.plugin.core.entities.xml

import net.meilcli.bibliothekar.extractor.plugin.core.entities.PomProject
import java.io.InputStream

interface IPomProjectXmlParser {

    fun parse(inputStream: InputStream): PomProject
}
