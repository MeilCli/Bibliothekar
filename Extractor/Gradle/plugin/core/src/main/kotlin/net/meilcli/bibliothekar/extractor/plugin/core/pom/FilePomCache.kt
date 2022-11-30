package net.meilcli.bibliothekar.extractor.plugin.core.pom

import net.meilcli.bibliothekar.extractor.plugin.core.entities.Dependency
import net.meilcli.bibliothekar.extractor.plugin.core.entities.Pom
import net.meilcli.bibliothekar.extractor.plugin.core.entities.json.IJsonDeserializer
import net.meilcli.bibliothekar.extractor.plugin.core.entities.json.IJsonSerializer
import java.io.File
import java.nio.file.Paths
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class FilePomCache(
    private val directory: File,
    private val pomJsonSerializer: IJsonSerializer<Pom>,
    private val pomJsonDeserializer: IJsonDeserializer<Pom>
) : IPomCache {

    companion object {

        // FixME: prepare multiple lock every directory
        private val fileLock = ReentrantReadWriteLock()
    }

    init {
        check(directory.isDirectory) { "$directory is not directory" }
    }

    override fun read(dependency: Dependency): Pom? {
        val path = dependency.toFilePath()
        if (path.exists().not()) {
            return null
        }

        val json = fileLock.read {
            path.readText()
        }

        return pomJsonDeserializer.deserialize(json)
    }

    override fun write(pom: Pom) {
        val path = pom.toDependency().toFilePath()
        val parent = path.parentFile
        if (parent.exists().not()) {
            parent.mkdirs()
        }

        val json = pomJsonSerializer.serialize(pom)
        fileLock.write {
            path.writeText(json)
        }
    }

    private fun Dependency.toFilePath(): File {
        val groupPath = group.ifEmpty { "undefined" }
        val namePath = name.ifEmpty { "undefined" }
        val versionPath = version.ifEmpty { "undefined" }
        return directory.toPath().resolve(Paths.get("$groupPath/$namePath/$versionPath/pom.json")).toFile()
    }
}
