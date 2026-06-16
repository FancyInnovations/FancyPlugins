plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev")
}

dependencies {
    paperweight.paperDevBundle("26.2.build.1-alpha")

    compileOnly(project(":libraries:packets:packets-api"))

    testImplementation(project(":libraries:packets"))
    testImplementation(project(":libraries:packets:packets-api"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:6.1.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:6.1.0")
    testImplementation("org.junit.platform:junit-platform-console-standalone:6.1.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks {
    test {
        useJUnitPlatform()
    }
}