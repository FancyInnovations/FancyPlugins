plugins {
    id("java-library")
    id("maven-publish")
    id("com.gradleup.shadow")
}

val minecraftVersion = "1.21.6"

dependencies {
    compileOnly("io.papermc.paper:paper-api:$minecraftVersion-R0.1-SNAPSHOT")

    compileOnly(project(":libraries:common"))
    compileOnly("de.oliver.FancyAnalytics:logger:0.0.8")

    implementation("org.lushplugins:ChatColorHandler:6.0.3")

    implementation("org.jetbrains:annotations:24.0.0")
}

tasks {
    shadowJar {
        relocate("org.lushplugins.chatcolorhandler", "com.fancyinnovations.fancydialogs.libs.chatcolorhandler")

        archiveClassifier.set("")
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
                groupId = "de.oliver"
                artifactId = "FancyDialogs"
                version = getFDVersion()
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

        options.release.set(21)
    }
}

fun getFDVersion(): String {
    return file("../VERSION").readText()
}