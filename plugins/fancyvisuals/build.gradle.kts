import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("java-library")

    id("xyz.jpenilla.run-paper")
    id("com.gradleup.shadow")
    id("de.eldoria.plugin-yml.paper")
}

runPaper.folia.registerTask()

allprojects {
    group = "de.oliver"
    version = getFVVersion()
    description = "Simple, lightweight and fast visual plugin using packets"

    repositories {
        mavenLocal()
        mavenCentral()
        maven(url = "https://repo.papermc.io/repository/maven-public/")
        maven(url = "https://repo.fancyinnovations.com/releases")
        maven(url = "https://repo.lushplugins.org/releases")
        maven(url = "https://jitpack.io")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT")

    rootProject.subprojects
        .filter { it.path.startsWith(":libraries:packets:implementations") }
        .forEach { implementation(project(it.path)) }
    implementation(project(":libraries:common"))
    implementation(project(":libraries:plugin-tests"))
    implementation(project(":libraries:jdb"))
    implementation(project(":libraries:packets"))
    implementation(project(":libraries:packets:packets-api"))
    implementation(project(":libraries:config"))
    compileOnly("de.oliver.FancyAnalytics:mc-api:0.1.11") // loaded in FancyVisualLoader
    compileOnly("de.oliver.FancyAnalytics:logger:0.0.8") // loaded in FancyVisualLoader

    implementation("org.lushplugins:ChatColorHandler:6.0.0")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")

    // commands
    implementation("org.incendo:cloud-core:2.0.0")
    implementation("org.incendo:cloud-paper:2.0.0-beta.10")
    implementation("org.incendo:cloud-annotations:2.0.0")
    annotationProcessor("org.incendo:cloud-annotations:2.0.0")
}

paper {
    name = "FancyVisuals"
    main = "de.oliver.fancyvisuals.FancyVisuals"
    bootstrapper = "de.oliver.fancyvisuals.loaders.FancyVisualsBootstrapper"
    loader = "de.oliver.fancyvisuals.loaders.FancyVisualsLoader"
    foliaSupported = true
    version = getFVVersion()
    description = "Simple, lightweight and fast visuals plugin using packets"
    apiVersion = "1.19"
    serverDependencies {
        register("PlaceholderAPI") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("MiniPlaceholders") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("LuckPerms") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("PermissionsEx") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
}

tasks {
    runServer {
        minecraftVersion("1.21.10")

        downloadPlugins {
            hangar("ViaVersion", "5.3.2")
            hangar("ViaBackwards", "5.3.2")
            hangar("PlaceholderAPI", "2.11.6")
//            modrinth("multiverse-core", "4.3.11")
        }
    }

    shadowJar {
        archiveClassifier.set("")
        archiveBaseName.set("FancyVisuals")
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
            "version" to project.version,
            "hash" to getCurrentCommitHash(),
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

fun getCurrentCommitHash(): String {
    return ""
}

fun getFVVersion(): String {
    return file("VERSION").readText()
}