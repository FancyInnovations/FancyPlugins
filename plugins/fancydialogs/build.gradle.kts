import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("java-library")
    id("maven-publish")

    id("xyz.jpenilla.run-paper")
    id("com.gradleup.shadow")
    id("net.minecrell.plugin-yml.paper")
    id("io.papermc.hangar-publish-plugin")
    id("com.modrinth.minotaur")
}

runPaper.folia.registerTask()

val supportedVersions =
    listOf(
        "1.21.5",
    )

//allprojects {
//    group = "de.oliver"
//    version = getFDVersion()
//    description = "Simple, lightweight and fast dialog plugin using the new dialog feature"
//
//    repositories {
//        mavenLocal()
//        mavenCentral()
//
//        maven(url = "https://repo.papermc.io/repository/maven-public/")
//        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
//
//        maven(url = "https://repo.fancyinnovations.com/snapshots")
//        maven(url = "https://repo.fancyinnovations.com/releases")
//        maven(url = "https://repo.lushplugins.org/releases")
//        maven(url = "https://repo.viaversion.com/")
//        maven(url = "https://repo.opencollab.dev/main/")
//    }
//}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.5-R0.1-SNAPSHOT")

    rootProject.subprojects
        .filter { it.path.startsWith(":libraries:packets:implementations") }
        .forEach { implementation(project(it.path)) }
    implementation(project(":libraries:packets:packets-api"))
    implementation(project(":libraries:packets:factories"))
    implementation(project(":libraries:common"))
    implementation(project(":libraries:jdb"))
    implementation("de.oliver.FancyAnalytics:api:0.1.6")
    implementation("de.oliver.FancyAnalytics:logger:0.0.6")

    compileOnly("org.lushplugins:ChatColorHandler:5.1.3")
}

paper {
    name = "FancyDialogs"
    main = "com.fancyinnovations.fancydialogs.FancyDialogsPlugin"
    bootstrapper = "com.fancyinnovations.fancydialogs.loaders.FancyDialogsBootstrapper"
    loader = "com.fancyinnovations.fancydialogs.loaders.FancyDialogsLoader"
    foliaSupported = true
    version = getFDVersion()
    description = "Simple, lightweight and fast dialog plugin using the new dialog feature"
    apiVersion = "1.21"
    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD
    serverDependencies {
        register("MiniPlaceholders") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("PlaceholderAPI") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
}

tasks {
    runServer {
        minecraftVersion("1.21.5")

        downloadPlugins {
            modrinth("fancynpcs", "2.5.0")
            hangar("ViaVersion", "5.3.2")
            hangar("ViaBackwards", "5.3.2")
//            modrinth("multiverse-core", "4.3.11")
            hangar("PlaceholderAPI", "2.11.6")
        }
    }

    shadowJar {
        archiveClassifier.set("")
        archiveBaseName.set("FancyDialogs")
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
            "version" to getFDVersion(),
            "hash" to gitCommitHash.get(),
            "build" to (System.getenv("BUILD_ID") ?: "").ifEmpty { "undefined" }
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

tasks.publishAllPublicationsToHangar {
    dependsOn("shadowJar")
}

tasks.modrinth {
    dependsOn("shadowJar")
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

fun getFDVersion(): String {
    return file("VERSION").readText()
}

hangarPublish {
    publications.register("plugin") {
        version = project.version as String
        id = "FancyDialogs"
        channel = "Alpha"

        apiKey.set(System.getenv("HANGAR_PUBLISH_API_TOKEN"))

        platforms {
            paper {
                jar = tasks.shadowJar.flatMap { it.archiveFile }
                platformVersions = supportedVersions
            }
        }

        changelog = gitCommitMessage.get()
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_PUBLISH_API_TOKEN"))
    projectId.set("FancyDialogs")
    versionNumber.set(getFDVersion())
    versionType.set("alpha")
    uploadFile.set(file("build/libs/${project.name}-${getFDVersion()}.jar"))
    gameVersions.addAll(supportedVersions)
    loaders.add("paper")
    loaders.add("folia")
    changelog.set(gitCommitMessage.get())
}