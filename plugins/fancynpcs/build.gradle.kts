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
    version = getFNVersion()
    description = "Simple, lightweight and fast NPC plugin using packets"

    repositories {
        mavenLocal()
        mavenCentral()
        maven(url = "https://repo.papermc.io/repository/maven-public/")
        maven(url = "https://repo.fancyinnovations.com/releases")
        maven(url = "https://repo.lushplugins.org/releases")
        maven(url = "https://repo.inventivetalent.org/repository/maven-snapshots/") // for cloud command framework
        maven(url = "https://repo.extendedclip.com/releases/") // for PlaceholderAPI
        maven(url = "https://maven.enginehub.org/repo/") // for WorldEdit
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT")

    implementation(project(":plugins:fancynpcs:fn-api"))
    implementation(project(":plugins:fancynpcs:implementation_1_21_11"))
    implementation(project(":plugins:fancynpcs:implementation_1_21_9"))
    implementation(project(":plugins:fancynpcs:implementation_1_21_6"))
    implementation(project(":plugins:fancynpcs:implementation_1_21_5"))
    implementation(project(":plugins:fancynpcs:implementation_1_21_4"))
    implementation(project(":plugins:fancynpcs:implementation_1_21_3"))
    implementation(project(":plugins:fancynpcs:implementation_1_21_1"))
    implementation(project(":plugins:fancynpcs:implementation_1_20_6"))
    implementation(project(":plugins:fancynpcs:implementation_1_20_4", configuration = "reobf"))
    implementation(project(":plugins:fancynpcs:implementation_1_20_2", configuration = "reobf"))
    implementation(project(":plugins:fancynpcs:implementation_1_20_1", configuration = "reobf"))
    implementation(project(":plugins:fancynpcs:implementation_1_20", configuration = "reobf"))

    rootProject.subprojects
        .filter { it.path.startsWith(":libraries:packets:implementations") }
        .forEach { implementation(project(it.path)) }
    implementation(project(":libraries:packets"))
    implementation(project(":libraries:packets:packets-api"))
    implementation(project(":libraries:common"))
    implementation(project(":libraries:jdb"))
    implementation(project(":libraries:plugin-tests"))
    implementation(project(":libraries:config"))
    compileOnly("org.lushplugins:ChatColorHandler:6.0.0")
    implementation("de.oliver.FancyAnalytics:java-sdk:0.0.4")
    implementation("de.oliver.FancyAnalytics:mc-api:0.1.11")
    implementation("de.oliver.FancyAnalytics:logger:0.0.8")
    implementation("org.incendo:cloud-core:2.0.0")
    implementation("org.incendo:cloud-paper:2.0.0-beta.13")
    implementation("org.incendo:cloud-annotations:2.0.0")
    annotationProcessor("org.incendo:cloud-annotations:2.0.0")
    implementation("org.mineskin:java-client-jsoup:3.0.3-SNAPSHOT")

    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.intellectualsites.plotsquared:plotsquared-core:7.5.6")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.14")
}

paper {
    name = "FancyNpcs"
    main = "de.oliver.fancynpcs.FancyNpcs"
    bootstrapper = "de.oliver.fancynpcs.loaders.FancyNpcsBootstrapper"
    loader = "de.oliver.fancynpcs.loaders.FancyNpcsLoader"
    foliaSupported = true
    version = getFNVersion()
    description = "Simple, lightweight and fast NPC plugin using packets"
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
        register("PlotSquared") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
}

tasks {
    runServer {
        minecraftVersion("1.21.10")

        downloadPlugins {
//            hangar("ViaVersion", "5.4.0")
//            hangar("ViaBackwards", "5.4.0")
//            hangar("PlaceholderAPI", "2.11.6")
//            modrinth("multiverse-core", "5.0.2")
        }
    }

    shadowJar {
        relocate("org.incendo", "de.oliver")
        relocate("org.lushplugins.chatcolorhandler", "de.oliver.fancynpcs.libs.chatcolorhandler")
        archiveClassifier.set("")
        archiveBaseName.set("FancyNpcs")
        dependsOn(":plugins:fancynpcs:fn-api:shadowJar")
    }

    publishing {
        repositories {
            maven {
                name = "fancyinnovationsReleases"
                url = uri("https://repo.fancyinnovations.com/releases")
                credentials(PasswordCredentials::class)
                authentication {
                    isAllowInsecureProtocol = true
                    create<BasicAuthentication>("basic")
                }
            }

            maven {
                name = "fancyinnovationsSnapshots"
                url = uri("https://repo.fancyinnovations.com/snapshots")
                credentials(PasswordCredentials::class)
                authentication {
                    isAllowInsecureProtocol = true
                    create<BasicAuthentication>("basic")
                }
            }
        }
        publications {
            create<MavenPublication>("maven") {
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()
                from(project.components["java"])
            }
        }
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
            "version" to getFNVersion(),
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

fun getFNVersion(): String {
    return file("VERSION").readText()
}
