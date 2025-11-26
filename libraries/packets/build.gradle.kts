plugins {
    id("java-library")
    id("maven-publish")
    id("com.gradleup.shadow")
}

allprojects {
    group = "de.oliver"
    version = findProperty("fancysitulaVersion") as String
    description = "Simple, lightweight and fast library for minecraft internals"

    repositories {
        mavenLocal()
        mavenCentral()
        maven(url = "https://repo.papermc.io/repository/maven-public/")
        maven(url = "https://repo.fancyinnovations.com/releases")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT")

    implementation(project(":libraries:packets:packets-api"))
    implementation(project(":libraries:packets:implementations:1_21_11"))
    implementation(project(":libraries:packets:implementations:1_21_9"))
    implementation(project(":libraries:packets:implementations:1_21_6"))
    implementation(project(":libraries:packets:implementations:1_21_5"))
    implementation(project(":libraries:packets:implementations:1_21_4"))
    implementation(project(":libraries:packets:implementations:1_21_3"))
    implementation(project(":libraries:packets:implementations:1_20_6"))
    implementation("de.oliver.FancyAnalytics:logger:0.0.8")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        configurations = listOf(project.configurations["runtimeClasspath"])
        dependencies {
            include(dependency("de.oliver:.*"))
        }
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
            create<MavenPublication>("shadow") {
                groupId = "de.oliver"
                version = findProperty("fancysitulaVersion") as String
                artifact(shadowJar)
            }
        }
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release = 21
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}