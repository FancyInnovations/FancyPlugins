import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("java-library")
    id("maven-publish")
    id("xyz.jpenilla.run-paper")
    id("com.gradleup.shadow")
    id("de.eldoria.plugin-yml.paper")
}

group = "de.oliver"
description = "Economy plugin"
version = getFEVersion()

repositories {
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://repo.alessiodp.com/releases/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://nexus.hc.to/content/repositories/pub_releases")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")

    implementation(project(":libraries:common"))
    implementation(project(":libraries:jdb"))
    implementation(project(":libraries:config"))
    implementation("de.oliver.FancyAnalytics:java-sdk:0.0.6")
    implementation("de.oliver.FancyAnalytics:mc-api:0.1.13")
    implementation("de.oliver.FancyAnalytics:logger:0.0.10")

    compileOnly("net.milkbowl.vault:VaultAPI:1.7")

    compileOnly("me.clip:placeholderapi:2.11.5")

    val commandapiVersion = "11.1.0"
    implementation("dev.jorel:commandapi-paper-shade:$commandapiVersion")
    compileOnly("dev.jorel:commandapi-paper-annotations:$commandapiVersion")
    annotationProcessor("dev.jorel:commandapi-paper-annotations:$commandapiVersion")
}

paper {
    name = "FancyEconomy"
    main = "de.oliver.fancyeconomy.FancyEconomy"
    bootstrapper = "de.oliver.fancyeconomy.FancyEconomyBootstrapper"
    loader = "de.oliver.fancyeconomy.FancyEconomyLoader"
    foliaSupported = true
    version = getFEVersion()
    description = "Simple and lightweight economy plugin with support for multiple currencies"
    apiVersion = "1.19"
    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD
    serverDependencies {
        register("Vault") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("PlaceholderAPI") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
    hasOpenClassloader = true
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks {
    runServer {
        minecraftVersion("26.1.2")
    }

    shadowJar {
        archiveClassifier.set("")
        relocate("dev.jorel.commandapi", "de.oliver.fancyeconomy.commandapi")
    }

    publishing {
        repositories {
            maven {
                name = "fancyspacesReleases"
                url = uri("https://maven.fancyspaces.net/fancyinnovations/releases")

                credentials(HttpHeaderCredentials::class) {
                    name = "Authorization"
                    value = "ApiKey " + providers
                        .gradleProperty("fancyspacesApiKey")
                        .orElse(
                            providers
                                .environmentVariable("FANCYSPACES_API_KEY")
                                .orElse("")
                        )
                        .get()
                }

                authentication {
                    create<HttpHeaderAuthentication>("header")
                }
            }

            maven {
                name = "fancyspacesSnapshots"
                url = uri("https://maven.fancyspaces.net/fancyinnovations/snapshots")

                credentials(HttpHeaderCredentials::class) {
                    name = "Authorization"
                    value = "ApiKey " + providers
                        .gradleProperty("fancyspacesApiKey")
                        .orElse(
                            providers
                                .environmentVariable("FANCYSPACES_API_KEY")
                                .orElse("")
                        )
                        .get()
                }

                authentication {
                    create<HttpHeaderAuthentication>("header")
                }
            }
        }
        publications {
            create<MavenPublication>("maven") {
                groupId = "de.oliver"
                artifactId = "fancyeconomy"
                version = getFEVersion()
                from(project.components["java"])
            }
        }
    }

    build {
        dependsOn(shadowJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(25)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything

        val props = mapOf(
            "description" to project.description,
            "version" to getFEVersion(),
            "commit_hash" to "",
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

fun getFEVersion(): String {
    return file("VERSION").readText()
}
