package net.meilcli.bibliothekar.extractor.plugin.core.pom

import net.meilcli.bibliothekar.extractor.plugin.core.entities.Dependency
import net.meilcli.bibliothekar.extractor.plugin.core.entities.Pom
import net.meilcli.bibliothekar.extractor.plugin.core.entities.json.IJsonDeserializer
import net.meilcli.bibliothekar.extractor.plugin.core.entities.json.IJsonSerializer
import java.io.File
import java.nio.file.Paths

class PomReporter(
    private val pomJsonSerializer: IJsonSerializer<Pom>,
    private val pomJsonDeserializer: IJsonDeserializer<Pom>
) {

    private val _poms = mutableListOf<Pom>()
    private val dependencies = mutableListOf<Dependency>()

    val poms: List<Pom>
        get() = _poms

    fun readCache(cacheDirectory: File) {
        val files = cacheDirectory.listFiles()
            ?.flatMap {
                it?.listFiles()
                    ?.toList()
                    .orEmpty()
            }
            ?.flatMap {
                it?.listFiles()
                    ?.toList()
                    .orEmpty()
            }
            ?.flatMap {
                it?.listFiles()
                    ?.toList()
                    .orEmpty()
            }
            .orEmpty()
            .filter { it.name == "pom.json" }

        for (file in files) {
            val pom = pomJsonDeserializer.deserialize(file.readText())
            if (dependencies.contains(pom.toDependency())) {
                continue
            }
            _poms += pom
            dependencies += pom.toDependency()
        }
    }

    fun report(outputDirectory: File) {
        for (pom in _poms) {
            report(outputDirectory, pom)
        }
    }

    private fun report(outputDirectory: File, pom: Pom) {
        val path = pom.toDependency().toFilePath(outputDirectory)
        val parent = path.parentFile
        if (parent.exists().not()) {
            parent.mkdirs()
        }

        val json = pomJsonSerializer.serialize(pom)
        path.writeText(json)
    }

    private fun Dependency.toFilePath(outputDirectory: File): File {
        val groupPath = group.ifEmpty { "undefined" }
        val namePath = name.ifEmpty { "undefined" }
        val versionPath = version.ifEmpty { "undefined" }
        return outputDirectory.toPath().resolve(Paths.get("$groupPath/$namePath/$versionPath/pom.json")).toFile()
    }
}
