# Strata

Strata is a tool to download and view the Minecraft server source code.
You can also apply patches to the source code and try to compile it.

This tool is inspired by [PaperMC's](https://github.com/PaperMC) build tools. 
It's not recommended to actually use Strata, it's more of a learning and experimental project.

## Setup

To set up a working environment, where the Minecraft server source is decompiled and patched, you need to follow these steps:

1. Run the `runStrata` run task in IntelliJ
2. Go to scripts directory: `cd tools/strata/scripts`
3. Apply file patches: `./applyFilePatches.sh`
4. Apply feature patches: `./applyFeaturePatches.sh`

You can now view the decompiled Minecraft source code in the `tools/strata/minecraft-source/src/main/java` directory.

To compile the source code, run `./gradlew :tools:strata:minecraft-source:shadowJar` in the root directory of the project.
The server jar will be located in `tools/strata/minecraft-source/build/libs` after the compilation is finished.

You can also directly run the server (based on the code) with the `runStrataServer` run task in IntelliJ. 
This will run the server with the applied patches and local changes (if any) without the need to compile the source code.

## Patch system

### Modify file patches

1. Have current file patches applied
2. Make changes to the source code
3. Commit changes (`git commit --amend --no-edit`) in the `tools/strata/minecraft-source/src/main/java` directory
4. Run `./rebuildFilePatches.sh` to rebuild the file patches with your changes

### Add new feature patch

1. Have all file and feature patches applied
2. Make changes to the source code
3. Commit changes (`git add .` and `git commit -m "My new feature"`) in the `tools/strata/minecraft-source/src/main/java` directory
4. Run `./rebuildFeaturePatches.sh` to rebuild the feature patches (including the new one)

### Modify feature patch

1. Have all file patches applied
2. Temporarily move all features patches after the patch you want to modify to a different directory (e.g. `unapplied-features`)
3. Apply all feature patches (your patch will be the last one to be applied)
4. Make changes to the source code
5. Commit changes (`git commit --amend --no-edit`) in the `tools/strata/minecraft-source/src/main/java` directory
6. Run `./rebuildFeaturePatches.sh` to rebuild the feature patch with your changes
7. Move back the feature patches you moved in step 1 to the original directory and apply them again
8. Check if the patches are applied correctly and if there are no conflicts