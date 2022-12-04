package net.meilcli.bibliothekar.extractor.plugin.jvm

fun String.unifyLineBreak(): String {
    return replace("\r\n", "\n")
}
