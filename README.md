# 

<div align="center">

![](docs/src/static/logos-and-banners/fancyinnovations-banner.png)

[![Modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/modrinth_vector.svg)](https://modrinth.com/user/Oliver)
[![Hangar](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/hangar_vector.svg)](https://hangar.papermc.io/Oliver)
[![Unsupported spigot](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/unsupported/spigot_vector.svg)]()

[![Website](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/documentation/website_vector.svg)](https://fancyinnovations.com)
[![Documentation](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/documentation/ghpages_vector.svg)](https://fancyinnovations.com/docs/minecraft-plugins)
[![discord-plural](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/social/discord-plural_46h.png)](https://discord.gg/ZUgYCEJUEx)


[![CodeFactor](https://www.codefactor.io/repository/github/fancyinnovations/fancyplugins/badge)](https://www.codefactor.io/repository/github/fancyinnovations/fancyplugins)
[![Lines of Code](https://tokei.rs/b1/github/fancyinnovations/fancyplugins?category=code)](https://github.com/fancyinnovations/fancyplugins)

</div>

## FancyPlugins

This is a [monorepo](docs/src/development-guidelines/monorepo.md) for all plugins, libraries, and tools of FancyInnovations.

**Plugins:**
- FancyNpcs: create and manage fancy looking npcs
- FancyHolograms: create fancy looking holograms
- FancyDialogs: create and manage fancy looking dialogs which were added in 1.21.6 (work in progress)
- FancyVisuals: plugin to customize all visual components of the game (work in progress)

**Libraries:**
- Common: common classes and utilities
- JDB: json database library
- Plugin Tests: testing library for plugins
- Packets: packet handling library (also called FancySitula)

**Tools:**
- Quick E2E: generate a quick end-to-end environment for testing

## Usage

The monorepo uses Gradle as a build system. See [monorepo.md](docs/src/development-guidelines/monorepo.md) for more information.

To see specific usage for each package, see the README.md in the respective package directory.

## Exilon fork: building FancyNpcs

This section describes the build and support policy for the Exilon fork. It is not an upstream FancyInnovations support statement.

### Requirements

- JDK 25 available through `JAVA_HOME`.
- Git.
- Network access to the Paper and FancyInnovations Maven repositories on the first build.
- The checked-in Gradle wrapper. A separately installed Gradle version is neither required nor recommended.

Run all commands from the repository root. On Windows, verify the selected JDK and compile every FancyNpcs implementation supported by Exilon with:

```powershell
java -version
.\gradlew.bat --configure-on-demand `
  :plugins:fancynpcs-v2:implementation_26_1_2:compileJava `
  :plugins:fancynpcs-v2:implementation_26_2:compileJava `
  :plugins:fancynpcs-v2:implementation_26_3:compileJava
```

On Linux or macOS:

```bash
java -version
./gradlew --configure-on-demand \
  :plugins:fancynpcs-v2:implementation_26_1_2:compileJava \
  :plugins:fancynpcs-v2:implementation_26_2:compileJava \
  :plugins:fancynpcs-v2:implementation_26_3:compileJava
```

These three tasks are the Exilon compatibility gate. All of them must pass before merging or releasing a FancyNpcs change.

### Supported versions

Exilon only supports the `26.1.2` implementation and newer implementations. The `implementation_1_21_5`, `implementation_1_21_6`, `implementation_1_21_9`, and `implementation_1_21_11` modules are retained from upstream for source synchronization and upstream compatibility, but they are outside Exilon's support matrix.

A failure confined to an `implementation_1_21_*` module does not fail Exilon validation and does not block an Exilon change when every supported task above succeeds. Do not weaken, patch around, or change supported `26.1.2+` code merely to make an unsupported legacy module compile.

### Building the distributable JAR

To run the upstream-compatible monorepo packaging task:

```powershell
.\gradlew.bat :plugins:fancynpcs-v2:shadowJar
```

The resulting JAR is written under `plugins/fancynpcs-v2/build/libs/`.

The Exilon `shadowJar` graph packages only FancyNpcs and packet implementations for `26.1.2+`. Legacy `1.21.x` projects remain available for upstream synchronization, but this task does not compile or include them. Use the targeted `:plugins:fancynpcs-v2:shadowJar` task instead of a repository-wide `build` when producing an Exilon artifact.

## Contributing

You can contribute to this repository by reporting bugs, suggesting features, or contributing code. 
Please read the [contributing guidelines](docs/src/development-guidelines/contributing.md) for more information.

---

[All contributors of this repository:](https://github.com/FancyInnovations/FancyPlugins/graphs/contributors)

<a href = "https://github.com/FancyInnovations/FancyPlugins/graphs/contributors">
  <img src = "https://contrib.rocks/image?repo=FancyInnovations/FancyPlugins" alt="All contributors of this repository"/>
</a>

**[All contributors of the old FancyNpcs repository:](https://github.com/FancyMcPlugins/FancyNpcs/graphs/contributors)**

<a href = "https://github.com/FancyMcPlugins/FancyNpcs/graphs/contributors">
  <img src = "https://contrib.rocks/image?repo=FancyMcPlugins/FancyNpcs" alt="All contributors of the old FancyNpcs repository"/>
</a>

**[All contributors of the old FancyHolograms repository:](https://github.com/FancyMcPlugins/FancyHolograms/graphs/contributors)**

<a href = "https://github.com/FancyMcPlugins/FancyHolograms/graphs/contributors">
  <img src = "https://contrib.rocks/image?repo=FancyMcPlugins/FancyHolograms" alt="All contributors of the old FancyHolograms repository"/>
</a>
