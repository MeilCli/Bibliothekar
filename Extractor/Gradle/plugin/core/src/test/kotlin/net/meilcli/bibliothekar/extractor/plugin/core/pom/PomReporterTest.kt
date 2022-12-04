package net.meilcli.bibliothekar.extractor.plugin.core.pom

import net.meilcli.bibliothekar.extractor.plugin.core.entities.Pom
import net.meilcli.bibliothekar.extractor.plugin.core.entities.json.PomJsonDeserializer
import net.meilcli.bibliothekar.extractor.plugin.core.entities.json.PomJsonSerializer
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.nio.file.Paths

class PomReporterTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Suppress("SameParameterValue")
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
    fun testReadCache() {
        val cacheDirectory = File(temporaryFolder.root, "cache").apply {
            mkdir()
        }
        val pomCache = FilePomCache(
            cacheDirectory,
            PomJsonSerializer,
            PomJsonDeserializer
        )
        val pom = createPom(
            group = "net.meilcli.bibliothekar",
            artifact = "bibliothekar",
            version = "1.0.0"
        )

        pomCache.write(pom)

        val pomReporter = PomReporter(PomJsonSerializer, PomJsonDeserializer)

        pomReporter.readCache(cacheDirectory)

        assertEquals(1, pomReporter.poms.size)
        assertEquals(pom, pomReporter.poms.first())
    }

    @Test
    fun testReport() {
        val cacheDirectory = File(temporaryFolder.root, "cache").apply {
            mkdir()
        }
        val pomCache = FilePomCache(
            cacheDirectory,
            PomJsonSerializer,
            PomJsonDeserializer
        )
        val pom = createPom(
            group = "net.meilcli.bibliothekar",
            artifact = "bibliothekar",
            version = "1.0.0"
        )

        pomCache.write(pom)

        val outputDirectory = File(temporaryFolder.root, "output").apply {
            mkdir()
        }
        val pomReporter = PomReporter(PomJsonSerializer, PomJsonDeserializer)

        pomReporter.readCache(cacheDirectory)
        pomReporter.report(outputDirectory)

        val writtenPath = outputDirectory.toPath().resolve(Paths.get("net.meilcli.bibliothekar/bibliothekar/1.0.0/pom.json")).toFile()
        val actual = PomJsonDeserializer.deserialize(writtenPath.readText())

        assertEquals(pom, actual)
    }
}
