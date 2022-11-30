package net.meilcli.bibliothekar.extractor.plugin.core.pom

import net.meilcli.bibliothekar.extractor.plugin.core.entities.Dependency
import net.meilcli.bibliothekar.extractor.plugin.core.entities.Pom
import java.util.concurrent.ConcurrentHashMap

class InMemoryPomCache : IPomCache {

    private val cache = ConcurrentHashMap<Dependency, Pom>()

    override fun read(dependency: Dependency): Pom? {
        return cache[dependency]
    }

    override fun write(pom: Pom) {
        cache[pom.toDependency()] = pom
    }
}
