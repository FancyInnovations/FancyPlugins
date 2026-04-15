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
        maven(url = "https://repo.inventivetalent.org/repository/maven-snapshots/") // for MineSkin
        maven(url = "https://repo.extendedclip.com/releases/") // for PlaceholderAPI
        maven(url = "https://maven.enginehub.org/repo/") // for WorldEdit
        maven(url = "https://maven.citizensnpcs.co/repo") // for Citizens
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")

    implementation(project(":plugins:fancynpcs-v2:fn-v2-api"))
    implementation(project(":plugins:fancynpcs-v2:implementation_26_2"))
    implementation(project(":plugins:fancynpcs-v2:implementation_26_1_1"))
    implementation(project(":plugins:fancynpcs-v2:implementation_1_21_11"))
    implementation(project(":plugins:fancynpcs-v2:implementation_1_21_9"))
    implementation(project(":plugins:fancynpcs-v2:implementation_1_21_6"))
    implementation(project(":plugins:fancynpcs-v2:implementation_1_21_5"))
    implementation(project(":plugins:fancynpcs-v2:implementation_1_21_4"))

    rootProject.subprojects
        .filter { it.path.startsWith(":libraries:packets:implementations") }
        .forEach { implementation(project(it.path)) }
    implementation(project(":libraries:packets"))
    implementation(project(":libraries:packets:packets-api"))
    implementation(project(":libraries:common"))
    implementation(project(":libraries:jdb"))
    implementation(project(":libraries:plugin-tests"))
    implementation(project(":libraries:config"))
    compileOnly("org.lushplugins.chatcolorhandler:paper:8.1.0")
    implementation("de.oliver.FancyAnalytics:java-sdk:0.0.6")
    implementation("de.oliver.FancyAnalytics:mc-api:0.1.13")
    implementation("de.oliver.FancyAnalytics:logger:0.0.8")
    implementation("org.incendo:cloud-core:2.0.0")
    implementation("org.incendo:cloud-paper:2.0.0-SNAPSHOT")
    implementation("org.incendo:cloud-annotations:2.0.0")
    annotationProcessor("org.incendo:cloud-annotations:2.0.0")
    implementation("org.mineskin:java-client-jsoup:3.0.3-SNAPSHOT")

    compileOnly("me.clip:placeholderapi:2.12.2")
    compileOnly("com.intellectualsites.plotsquared:plotsquared-core:7.5.12")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.4.0")
    compileOnly("net.citizensnpcs:citizens-main:2.0.42-SNAPSHOT") {
        exclude(group = "*", module = "*")
    }
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
        register("Citizens") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
}

tasks {
    runServer {
        minecraftVersion("26.1.2")
//        minecraftVersion("26.2-snapshot-1")
//        serverJar(file("/Users/oliver/Workspace/paper/paper-server/build/libs/paper-bundler-26.2-snapshot-1.build.2-alpha.jar"))


        downloadPlugins {
//            url("https://fancyspaces.net/api/v1/spaces/s1gGcHj5/versions/3qG7szVk/files/FancyWorlds-0.0.2.jar")
//            modrinth("FancyHolograms", "2.9.1")
//            modrinth("FancyDialogs", "1.1.2")
//            modrinth("FancyEconomy", "1.0.3+6")

//            modrinth("kite", "1.4.0")
//            hangar("PlaceholderAPI", "2.11.6")
//            hangar("ViaVersion", "5.8.1")
//            hangar("ViaBackwards", "5.8.1")
//            url("https://ci.citizensnpcs.co/job/citizens2/4138/artifact/dist/target/Citizens-2.0.41-b4138.jar")
        }
    }

    shadowJar {
        relocate("org.incendo", "de.oliver")
        relocate("org.lushplugins.chatcolorhandler", "de.oliver.fancynpcs.libs.chatcolorhandler")
        archiveClassifier.set("")
        archiveBaseName.set("FancyNpcs")
        dependsOn(":plugins:fancynpcs-v2:fn-v2-api:shadowJar")
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
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
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
