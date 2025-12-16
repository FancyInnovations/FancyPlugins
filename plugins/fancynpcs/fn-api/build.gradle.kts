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

    implementation("org.lushplugins:ChatColorHandler:6.0.3")
}

tasks {
    shadowJar {
        archiveClassifier.set("")

        relocate("org.lushplugins.chatcolorhandler", "de.oliver.fancynpcs.libs.chatcolorhandler")
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
                artifactId = "FancyNpcs"
                version = getFNVersion()
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
        options.release = 17

    }
}

fun getFNVersion(): String {
    return file("../VERSION").readText()
}