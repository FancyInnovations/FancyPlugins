plugins {
    id("com.gradleup.shadow") version "9.4.1" apply false
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21" apply false
    id("xyz.jpenilla.run-paper") version "3.0.2" apply false
    id("de.eldoria.plugin-yml.paper") version "0.8.0" apply false
}

allprojects {
    group = "de.oliver"
    description = "Minecraft plugins of FancyInnovations"

    repositories {
        mavenLocal()
        mavenCentral()

        maven(url = "https://maven.fancyspaces.net/fancyinnovations/releases")
        maven(url = "https://maven.fancyspaces.net/fancyinnovations/snapshots")
        maven(url = "https://maven.fancyspaces.net/origami/releases")
        maven(url = "https://repo.fancyinnovations.com/releases")
        maven(url = "https://repo.fancyinnovations.com/snapshots")

        maven(url = "https://repo.lushplugins.org/releases")
        maven(url = "https://repo.papermc.io/repository/maven-public/")
//        maven(url = "https://jitpack.io")
    }
}
