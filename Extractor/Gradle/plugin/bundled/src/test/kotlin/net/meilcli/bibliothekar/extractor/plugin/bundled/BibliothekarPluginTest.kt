package net.meilcli.bibliothekar.extractor.plugin.bundled

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

class BibliothekarPluginTest {

    @Test
    fun testApply() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("net.meilcli.bibliothekar.extractor.plugin.bundled")
    }
}
