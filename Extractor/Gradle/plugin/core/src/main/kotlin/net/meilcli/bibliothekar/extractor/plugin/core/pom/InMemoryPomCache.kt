package net.meilcli.bibliothekar.extractor.plugin.core.pom

import net.meilcli.bibliothekar.extractor.plugin.core.entities.Dependency
import net.meilcli.bibliothekar.extractor.plugin.core.entities.PomProject
import java.util.concurrent.ConcurrentHashMap

class InMemoryPomCache : IPomCache {

    private val cache = ConcurrentHashMap<Dependency, PomProject>()

    override fun read(dependency: Dependency): PomProject? {
        return cache[dependency]
    }

    override fun write(pomProject: PomProject) {
        cache[pomProject.toDependency()] = pomProject
    }
}
