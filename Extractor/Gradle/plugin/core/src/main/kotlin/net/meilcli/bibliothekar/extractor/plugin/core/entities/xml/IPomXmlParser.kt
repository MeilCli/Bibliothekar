package net.meilcli.bibliothekar.extractor.plugin.core.entities.xml

import net.meilcli.bibliothekar.extractor.plugin.core.entities.Pom
import java.io.InputStream

interface IPomXmlParser {

    fun parse(inputStream: InputStream): Pom
}
