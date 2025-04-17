plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev")
}

val minecraftVersion = "1.21"

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

dependencies {
    paperweight.paperDevBundle("$minecraftVersion-R0.1-SNAPSHOT")
    compileOnly(project(":libraries:packets:packets-api"))

    testImplementation(project(":libraries:packets:packets-api"))
    testImplementation(project(":libraries:packets:implementations:1_20_6"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.12.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.12.2")
    testImplementation("org.junit.platform:junit-platform-console-standalone:1.12.2")
}

tasks {
    test {
        useJUnitPlatform()
    }
}