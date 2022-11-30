package net.meilcli.bibliothekar.extractor.plugin.core.entities.xml

import net.meilcli.bibliothekar.extractor.plugin.core.BibliothekarException
import net.meilcli.bibliothekar.extractor.plugin.core.entities.Pom
import org.junit.Assert.*
import org.junit.Test

class PomXmlParserTest {

    private fun readFile(fileName: String): Pom {
        return this.javaClass
            .classLoader
            .getResourceAsStream(fileName)
            ?.let { PomXmlParser.parse(it) }
            ?: throw BibliothekarException("cannot read $fileName")
    }

    @Test
    fun testFull() {
        val project = readFile("test-pom-full.xml")

        assertEquals("test-group", project.group)
        assertEquals("test-artifact", project.artifact)
        assertEquals("1.0.0", project.version)
        assertEquals("test-name", project.name)
        assertEquals("test-description", project.description)
        assertEquals("https://meilcli.net", project.url)
        assertEquals(1, project.licenses.size)
        assertEquals("The Apache License, Version 2.0", project.licenses.first().name)
        assertEquals("http://www.apache.org/licenses/LICENSE-2.0.txt", project.licenses.first().url)
        assertEquals(1, project.developers.size)
        assertEquals("test-name", project.developers.first().name)
        assertEquals("test-organization", project.developers.first().organization)
        assertNotNull(project.parent)
        assertEquals("test-group", project.parent?.group)
        assertEquals("test-artifact", project.parent?.artifact)
        assertEquals("1.0.0", project.parent?.version)
        assertNotNull(project.organization)
        assertEquals("test-organization", project.organization?.name)
    }

    @Test
    fun testWithEmptyUrl() {
        val project = readFile("test-pom-with-empty-url.xml")

        assertEquals("test-group", project.group)
        assertEquals("test-artifact", project.artifact)
        assertEquals("1.0.0", project.version)
        assertEquals("test-name", project.name)
        assertEquals("test-description", project.description)
        assertNull(project.url)
        assertEquals(1, project.licenses.size)
        assertEquals("The Apache License, Version 2.0", project.licenses.first().name)
        assertEquals("http://www.apache.org/licenses/LICENSE-2.0.txt", project.licenses.first().url)
        assertEquals(1, project.developers.size)
        assertEquals("test-name", project.developers.first().name)
        assertEquals("test-organization", project.developers.first().organization)
        assertNotNull(project.parent)
        assertEquals("test-group", project.parent?.group)
        assertEquals("test-artifact", project.parent?.artifact)
        assertEquals("1.0.0", project.parent?.version)
        assertNotNull(project.organization)
        assertEquals("test-organization", project.organization?.name)
    }

    @Test
    fun testWithNullUrl() {
        val project = readFile("test-pom-with-null-url.xml")

        assertEquals("test-group", project.group)
        assertEquals("test-artifact", project.artifact)
        assertEquals("1.0.0", project.version)
        assertEquals("test-name", project.name)
        assertEquals("test-description", project.description)
        assertNull(project.url)
        assertEquals(1, project.licenses.size)
        assertEquals("The Apache License, Version 2.0", project.licenses.first().name)
        assertEquals("http://www.apache.org/licenses/LICENSE-2.0.txt", project.licenses.first().url)
        assertEquals(1, project.developers.size)
        assertEquals("test-name", project.developers.first().name)
        assertEquals("test-organization", project.developers.first().organization)
        assertNotNull(project.parent)
        assertEquals("test-group", project.parent?.group)
        assertEquals("test-artifact", project.parent?.artifact)
        assertEquals("1.0.0", project.parent?.version)
        assertNotNull(project.organization)
        assertEquals("test-organization", project.organization?.name)
    }

    @Test
    fun testWithoutLicenses() {
        val project = readFile("test-pom-without-licenses.xml")

        assertEquals("test-group", project.group)
        assertEquals("test-artifact", project.artifact)
        assertEquals("1.0.0", project.version)
        assertEquals("test-name", project.name)
        assertEquals("test-description", project.description)
        assertEquals("https://meilcli.net", project.url)
        assertEquals(0, project.licenses.size)
        assertEquals(1, project.developers.size)
        assertEquals("test-name", project.developers.first().name)
        assertEquals("test-organization", project.developers.first().organization)
        assertNotNull(project.parent)
        assertEquals("test-group", project.parent?.group)
        assertEquals("test-artifact", project.parent?.artifact)
        assertEquals("1.0.0", project.parent?.version)
    }

