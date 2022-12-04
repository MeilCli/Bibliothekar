package net.meilcli.bibliothekar.extractor.plugin.android

fun String.unifyLineBreak(): String {
    return replace("\r\n", "\n")
}
