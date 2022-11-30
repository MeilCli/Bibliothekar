package net.meilcli.bibliothekar.extractor.plugin.core.entities.xml

import net.meilcli.bibliothekar.extractor.plugin.core.entities.*
import org.w3c.dom.Element
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

object PomXmlParser : IPomXmlParser {

    private val factory = DocumentBuilderFactory.newInstance()

    override fun parse(inputStream: InputStream): Pom {
        val builder = factory.newDocumentBuilder()
        val root = builder.parse(inputStream)
        val project = root.documentElement

        val groupId = project.getText("groupId")
        val artifactId = project.getText("artifactId")
        val version = project.getText("version")
        val name = project.getTextOrNull("name")
        val description = project.getTextOrNull("description")
        val url = project.getTextOrNull("url")
        val parent = project.getParent()
        val organization = project.getOrganization()
        val licenses = project.getLicenses()
        val developers = project.getDevelopers()

        return Pom(
            group = groupId,
            artifact = artifactId,
            version = version,
            name = name,
            description = description,
            url = url,
            parent = parent,
            organization = organization,
            licenses = licenses,
            developers = developers
        )
    }

    private fun Element.getText(tag: String): String {
        val elementTag = getElementsByTagName(tag)
        check(1 <= elementTag.length) { "illegal pom file, $tag is not defined" }
        val result = elementTag.item(0).textContent
        check(result.isNotEmpty()) { "illegal pom file, $tag is empty" }
        return result
    }

    private fun Element.getTextOrNull(tag: String): String? {
        val elementTag = getElementsByTagName(tag)
        if (elementTag.length == 0) {
            return null
        }
        val result = elementTag.item(0).textContent
        if (result.isEmpty()) {
            return null
        }
        return result
    }

    private fun Element.getParent(): PomParent? {
        val parentTag = getElementsByTagName("parent")
        if (parentTag.length == 0) {
            return null
        }

        val element = parentTag.item(0) as? Element ?: return null
        val groupId = element.getText("groupId")
        val artifactId = element.getText("artifactId")
        val version = element.getText("version")

        return PomParent(
            group = groupId,
            artifact = artifactId,
            version = version
        )
    }

    private fun Element.getOrganization(): PomOrganization? {
        val organizationTag = getElementsByTagName("organization")
        if (organizationTag.length == 0) {
            return null
        }

        val element = organizationTag.item(0) as? Element ?: return null
        val name = element.getTextOrNull("name") ?: return null

        return PomOrganization(name)
    }

    private fun Element.getLicenses(): List<PomLicense> {
        val result = mutableListOf<PomLicense>()
        val licensesTag = getElementsByTagName("licenses")

        for (i in 0 until licensesTag.length) {
            val element = licensesTag.item(i) as? Element ?: continue
            val name = element.getTextOrNull("name")
            val url = element.getTextOrNull("url")
            if (name != null || url != null) {
                result += PomLicense(name = name, url = url)
            }
        }

        return result
    }

    private fun Element.getDevelopers(): List<PomDeveloper> {
        val result = mutableListOf<PomDeveloper>()
        val developersTag = getElementsByTagName("developers")

        for (i in 0 until developersTag.length) {
            val element = developersTag.item(i) as? Element ?: continue
            val name = element.getTextOrNull("name")
            val organization = element.getTextOrNull("organization")
            if (name != null || organization != null) {
                result += PomDeveloper(name = name, organization = organization)
            }
        }

        return result
    }
}
