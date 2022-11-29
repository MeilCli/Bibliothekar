package net.meilcli.bibliothekar.extractor.plugin.core.entities

data class PomProject(
    val group: String,
    val artifact: String,
    val version: String,
    val name: String?,
    val description: String?,
    val url: String?,
    val parent: PomParentProject?,
    val organization: PomOrganization?,
    val licenses: List<PomLicense>,
    val developers: List<PomDeveloper>
) {

    fun toDependency(): Dependency {
        return Dependency(
            group = group,
            name = artifact,
            version = version
        )
    }
}
