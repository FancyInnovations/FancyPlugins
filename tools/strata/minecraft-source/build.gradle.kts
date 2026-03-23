plugins {
    id("java")
    id("maven-publish")
    id("com.gradleup.shadow")
//    id("com.fancyinnovations.strata") version "0.0.1-LOCAL-SNAPSHOT"
}

group = "com.fancyinnovations"
description = "Minecraft server source code"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

repositories {
    maven(url = "https://libraries.minecraft.net/")
}

dependencies {
    implementation(fileTree("../strata-cache/server-libraries/libraries-26.1-pre-3") {
        include("**/*.jar")
    })

    implementation("org.jetbrains:annotations:24.0.1")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("org.checkerframework:checker-qual:3.49.0")
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "net.minecraft.server.Main"
        }
    }

    shadowJar {
        archiveFileName.set("minecraft-server.jar")
        archiveClassifier.set("")
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()

        options.release.set(25)
        options.compilerArgs.addAll(listOf("-Xmaxerrs", "10000"))
    }

    java {
        withSourcesJar()
        withJavadocJar()
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
}
