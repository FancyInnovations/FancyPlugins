plugins {
    id("java-library")
//    id("io.papermc.paperweight.userdev")
}

dependencies {
//    paperweight.paperDevBundle("26.1-R0.1-SNAPSHOT")
    compileOnly(fileTree("../../../../libraries/paper-server") { include("**/*.jar") })
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")

    compileOnly(project(":libraries:packets:packets-api"))

    testImplementation(project(":libraries:packets"))
    testImplementation(project(":libraries:packets:packets-api"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:6.0.3")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:6.0.3")
    testImplementation("org.junit.platform:junit-platform-console-standalone:6.0.3")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks {
    test {
        useJUnitPlatform()
    }
}