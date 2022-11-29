package net.meilcli.bibliothekar.extractor.plugin.core.pom

import io.mockk.every
import io.mockk.mockk
import net.meilcli.bibliothekar.extractor.plugin.core.entities.PomProject
import org.junit.Test
import kotlin.test.assertEquals

class CompositeCachingPomReaderTest {

    @Test
    fun testRead_fromCache() {
        val gradlePomReader = mockk<IPomReader>(relaxed = true)
        val inMemoryPomCache = InMemoryPomCache()
        val compositeCachingPomReader = CompositeCachingPomReader(
            gradlePomReader = gradlePomReader,
            inMemoryPomCache = inMemoryPomCache
        )
        val pomProject = mockk<PomProject>(relaxed = true)
        val dependency = pomProject.toDependency()

        inMemoryPomCache.write(pomProject)

        val actual = compositeCachingPomReader.read(dependency)

        assertEquals(pomProject, actual)
    }

    @Test
    fun testRead_fromGradle() {
        val pomProject = mockk<PomProject>(relaxed = true)
        val dependency = pomProject.toDependency()
        val gradlePomReader = mockk<IPomReader> {
            every { read(dependency) } returns pomProject
        }
        val inMemoryPomCache = InMemoryPomCache()
        val compositeCachingPomReader = CompositeCachingPomReader(
            gradlePomReader = gradlePomReader,
            inMemoryPomCache = inMemoryPomCache
        )

        val actual = compositeCachingPomReader.read(dependency)

        assertEquals(pomProject, actual)
    }
}
