import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("java-library")
    id("maven-publish")

    id("xyz.jpenilla.run-paper")
    id("com.gradleup.shadow")
    id("de.eldoria.plugin-yml.paper")
}

runPaper.folia.registerTask()

allprojects {
    group = "de.oliver"
    version = getFDVersion()
    description = "Simple, lightweight and fast dialog plugin using the new dialog feature"

    repositories {
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven(url = "https://repo.viaversion.com/")
        maven(url = "https://repo.opencollab.dev/main/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")

    implementation(project(":plugins:fancydialogs:fd-api"))

    rootProject.subprojects
        .filter { it.path.startsWith(":libraries:packets:implementations") }
        .forEach { implementation(project(it.path)) }
    implementation(project(":libraries:packets"))
    implementation(project(":libraries:packets:packets-api"))
    implementation(project(":libraries:common"))
    implementation(project(":libraries:jdb"))
    implementation(project(":libraries:config"))
    implementation("de.oliver.FancyAnalytics:java-sdk:0.0.6")
    implementation("de.oliver.FancyAnalytics:mc-api:0.1.13")
    implementation("de.oliver.FancyAnalytics:logger:0.0.8")

    compileOnly(project(":plugins:fancynpcs-v2:fn-v2-api"))
    compileOnly("org.lushplugins.chatcolorhandler:paper:8.1.0")
    implementation("io.github.revxrsal:lamp.common:4.0.0-rc.16")
    implementation("io.github.revxrsal:lamp.bukkit:4.0.0-rc.16")

    implementation("org.jetbrains:annotations:26.1.0")
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
        register("FancyNpcs") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
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
        minecraftVersion("26.1.2")
        serverJar(file("../../libraries/paper-server/paper-bundler-26.1.2-R0.1-SNAPSHOT.jar"))

        downloadPlugins {
            url("https://fancyspaces.net/api/v1/spaces/s1gGcHj5/versions/3qG7szVk/files/FancyWorlds-0.0.2.jar")
            modrinth("FancyNpcs", "2.9.2")
            modrinth("FancyHolograms", "2.9.1")
            modrinth("FancyEconomy", "1.0.3+6")

//            hangar("PlaceholderAPI", "2.11.6")
//            hangar("ViaVersion", "5.8.1")
//            hangar("ViaBackwards", "5.8.1")
        }
    }

    shadowJar {
        archiveClassifier.set("")
        archiveBaseName.set("FancyDialogs")

        dependsOn(":plugins:fancydialogs:fd-api:shadowJar")
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
        options.release = 25
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
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
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