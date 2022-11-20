package net.meilcli.bibliothekar.extractor.plugin.jvm

import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertNotNull

class BibliothekarPluginTest {

    @Test
    fun `plugin registers task`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("net.meilcli.bibliothekar.extractor.plugin.android")

        assertNotNull(project.tasks.findByName("greeting"))
    }
}
