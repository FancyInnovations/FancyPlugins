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
        "1.19.4",
        "1.20",
        "1.20.1",
        "1.20.2",
        "1.20.3",
        "1.20.4",
        "1.20.5",
        "1.20.6",
        "1.21",
        "1.21.1",
        "1.21.2",
        "1.21.3",
        "1.21.4",
        "1.21.5",
        "1.21.6"
    )

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
        maven(url = "https://repo.inventivetalent.org/repository/maven-snapshots/")
        maven(url = "https://repo.extendedclip.com/releases/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.5-R0.1-SNAPSHOT")

    implementation(project(":plugins:fancynpcs:fn-api"))
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
    implementation(project(":plugins:fancynpcs:implementation_1_19_4", configuration = "reobf"))

    rootProject.subprojects
        .filter { it.path.startsWith(":libraries:packets:implementations") }
        .forEach { implementation(project(it.path)) }
    implementation(project(":libraries:packets"))
    implementation(project(":libraries:packets:packets-api"))
    implementation(project(":libraries:common"))
    implementation(project(":libraries:jdb"))
    implementation(project(":libraries:plugin-tests"))
    compileOnly("org.lushplugins:ChatColorHandler:5.1.6")
    implementation("de.oliver.FancyAnalytics:api:0.1.6")
    implementation("de.oliver.FancyAnalytics:logger:0.0.6")
    implementation("org.incendo:cloud-core:2.1.0-SNAPSHOT")
    implementation("org.incendo:cloud-paper:2.0.0-SNAPSHOT")
    implementation("org.incendo:cloud-annotations:2.1.0-SNAPSHOT")
    annotationProcessor("org.incendo:cloud-annotations:2.1.0-SNAPSHOT")
    implementation("org.mineskin:java-client-jsoup:3.0.3-SNAPSHOT")

    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.intellectualsites.plotsquared:plotsquared-core:7.5.2")
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
        minecraftVersion("1.21.5")

        downloadPlugins {
            hangar("ViaVersion", "5.3.2")
            hangar("ViaBackwards", "5.3.2")
            hangar("PlaceholderAPI", "2.11.6")
//            modrinth("multiverse-core", "4.3.11")
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
    dependsOn(":plugins:fancynpcs:shadowJar")
}

tasks.modrinth {
    dependsOn(":plugins:fancynpcs:shadowJar")
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

hangarPublish {
    publications.register("plugin") {
        version = getFNVersion()
        id = "FancyNpcs"
        channel = "Alpha"

        apiKey.set(System.getenv("HANGAR_PUBLISH_API_TOKEN"))

        platforms {
            paper {
                jar = tasks.shadowJar.flatMap { it.archiveFile }
                platformVersions.set(supportedVersions)
            }
        }

        changelog = gitCommitMessage.get()
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_PUBLISH_API_TOKEN"))
    projectId.set("fancynpcs")
    versionNumber.set(getFNVersion())
    versionType.set("alpha")
    uploadFile.set(file("build/libs/${project.name}-${getFNVersion()}.jar"))
    gameVersions.addAll(supportedVersions)
    loaders.add("paper")
    loaders.add("folia")
    changelog.set(gitCommitMessage.get())
}