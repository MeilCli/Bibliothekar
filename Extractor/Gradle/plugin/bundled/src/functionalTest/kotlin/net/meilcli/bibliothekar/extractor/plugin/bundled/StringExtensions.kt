package net.meilcli.bibliothekar.extractor.plugin.bundled

fun String.unifyLineBreak(): String {
    return replace("\r\n", "\n")
}
