rootProject.name = "minecraft-plugins"

include(":plugins:fancynpcs:")
include(":plugins:fancynpcs:fn-api")
include(":plugins:fancynpcs:implementation_1_21_11")
include(":plugins:fancynpcs:implementation_1_21_9")
include(":plugins:fancynpcs:implementation_1_21_6")
include(":plugins:fancynpcs:implementation_1_21_5")
include(":plugins:fancynpcs:implementation_1_21_4")
include(":plugins:fancynpcs:implementation_1_21_3")
include(":plugins:fancynpcs:implementation_1_21_1")
include(":plugins:fancynpcs:implementation_1_20_6")
include(":plugins:fancynpcs:implementation_1_20_4")
include(":plugins:fancynpcs:implementation_1_20_2")
include(":plugins:fancynpcs:implementation_1_20_1")
include(":plugins:fancynpcs:implementation_1_20")
include(":plugins:fancynpcs:implementation_1_19_4")

include(":plugins:fancyholograms-v2")
include(":plugins:fancyholograms-v2:api")
include(":plugins:fancyholograms-v2:implementation_1_20_4")
include(":plugins:fancyholograms-v2:implementation_1_20_2")
include(":plugins:fancyholograms-v2:implementation_1_20_1")
include(":plugins:fancyholograms-v2:implementation_1_19_4")

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
include(":libraries:packets:implementations:1_20_6")
include(":libraries:packets:implementations:1_21")
include(":libraries:packets:implementations:1_21_1")
include(":libraries:packets:implementations:1_21_3")
include(":libraries:packets:implementations:1_21_4")
include(":libraries:packets:implementations:1_21_5")
include(":libraries:packets:implementations:1_21_6")
include(":libraries:packets:implementations:1_21_9")
include(":libraries:packets:implementations:1_21_11")


include(":tools:deployment")
include(":tools:quick-e2e")

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}