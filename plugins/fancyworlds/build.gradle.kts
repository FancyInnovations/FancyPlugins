plugins {
    id("java-library")
    id("maven-publish")

    id("xyz.jpenilla.run-paper")
    id("com.gradleup.shadow")
}

runPaper.folia.registerTask()

allprojects {
    group = "com.fancyinnovations"
    version = getFWVersion()
    description = "Simple, lightweight and fast world management plugin"

    repositories {
        mavenLocal()
        mavenCentral()

        maven(url = "https://repo.papermc.io/repository/maven-public/")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")

        maven(url = "https://maven.fancyspaces.net/fancyinnovations/releases")
        maven(url = "https://repo.fancyinnovations.com/snapshots")
        maven(url = "https://repo.fancyinnovations.com/releases")
        maven(url = "https://repo.lushplugins.org/releases")
        maven(url = "https://repo.viaversion.com/")
        maven(url = "https://repo.opencollab.dev/main/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")

    implementation(project(":plugins:fancyworlds:fw-api"))

    implementation(project(":libraries:common"))
    implementation(project(":libraries:jdb"))
    implementation(project(":libraries:config"))
    implementation("de.oliver.FancyAnalytics:java-sdk:0.0.6")
    implementation("de.oliver.FancyAnalytics:mc-api:0.1.13")
    implementation("de.oliver.FancyAnalytics:logger:0.0.10")

    compileOnly("de.oliver:FancyDialogs:1.1.2")
    compileOnly("org.lushplugins.chatcolorhandler:paper:8.1.0")
    implementation("io.github.revxrsal:lamp.common:4.0.0-rc.16")
    implementation("io.github.revxrsal:lamp.bukkit:4.0.0-rc.16")

    implementation("org.jetbrains:annotations:26.1.0")
}

tasks {
    runServer {
        minecraftVersion("26.1.2")

        downloadPlugins {
            modrinth("FancyDialogs", "1.1.2.53")
//            modrinth("FancyNpcs", "2.9.2")
//            modrinth("FancyHolograms", "2.9.1")
//            modrinth("FancyDialogs", "1.1.2")
//            modrinth("FancyEconomy", "1.0.3+6")

//            hangar("PlaceholderAPI", "2.11.6")
//            hangar("ViaVersion", "5.8.1")
//            hangar("ViaBackwards", "5.8.1")
        }
    }

    shadowJar {
        archiveClassifier.set("")
        archiveBaseName.set("FancyWorlds")

        dependsOn(":plugins:fancyworlds:fw-api:shadowJar")
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
            "version" to getFWVersion(),
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

fun getFWVersion(): String {
    return file("VERSION").readText()
}