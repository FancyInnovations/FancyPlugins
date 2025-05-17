plugins {
    id("java-library")
}

allprojects {

    apply(plugin = "java-library")

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

    tasks {
        compileJava {
            options.encoding = Charsets.UTF_8.name()
            options.release = 21
        }
    }
}