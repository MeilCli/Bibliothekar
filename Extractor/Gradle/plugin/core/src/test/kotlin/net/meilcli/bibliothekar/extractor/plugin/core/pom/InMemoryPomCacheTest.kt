package net.meilcli.bibliothekar.extractor.plugin.core.pom

import io.mockk.mockk
import net.meilcli.bibliothekar.extractor.plugin.core.entities.PomProject
import org.junit.Test
import kotlin.test.assertEquals

class InMemoryPomCacheTest {

    @Test
    fun testRead_cacheHit() {
        val pomCache = InMemoryPomCache()
        val pomProject = mockk<PomProject>(relaxed = true)
        val dependency = pomProject.toDependency()

        pomCache.write(pomProject)

        val actual = pomCache.read(dependency)

        assertEquals(pomProject, actual)
    }

    @Test
    fun testRead_noCache() {
        val pomCache = InMemoryPomCache()
        val pomProject = mockk<PomProject>(relaxed = true)
        val dependency = pomProject.toDependency()

        val actual = pomCache.read(dependency)

        assertEquals(null, actual)
    }
}
