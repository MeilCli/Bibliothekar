package net.meilcli.bibliothekar.extractor.plugin.core.pom

import net.meilcli.bibliothekar.extractor.plugin.core.entities.Dependency
import net.meilcli.bibliothekar.extractor.plugin.core.entities.Pom
import net.meilcli.bibliothekar.extractor.plugin.core.entities.PomParent

class ParentResolvingPomReader(
    private val pomReader: IPomReader
) : IPomReader {

    override fun read(dependency: Dependency): Pom? {
        val target = pomReader.read(dependency) ?: return null
        var parent: PomParent? = target.parent ?: return target
        val parents = mutableListOf<Pom>()

        while (parent != null) {
            val parentDependency = parent.toDependency()
            if (dependency == parentDependency) {
                // loop detected
                break
            }
            if (parents.any { it.toDependency() == parentDependency }) {
                // loop detected
                break
            }
            val pom = pomReader.read(parent.toDependency()) ?: break
            parents += pom
            parent = pom.parent
        }

        var result = target
        for (parentPom in parents) {
            result = result.override(parentPom)
        }
        return result
    }

    private fun Pom.override(value: Pom): Pom {
        return Pom(
            group = group,
            artifact = artifact,
            version = version,
            name = name ?: value.name,
            description = description ?: value.description,
            url = url ?: value.url,
            parent = parent,
            organization = organization ?: value.organization,
            licenses = licenses.ifEmpty { value.licenses },
            developers = developers.ifEmpty { value.developers }
        )
    }
}
