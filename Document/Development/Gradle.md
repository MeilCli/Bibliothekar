# Gradle Development
Bibliothekar is using Kotlin for Gradle/JVM/Android development.  
So we need use Android Studio or IntelliJ IDEA(including Android plugin).

And, [detekt](https://github.com/detekt/detekt) is used for linting and code-formatting at Bibliothekar.

## Setup
1. Install [Android Studio](https://developer.android.com/studio)
1. Fork this repository, and clone forked-repository
1. Open forked-repository on VS Code
1. Install IntelliJ plugins
   - Maybe, Android Studio will recommend for you
1. Write android sdk path into `./local.properties` if you don't set environment variable
   - for example: `sdk.dir=path/to/your/android/sdk`

## Coding convention
### Naming
- General:
  - Don't abbreviate word
    - Exception-rule is only `Id` and `Ok`
  - Acronym word can use, but don't use word that don't found Google-search
    - Regardless of the number of letters, it is treated as one word.
    - for example: `HtmlLogger`, `XmlLogger`
- Kotlin-syntax:
  - const val: lowerCamelCase
  - Enum Entry: UpperCamelCase
  - interface: prefix I
  - class: Don't use postfix Impl

### Class-layout
Class layout order:
1. companion object
1. nested type member
1. property member
1. constructor
1. init
1. function