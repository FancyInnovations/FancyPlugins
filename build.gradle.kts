plugins {
    id("com.gradleup.shadow") version "9.0.0-beta17" apply false
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17" apply false
    id("xyz.jpenilla.run-paper") version "2.3.1" apply false
    id("de.eldoria.plugin-yml.paper") version "0.7.1" apply false
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
