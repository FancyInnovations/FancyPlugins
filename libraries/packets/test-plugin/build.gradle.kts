plugins {
    id("java-library")
    id("maven-publish")

    id("xyz.jpenilla.run-paper")
    id("com.gradleup.shadow")
    id("de.eldoria.plugin-yml.paper")
}

runPaper.folia.registerTask()

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")

    implementation(project(":libraries:packets"))
    implementation(project(":libraries:packets:packets-api"))
    implementation(project(":libraries:packets:implementations:26_1_1"))
    implementation("de.oliver.FancyAnalytics:logger:0.0.9")
}

paper {
    name = "FancySitulaTestPlugin"
    main = "de.oliver.fancysitula.FancySitulaPlugin"
    bootstrapper = "de.oliver.fancysitula.loaders.FancySitulaPluginBootstrapper"
    loader = "de.oliver.fancysitula.loaders.FancySitulaPluginLoader"
    foliaSupported = true
    version = "1.0.0"
    description = "Test plugin for FancySitula"
    apiVersion = "1.19"
}

tasks {
    runServer {
        minecraftVersion("1.21.10")
    }

    shadowJar {
        archiveClassifier.set("")
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release = 25
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}