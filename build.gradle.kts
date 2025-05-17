plugins {
    id("com.gradleup.shadow") version "9.0.0-beta13" apply false
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.16" apply false
    id("xyz.jpenilla.run-paper") version "2.3.1" apply false
    id("net.minecrell.plugin-yml.paper") version "0.6.0" apply false
    id("io.papermc.hangar-publish-plugin") version "0.1.3" apply false
    id("com.modrinth.minotaur") version "2.+" apply false
}

allprojects {
    group = "de.oliver"
    description = "Minecraft plugins of FancyInnovations"

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.fancyinnovations.com/releases")
        maven(url = "https://jitpack.io")
    }
}
