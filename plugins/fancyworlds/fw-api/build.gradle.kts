plugins {
    id("java-library")
    id("maven-publish")
    id("com.gradleup.shadow")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.60-stable")

    compileOnly(project(":libraries:common"))
    compileOnly("de.oliver.FancyAnalytics:logger:0.0.8")

    implementation("org.lushplugins.chatcolorhandler:paper:8.1.0")

    implementation("org.jetbrains:annotations:26.1.0")
}

tasks {
    shadowJar {
        relocate("org.lushplugins.chatcolorhandler", "com.fancyinnovations.fancyworlds.libs.chatcolorhandler")

        archiveClassifier.set("")
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
                artifactId = "FancyWorlds"
                version = getFWVersion()
                from(project.components["java"])
            }
        }
    }

    java {
        withSourcesJar()
        withJavadocJar()
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()

        options.release.set(25)
    }
}

fun getFWVersion(): String {
    return file("../VERSION").readText()
}