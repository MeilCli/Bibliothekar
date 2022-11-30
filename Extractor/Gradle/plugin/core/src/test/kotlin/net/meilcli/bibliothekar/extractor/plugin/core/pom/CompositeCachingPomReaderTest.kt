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
        val filePomCache = InMemoryPomCache() // for testing
        val compositeCachingPomReader = CompositeCachingPomReader(
            gradlePomReader = gradlePomReader,
            inMemoryPomCache = inMemoryPomCache,
            filePomCache = filePomCache
        )
        val pom = mockk<Pom>(relaxed = true)
        val dependency = pom.toDependency()

        inMemoryPomCache.write(pom)

        val actual = compositeCachingPomReader.read(dependency)

        assertEquals(pom, actual)
    }

    @Test
    fun testRead_fromFile() {
        val gradlePomReader = mockk<IPomReader>(relaxed = true)
        val inMemoryPomCache = InMemoryPomCache()
        val filePomCache = InMemoryPomCache() // for testing
        val compositeCachingPomReader = CompositeCachingPomReader(
            gradlePomReader = gradlePomReader,
            inMemoryPomCache = inMemoryPomCache,
            filePomCache = filePomCache
        )
        val pom = mockk<Pom>(relaxed = true)
        val dependency = pom.toDependency()

        filePomCache.write(pom)

        val actual = compositeCachingPomReader.read(dependency)

        assertEquals(pom, actual)
        assertEquals(pom, inMemoryPomCache.read(dependency))
    }

    @Test
    fun testRead_fromGradle() {
        val pom = mockk<Pom>(relaxed = true)
        val dependency = pom.toDependency()
        val gradlePomReader = mockk<IPomReader> {
            every { read(dependency) } returns pom
        }
        val inMemoryPomCache = InMemoryPomCache()
        val filePomCache = InMemoryPomCache() // for testing
        val compositeCachingPomReader = CompositeCachingPomReader(
            gradlePomReader = gradlePomReader,
            inMemoryPomCache = inMemoryPomCache,
            filePomCache = filePomCache
        )

        val actual = compositeCachingPomReader.read(dependency)

        assertEquals(pom, actual)
        assertEquals(pom, inMemoryPomCache.read(dependency))
        assertEquals(pom, filePomCache.read(dependency))
    }
}
