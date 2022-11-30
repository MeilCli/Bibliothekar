package net.meilcli.bibliothekar.extractor.plugin.core.pom

import net.meilcli.bibliothekar.extractor.plugin.core.entities.Dependency
import net.meilcli.bibliothekar.extractor.plugin.core.entities.Pom

class CompositeCachingPomReader(
    private val gradlePomReader: IPomReader,
    private val inMemoryPomCache: IPomCache
) : IPomReader {

    override fun read(dependency: Dependency): Pom? {
        var result = inMemoryPomCache.read(dependency)
        if (result != null) {
            return result
        }

        result = gradlePomReader.read(dependency) ?: return null

        inMemoryPomCache.write(result)

        return result
    }
}
