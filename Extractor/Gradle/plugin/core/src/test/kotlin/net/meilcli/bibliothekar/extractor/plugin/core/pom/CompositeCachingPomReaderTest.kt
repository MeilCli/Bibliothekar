package net.meilcli.bibliothekar.extractor.plugin.core.pom

import io.mockk.every
import io.mockk.mockk
import net.meilcli.bibliothekar.extractor.plugin.core.entities.Pom
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
        val pom = mockk<Pom>(relaxed = true)
        val dependency = pom.toDependency()

        inMemoryPomCache.write(pom)

        val actual = compositeCachingPomReader.read(dependency)

        assertEquals(pom, actual)
    }

    @Test
    fun testRead_fromGradle() {
        val pom = mockk<Pom>(relaxed = true)
        val dependency = pom.toDependency()
        val gradlePomReader = mockk<IPomReader> {
            every { read(dependency) } returns pom
        }
        val inMemoryPomCache = InMemoryPomCache()
        val compositeCachingPomReader = CompositeCachingPomReader(
            gradlePomReader = gradlePomReader,
            inMemoryPomCache = inMemoryPomCache
        )

        val actual = compositeCachingPomReader.read(dependency)

        assertEquals(pom, actual)
    }
}
