plugins {
    id("java-library")
    id("maven-publish")

    id("xyz.jpenilla.run-paper")
    id("com.gradleup.shadow")
}

runPaper.folia.registerTask()

allprojects {
    group = "com.fancyinnovations"
    version = getFNMVersion()
    description = "Addon for FancyNpcs that adds support for custom models"
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")

    compileOnly(project(":plugins:fancynpcs-v2:fn-v2-api"))
    compileOnly("io.github.toxicity188:bettermodel-bukkit-api:3.0.1")

    implementation(project(":libraries:common"))
    implementation(project(":libraries:jdb"))
    implementation(project(":libraries:config"))
    implementation("de.oliver.FancyAnalytics:java-sdk:0.0.6")
    implementation("de.oliver.FancyAnalytics:mc-api:0.1.13")
    implementation("de.oliver.FancyAnalytics:logger:0.0.10")

    compileOnly("org.incendo:cloud-core:2.0.0")
    compileOnly("org.incendo:cloud-paper:2.0.0-SNAPSHOT")
    compileOnly("org.incendo:cloud-annotations:2.0.0")
    annotationProcessor("org.incendo:cloud-annotations:2.0.0")

    implementation("org.jetbrains:annotations:26.1.0")
}

tasks {
    runServer {
        minecraftVersion("26.1.2")

        downloadPlugins {
            modrinth("FancyNpcs", "2.10.0.359")
            modrinth("BetterModel", "4h8rX3rt") // 3.0.2-SNAPSHOT-492
//            modrinth("FancyDialogs", "1.1.2.53")
//            modrinth("FancyHolograms", "2.9.1")
//            modrinth("FancyDialogs", "1.1.2")
//            modrinth("FancyEconomy", "1.0.3+6")

//            hangar("PlaceholderAPI", "2.11.6")
//            hangar("ViaVersion", "5.8.1")
//            hangar("ViaBackwards", "5.8.1")
        }
    }

    shadowJar {
        relocate("org.incendo", "de.oliver")
        archiveClassifier.set("")
        archiveBaseName.set("FancyNpcsModel")
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
        options.release = 21
        // For cloud-annotations, see https://cloud.incendo.org/annotations/#command-components
        options.compilerArgs.add("-parameters")
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything

        val props = mapOf(
            "description" to project.description,
            "version" to getFNMVersion(),
            "commit_hash" to gitCommitHash.get(),
            "channel" to (System.getenv("RELEASE_CHANNEL") ?: "").ifEmpty { "undefined" },
            "platform" to (System.getenv("RELEASE_PLATFORM") ?: "").ifEmpty { "undefined" }
        )

        inputs.properties(props)

        filesMatching("paper-plugin.yml") {
            expand(props)
        }

        filesMatching("version.yml") {
            expand(props)
        }
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

val gitCommitHash: Provider<String> = providers.exec {
    commandLine("git", "rev-parse", "HEAD")
}.standardOutput.asText.map { it.trim() }

val gitCommitMessage: Provider<String> = providers.exec {
    commandLine("git", "log", "-1", "--pretty=%B")
}.standardOutput.asText.map { it.trim() }

fun getFNMVersion(): String {
    return file("VERSION").readText()
}