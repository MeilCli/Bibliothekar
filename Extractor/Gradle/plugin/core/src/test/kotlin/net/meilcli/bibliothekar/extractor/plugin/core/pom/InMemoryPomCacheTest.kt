package net.meilcli.bibliothekar.extractor.plugin.core.pom

import io.mockk.mockk
import net.meilcli.bibliothekar.extractor.plugin.core.entities.Pom
import org.junit.Test
import kotlin.test.assertEquals

class InMemoryPomCacheTest {

    @Test
    fun testRead_cacheHit() {
        val pomCache = InMemoryPomCache()
        val pom = mockk<Pom>(relaxed = true)
        val dependency = pom.toDependency()

        pomCache.write(pom)

        val actual = pomCache.read(dependency)

        assertEquals(pom, actual)
    }

    @Test
    fun testRead_noCache() {
        val pomCache = InMemoryPomCache()
        val pom = mockk<Pom>(relaxed = true)
        val dependency = pom.toDependency()

        val actual = pomCache.read(dependency)

        assertEquals(null, actual)
    }
}
