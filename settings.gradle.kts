pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "minecraft-plugins"

include(":plugins:fancynpcs-v2:")
include(":plugins:fancynpcs-v2:fn-v2-api")
include(":plugins:fancynpcs-v2:implementation_1_21_11")
include(":plugins:fancynpcs-v2:implementation_1_21_9")
include(":plugins:fancynpcs-v2:implementation_1_21_6")
include(":plugins:fancynpcs-v2:implementation_1_21_5")
include(":plugins:fancynpcs-v2:implementation_1_21_4")
include(":plugins:fancynpcs-v2:implementation_1_21_3")

include(":plugins:fancynpcs:")
include(":plugins:fancynpcs:fn-api")

include(":plugins:fancyholograms-v2")
include(":plugins:fancyholograms-v2:api")

include(":plugins:fancyholograms")
include(":plugins:fancyholograms:fh-api")

include(":plugins:fancyvisuals")

include(":plugins:fancydialogs")
include(":plugins:fancydialogs:fd-api")

include(":libraries:common")
include(":libraries:jdb")
include(":libraries:config")
include(":libraries:plugin-tests")

include(":libraries:packets")
include(":libraries:packets:packets-api")
include(":libraries:packets:test-plugin")
include(":libraries:packets:implementations:1_21_3")
include(":libraries:packets:implementations:1_21_4")
include(":libraries:packets:implementations:1_21_5")
include(":libraries:packets:implementations:1_21_6")
include(":libraries:packets:implementations:1_21_9")
include(":libraries:packets:implementations:1_21_11")


include(":tools:quick-e2e")
