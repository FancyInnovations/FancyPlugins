plugins {
    id("java-library")
    id("maven-publish")
    id("com.gradleup.shadow")
}

val minecraftVersion = "1.20"

dependencies {
    compileOnly("io.papermc.paper:paper-api:$minecraftVersion-R0.1-SNAPSHOT")

    compileOnly(project(":libraries:common"))
    compileOnly(project(":libraries:config"))
    compileOnly("de.oliver.FancyAnalytics:logger:0.0.8")

    implementation("org.lushplugins:ChatColorHandler:6.0.4")
}

tasks {
    shadowJar {
        archiveClassifier.set("")

        relocate("org.lushplugins.chatcolorhandler", "com.fancyinnovations.fancynpcs.libs.chatcolorhandler")
    }

    publishing {
        repositories {
            maven {
                name = "fancyspacesReleases"
                url = uri("https://maven.fancyspaces.net/fancyinnovations/releases")

                credentials(HttpHeaderCredentials::class) {
                    name = "Authorization"
                    value = providers
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
                    value = providers
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
                groupId = "com.fancyinnovations"
                artifactId = "FancyNpcs"
                version = getFNVersion()
                from(project.components["java"])
            }
        }
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release = 21
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    withSourcesJar()
    withJavadocJar()
}

fun getFNVersion(): String {
    return file("../VERSION").readText()
}