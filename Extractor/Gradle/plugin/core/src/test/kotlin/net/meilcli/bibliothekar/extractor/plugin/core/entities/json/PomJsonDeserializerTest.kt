package net.meilcli.bibliothekar.extractor.plugin.core.entities.json

import net.meilcli.bibliothekar.extractor.plugin.core.entities.*
import org.junit.Test
import kotlin.test.assertEquals

class PomJsonDeserializerTest {

    @Test
    fun testDeserialize_full() {
        val pom = Pom(
            group = "net.meilcli",
            artifact = "bibliothekar",
            version = "1.0.0",
            name = "Bibliothekar",
            url = "https://meilcli.net",
            description = "The Bibliothekar",
            parent = PomParent(
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
        val json = PomJsonSerializer.serialize(pom)

        val actual = PomJsonDeserializer.deserialize(json)

        assertEquals(pom, actual)
    }

    @Test
    fun testDeserialize_partial() {
        val pom = Pom(
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
        val json = PomJsonSerializer.serialize(pom)

        val actual = PomJsonDeserializer.deserialize(json)

        assertEquals(pom, actual)
    }
}
