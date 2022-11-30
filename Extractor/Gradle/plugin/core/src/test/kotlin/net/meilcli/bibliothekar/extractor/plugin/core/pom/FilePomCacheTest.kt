package net.meilcli.bibliothekar.extractor.plugin.core.pom

import net.meilcli.bibliothekar.extractor.plugin.core.entities.Pom
import net.meilcli.bibliothekar.extractor.plugin.core.entities.json.PomJsonDeserializer
import net.meilcli.bibliothekar.extractor.plugin.core.entities.json.PomJsonSerializer
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.nio.file.Paths
import kotlin.test.assertEquals

class FilePomCacheTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private fun createPom(
        group: String,
        artifact: String,
        version: String
    ): Pom {
        return Pom(
            group = group,
            artifact = artifact,
            version = version,
            name = null,
            url = null,
            description = null,
            organization = null,
            parent = null,
            developers = emptyList(),
            licenses = emptyList()
        )
    }

    @Test
    fun testWrite() {
        val directory = temporaryFolder.root
        val pomCache = FilePomCache(
            directory,
            PomJsonSerializer,
            PomJsonDeserializer
        )
        val pom = createPom(
            group = "net.meilcli.bibliothekar",
            artifact = "bibliothekar",
            version = "1.0.0"
        )

        pomCache.write(pom)

        val writtenPath = directory.toPath().resolve(Paths.get("net.meilcli.bibliothekar/bibliothekar/1.0.0/pom.json")).toFile()
        val actual = PomJsonDeserializer.deserialize(writtenPath.readText())

        assertEquals(pom, actual)
    }

    @Test
    fun testWrite_undefined() {
        val directory = temporaryFolder.root
        val pomCache = FilePomCache(
            directory,
            PomJsonSerializer,
            PomJsonDeserializer
        )
        val pom = createPom(
            group = "",
            artifact = "",
            version = ""
        )

        pomCache.write(pom)

        val writtenPath = directory.toPath().resolve(Paths.get("undefined/undefined/undefined/pom.json")).toFile()
        val actual = PomJsonDeserializer.deserialize(writtenPath.readText())

        assertEquals(pom, actual)
    }

    @Test
    fun testRead() {
        val directory = temporaryFolder.root
        val pomCache = FilePomCache(
            directory,
            PomJsonSerializer,
            PomJsonDeserializer
        )
        val pom = createPom(
            group = "net.meilcli.bibliothekar",
            artifact = "bibliothekar",
            version = "1.0.0"
        )

        directory.toPath().resolve(Paths.get("net.meilcli.bibliothekar/bibliothekar/1.0.0")).toFile().mkdirs()
        val readPath = directory.toPath().resolve(Paths.get("net.meilcli.bibliothekar/bibliothekar/1.0.0/pom.json")).toFile()
        readPath.writeText(PomJsonSerializer.serialize(pom))

        val actual = pomCache.read(pom.toDependency())

        assertEquals(pom, actual)
    }
}
