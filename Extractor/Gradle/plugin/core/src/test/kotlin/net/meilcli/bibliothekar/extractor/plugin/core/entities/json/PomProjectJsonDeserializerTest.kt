package net.meilcli.bibliothekar.extractor.plugin.core.entities.json

import net.meilcli.bibliothekar.extractor.plugin.core.entities.*
import org.junit.Test
import kotlin.test.assertEquals

class PomProjectJsonDeserializerTest {

    @Test
    fun testDeserialize_full() {
        val pomProject = PomProject(
            group = "net.meilcli",
            artifact = "bibliothekar",
            version = "1.0.0",
            name = "Bibliothekar",
            url = "https://meilcli.net",
            description = "The Bibliothekar",
            parent = PomParentProject(
                group = "dev.meilcli",
                artifact = "bibliothekar_parent",
                version = "0.0.1"
            ),
            organization = PomOrganization(
                name = "OSS"
            ),
            licenses = listOf(
                PomLicense(
                    name = "MIT",
                    url = "https://meilcli.net"
                )
            ),
            developers = listOf(
                PomDeveloper(
                    name = "MeilCli",
                    organization = "OSS"
                )
            )
        )
        val json = PomProjectJsonSerializer.serialize(pomProject)

        val actual = PomProjectJsonDeserializer.deserialize(json)

        assertEquals(pomProject, actual)
    }

    @Test
    fun testDeserialize_partial() {
        val pomProject = PomProject(
            group = "net.meilcli",
            artifact = "bibliothekar",
            version = "1.0.0",
            name = null,
            url = null,
            description = null,
            parent = null,
            organization = null,
            licenses = emptyList(),
            developers = emptyList()
        )
        val json = PomProjectJsonSerializer.serialize(pomProject)

        val actual = PomProjectJsonDeserializer.deserialize(json)

        assertEquals(pomProject, actual)
    }
}
