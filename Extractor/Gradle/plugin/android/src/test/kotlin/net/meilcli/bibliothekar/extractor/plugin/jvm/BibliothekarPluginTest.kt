package net.meilcli.bibliothekar.extractor.plugin.jvm

import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test

class BibliothekarPluginTest {

    @Test
    fun testApply() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("net.meilcli.bibliothekar.extractor.plugin.android")
    }
}
