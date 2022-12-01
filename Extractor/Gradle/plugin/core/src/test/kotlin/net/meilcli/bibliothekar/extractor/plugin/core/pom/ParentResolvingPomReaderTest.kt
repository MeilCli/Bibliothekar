package net.meilcli.bibliothekar.extractor.plugin.core.pom

import net.meilcli.bibliothekar.extractor.plugin.core.entities.*
import org.junit.Assert.assertEquals
import org.junit.Test

class ParentResolvingPomReaderTest {

    @Test
    fun testNoParent() {
        val pom = Pom(
            group = "net.meilcli.bibliothekar",
            artifact = "bibliothekar",
            version = "1.0.0",
            name = null,
            description = null,
            url = null,
            parent = null,
            organization = null,
            licenses = emptyList(),
            developers = emptyList()
        )
        val inMemoryPomCache = InMemoryPomCache().apply {
            write(pom)
        }
        val parentResolvingPomReader = ParentResolvingPomReader(inMemoryPomCache)

        val actual = parentResolvingPomReader.read(pom.toDependency())

        assertEquals(pom, actual)
    }

    @Test
    @Suppress("detekt.LongMethod")
    fun testOverride() {
        val pom = Pom(
            group = "net.meilcli.bibliothekar",
            artifact = "bibliothekar1",
            version = "1.0.0",
            name = null,
            description = null,
            url = null,
            parent = PomParent(
                group = "net.meilcli.bibliothekar",
                artifact = "bibliothekar2",
                version = "1.0.0"
            ),
            organization = null,
            licenses = emptyList(),
            developers = emptyList()
        )
        val parent = Pom(
            group = "net.meilcli.bibliothekar",
            artifact = "bibliothekar2",
            version = "1.0.0",
            name = "Bibliothekar",
            description = "The bibliothekar",
            url = "https://meilcli.net",
            parent = null,
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
        val inMemoryPomCache = InMemoryPomCache().apply {
            write(pom)
            write(parent)
        }
        val parentResolvingPomReader = ParentResolvingPomReader(inMemoryPomCache)

        val actual = parentResolvingPomReader.read(pom.toDependency())

        assertEquals(
            Pom(
                group = "net.meilcli.bibliothekar",
                artifact = "bibliothekar1",
                version = "1.0.0",
                name = "Bibliothekar",
                description = "The bibliothekar",
                url = "https://meilcli.net",
                parent = PomParent(
                    group = "net.meilcli.bibliothekar",
                    artifact = "bibliothekar2",
                    version = "1.0.0"
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
            ),
            actual
        )
    }

    @Test
    @Suppress("detekt.LongMethod")
    fun testTransitiveOverride() {
        val pom = Pom(
            group = "net.meilcli.bibliothekar",
            artifact = "bibliothekar1",
            version = "1.0.0",
            name = null,
            description = null,
            url = null,
            parent = PomParent(
                group = "net.meilcli.bibliothekar",
                artifact = "bibliothekar2",
                version = "1.0.0"
            ),
            organization = null,
            licenses = emptyList(),
            developers = emptyList()
        )
        val parent = Pom(
            group = "net.meilcli.bibliothekar",
            artifact = "bibliothekar2",
            version = "1.0.0",
            name = "Bibliothekar",
            description = "The bibliothekar",
            url = "https://meilcli.net",
            parent = PomParent(
                group = "net.meilcli.bibliothekar",
                artifact = "bibliothekar3",
                version = "1.0.0"
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
        val parentParent = Pom(
            group = "net.meilcli.bibliothekar",
            artifact = "bibliothekar3",
            version = "1.0.0",
            name = "Bibliothekar!",
            description = "The bibliothekar!",
            url = "https://meilcli.net/bibliothekar",
            parent = null,
            organization = PomOrganization(
                name = "OSS/Bibliothekar"
            ),
            licenses = listOf(
                PomLicense(
                    name = "MIT License",
                    url = "https://meilcli.net/bibliothekar"
                )
            ),
            developers = listOf(
                PomDeveloper(
                    name = "@MeilCli",
                    organization = "OSS/Bibliothekar"
                )
            )
        )
        val inMemoryPomCache = InMemoryPomCache().apply {
            write(pom)
            write(parent)
            write(parentParent)
        }
        val parentResolvingPomReader = ParentResolvingPomReader(inMemoryPomCache)

        val actual = parentResolvingPomReader.read(pom.toDependency())

        assertEquals(
            Pom(
                group = "net.meilcli.bibliothekar",
                artifact = "bibliothekar1",
                version = "1.0.0",
                name = "Bibliothekar",
                description = "The bibliothekar",
                url = "https://meilcli.net",
                parent = PomParent(
                    group = "net.meilcli.bibliothekar",
                    artifact = "bibliothekar2",
                    version = "1.0.0"
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
            ),
            actual
        )
    }

    @Test
    @Suppress("detekt.LongMethod")
    fun testNoOverride() {
        val pom = Pom(
            group = "net.meilcli.bibliothekar",
            artifact = "bibliothekar1",
            version = "1.0.0",
            name = "Bibliothekar",
            description = "The bibliothekar",
            url = "https://meilcli.net",
            parent = PomParent(
                group = "net.meilcli.bibliothekar",
                artifact = "bibliothekar2",
                version = "1.0.0"
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
        val parent = Pom(
            group = "net.meilcli.bibliothekar",
            artifact = "bibliothekar2",
            version = "1.0.0",
            name = "Bibliothekar!",
            description = "The bibliothekar!",
            url = "https://meilcli.net/bibliothekar",
            parent = null,
            organization = PomOrganization(
                name = "OSS/Bibliothekar"
            ),
            licenses = listOf(
                PomLicense(
                    name = "MIT License",
                    url = "https://meilcli.net/bibliothekar"
                )
            ),
            developers = listOf(
                PomDeveloper(
                    name = "@MeilCli",
                    organization = "OSS/Bibliothekar"
                )
            )
        )
        val inMemoryPomCache = InMemoryPomCache().apply {
            write(pom)
            write(parent)
        }
        val parentResolvingPomReader = ParentResolvingPomReader(inMemoryPomCache)

        val actual = parentResolvingPomReader.read(pom.toDependency())

        assertEquals(pom, actual)
    }

    @Test
    fun testLoopDetected_same() {
        val pom = Pom(
            group = "net.meilcli.bibliothekar",
            artifact = "bibliothekar",
            version = "1.0.0",
            name = null,
            description = null,
            url = null,
            parent = PomParent(
                group = "net.meilcli.bibliothekar",
                artifact = "bibliothekar",
                version = "1.0.0"
            ),
            organization = null,
            licenses = emptyList(),
            developers = emptyList()
        )
        val inMemoryPomCache = InMemoryPomCache().apply {
            write(pom)
        }
        val parentResolvingPomReader = ParentResolvingPomReader(inMemoryPomCache)

        val actual = parentResolvingPomReader.read(pom.toDependency())

        assertEquals(pom, actual)
    }

    @Test
    fun testLoopDetected_parent() {
        val pom = Pom(
            group = "net.meilcli.bibliothekar",
            artifact = "bibliothekar1",
            version = "1.0.0",
            name = null,
            description = null,
            url = null,
            parent = PomParent(
                group = "net.meilcli.bibliothekar",
                artifact = "bibliothekar",
                version = "1.0.0"
            ),
            organization = null,
            licenses = emptyList(),
            developers = emptyList()
        )
        val parent = Pom(
            group = "net.meilcli.bibliothekar",
            artifact = "bibliothekar2",
            version = "1.0.0",
            name = null,
            description = null,
            url = null,
            parent = PomParent(
                group = "net.meilcli.bibliothekar",
                artifact = "bibliothekar3",
                version = "1.0.0"
            ),
            organization = null,
            licenses = emptyList(),
            developers = emptyList()
        )
        val parentParent = Pom(
            group = "net.meilcli.bibliothekar",
            artifact = "bibliothekar3",
            version = "1.0.0",
            name = null,
            description = null,
            url = null,
            parent = PomParent(
                group = "net.meilcli.bibliothekar",
                artifact = "bibliothekar2",
                version = "1.0.0"
            ),
            organization = null,
            licenses = emptyList(),
            developers = emptyList()
        )
        val inMemoryPomCache = InMemoryPomCache().apply {
            write(pom)
            write(parent)
            write(parentParent)
        }
        val parentResolvingPomReader = ParentResolvingPomReader(inMemoryPomCache)

        val actual = parentResolvingPomReader.read(pom.toDependency())

        assertEquals(pom, actual)
    }
}