    @Test
    fun testWithoutDevelopers() {
        val project = readFile("test-pom-without-developers.xml")

        assertEquals("test-group", project.group)
        assertEquals("test-artifact", project.artifact)
        assertEquals("1.0.0", project.version)
        assertEquals("test-name", project.name)
        assertEquals("test-description", project.description)
        assertEquals("https://meilcli.net", project.url)
        assertEquals(1, project.licenses.size)
        assertEquals("The Apache License, Version 2.0", project.licenses.first().name)
        assertEquals("http://www.apache.org/licenses/LICENSE-2.0.txt", project.licenses.first().url)
        assertEquals(0, project.developers.size)
        assertNotNull(project.parent)
        assertEquals("test-group", project.parent?.group)
        assertEquals("test-artifact", project.parent?.artifact)
        assertEquals("1.0.0", project.parent?.version)
    }

    @Test
    fun testWithoutParent() {
        val project = readFile("test-pom-without-parent.xml")

        assertEquals("test-group", project.group)
        assertEquals("test-artifact", project.artifact)
        assertEquals("1.0.0", project.version)
        assertEquals("test-name", project.name)
        assertEquals("test-description", project.description)
        assertEquals("https://meilcli.net", project.url)
        assertEquals(1, project.licenses.size)
        assertEquals("The Apache License, Version 2.0", project.licenses.first().name)
        assertEquals("http://www.apache.org/licenses/LICENSE-2.0.txt", project.licenses.first().url)
        assertEquals(1, project.developers.size)
        assertEquals("test-name", project.developers.first().name)
        assertEquals("test-organization", project.developers.first().organization)
        assertNull(project.parent)
    }

    @Test
    fun testWithoutOrganization() {
        val project = readFile("test-pom-without-organization.xml")

        assertEquals("test-group", project.group)
        assertEquals("test-artifact", project.artifact)
        assertEquals("1.0.0", project.version)
        assertEquals("test-name", project.name)
        assertEquals("test-description", project.description)
        assertEquals("https://meilcli.net", project.url)
        assertEquals(1, project.licenses.size)
        assertEquals("The Apache License, Version 2.0", project.licenses.first().name)
        assertEquals("http://www.apache.org/licenses/LICENSE-2.0.txt", project.licenses.first().url)
        assertEquals(1, project.developers.size)
        assertEquals("test-name", project.developers.first().name)
        assertEquals("test-organization", project.developers.first().organization)
        assertNotNull(project.parent)
        assertEquals("test-group", project.parent?.group)
        assertEquals("test-artifact", project.parent?.artifact)
        assertEquals("1.0.0", project.parent?.version)
        assertNull(project.organization)
    }

    @Test
    fun testWithoutNameSpace() {
        val project = readFile("test-pom-without-namespace.xml")

        assertEquals("test-group", project.group)
        assertEquals("test-artifact", project.artifact)
        assertEquals("1.0.0", project.version)
        assertEquals("test-name", project.name)
        assertEquals("test-description", project.description)
        assertEquals("https://meilcli.net", project.url)
        assertEquals(1, project.licenses.size)
        assertEquals("The Apache License, Version 2.0", project.licenses.first().name)
        assertEquals("http://www.apache.org/licenses/LICENSE-2.0.txt", project.licenses.first().url)
        assertEquals(1, project.developers.size)
        assertEquals("test-name", project.developers.first().name)
        assertEquals("test-organization", project.developers.first().organization)
        assertNotNull(project.parent)
        assertEquals("test-group", project.parent?.group)
        assertEquals("test-artifact", project.parent?.artifact)
        assertEquals("1.0.0", project.parent?.version)
        assertNotNull(project.organization)
        assertEquals("test-organization", project.organization?.name)
    }
}
