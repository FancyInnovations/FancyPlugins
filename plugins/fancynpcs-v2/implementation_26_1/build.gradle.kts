plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev")
}

dependencies {
    paperweight.paperDevBundle("26.1.1-SNAPSHOT")

    compileOnly(project(":plugins:fancynpcs-v2:fn-v2-api"))
    compileOnly(project(":libraries:common"))
    compileOnly("org.lushplugins.chatcolorhandler:paper:8.1.0")
}


tasks {
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release = 25
    }
}