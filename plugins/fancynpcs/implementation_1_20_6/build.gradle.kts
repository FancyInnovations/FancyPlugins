plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev")
}

val minecraftVersion = "1.20.6"

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

dependencies {
    paperweight.paperDevBundle("$minecraftVersion-R0.1-SNAPSHOT")

    compileOnly(project(":plugins:fancynpcs:fn-api"))
    compileOnly(project(":libraries:common"))
    compileOnly("org.lushplugins:ChatColorHandler:6.0.3")
}


tasks {
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release = 21
    }
}