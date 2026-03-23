plugins {
    id("java")
    id("maven-publish")
    id("java-gradle-plugin")
    id("com.gradleup.shadow")
}

group = "com.fancyinnovations"
description = "Tool to decompile Minecraft's server code and extract the source code"
version = getStrataVersion()

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.fancyinnovations.com/releases")
    maven("https://nexus.covers1624.net/repository/maven-releases/")
}

dependencies {
    implementation("io.codechicken:DiffPatch:2.1.0.43")
    implementation("org.vineflower:vineflower:1.11.2")
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("de.oliver.FancyAnalytics:logger:0.0.8")
    implementation("org.jetbrains:annotations:26.1.0")
}

gradlePlugin {
    plugins {
        register("com.fancyinnovations.strata") {
            id = "com.fancyinnovations.strata"
            version = getStrataVersion()
            description = "Gradle plugin for Strata"
            implementationClass = "com.fancyinnovations.strata.plugin.StrataGradlePlugin"
        }
    }
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "com.fancyinnovations.strata.Main"
        }
    }

    shadowJar {
        archiveClassifier.set("")
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(21)
    }

    java {
        withSourcesJar()
        withJavadocJar()
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
}

fun getStrataVersion(): String {
    return file("VERSION").readText()
}
