package com.fancyinnovations.strata.patcher;

import com.fancyinnovations.strata.Strata;
import de.oliver.fancyanalytics.logger.properties.StringProperty;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import io.codechicken.diffpatch.cli.CliOperation;
import io.codechicken.diffpatch.cli.DiffOperation;
import io.codechicken.diffpatch.cli.PatchOperation;
import io.codechicken.diffpatch.match.FuzzyLineMatcher;
import io.codechicken.diffpatch.util.Input;
import io.codechicken.diffpatch.util.LogLevel;
import io.codechicken.diffpatch.util.Output;
import io.codechicken.diffpatch.util.PatchMode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class PatcherService {

    private final Strata strata;

    public PatcherService(Strata strata) {
        this.strata = strata;
    }

    public void rebuildFilePatches(String originalSourcePath, String patchedSourcePath, String patchesPath) {
        // Delete existing patches
        try {
            Path patchesDir = Path.of(patchesPath);
            if (patchesDir.toFile().exists()) {
                Files.walk(patchesDir)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        } catch (IOException e) {
            strata.getLogger().error(
                    "Failed to clear existing patches",
                    ThrowableProperty.of(e),
                    StringProperty.of("patchesPath", patchesPath)
            );
            return;
        }

        try {
            CliOperation.Result<DiffOperation.DiffSummary> result = DiffOperation.builder()
                    .baseInput(Input.MultiInput.folder(Path.of(originalSourcePath)))
                    .changedInput(Input.MultiInput.folder(Path.of(patchedSourcePath)))
                    .patchesOutput(Output.MultiOutput.folder(Path.of(patchesPath)))
                    .autoHeader(true)
                    .ignorePrefix(".git")
                    .ignorePrefix("META-INF")
                    .ignorePrefix("data/")
                    .ignorePrefix("assets/")
                    .ignorePrefix("version.json")
                    .ignorePrefix("flightrecorder-config.jfc")
                    .context(3)
                    .logTo(s -> strata.getLogger().error(s))
                    .level(LogLevel.ERROR)
                    .summary(false)
                    .build()
                    .operate();

            strata.getLogger().info(
                    "Finished rebuilding file patches",
                    StringProperty.of("originalSourcePath", originalSourcePath),
                    StringProperty.of("patchedSourcePath", patchedSourcePath),
                    StringProperty.of("patchesPath", patchesPath)
            );
        } catch (IOException e) {
            strata.getLogger().error(
                    "Failed to rebuild file patches",
                    ThrowableProperty.of(e),
                    StringProperty.of("originalSourcePath", originalSourcePath),
                    StringProperty.of("patchedSourcePath", patchedSourcePath),
                    StringProperty.of("patchesPath", patchesPath)
            );
        }
    }

    public void applyFilePatches(String originalSourcePath, String patchedSourcePath, String patchesPath, String rejectsPath) {
        Path cache = strata.getCacheDir().toPath().resolve("source-with-patches");

        // Clear cache directory
        if (cache.toFile().exists()) {
            try {
                Files.walk(cache)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                strata.getLogger().error(
                        "Failed to clear patch application cache",
                        ThrowableProperty.of(e),
                        StringProperty.of("cachePath", cache.toString())
                );
                return;
            }
        }

        sleep(1000);

        // Apply patches to original source, output to cache
        try {
            CliOperation.Result<PatchOperation.PatchesSummary> result = PatchOperation.builder()
                    .baseInput(Input.MultiInput.folder(Path.of(originalSourcePath)))
                    .patchesInput(Input.MultiInput.folder(Path.of(patchesPath)))
                    .patchedOutput(Output.MultiOutput.folder(cache))
                    .logTo(s -> strata.getLogger().error(s))
                    .level(LogLevel.ERROR)
                    .rejectsOutput(Output.MultiOutput.folder(Path.of(rejectsPath)))
                    .mode(PatchMode.OFFSET)
                    .minFuzz(FuzzyLineMatcher.DEFAULT_MIN_MATCH_SCORE)
                    .ignorePrefix(".git")
                    .ignorePrefix("META-INF")
                    .ignorePrefix("data/")
                    .ignorePrefix("assets/")
                    .ignorePrefix("version.json")
                    .ignorePrefix("flightrecorder-config.jfc")
                    .build()
                    .operate();

            strata.getLogger().info(
                    "Finished applying file patches",
                    StringProperty.of("originalSourcePath", originalSourcePath),
                    StringProperty.of("patchedSourcePath", patchedSourcePath),
                    StringProperty.of("patchesPath", patchesPath)
            );
        } catch (IOException e) {
            strata.getLogger().error(
                    "Failed to apply file patches",
                    ThrowableProperty.of(e),
                    StringProperty.of("originalSourcePath", originalSourcePath),
                    StringProperty.of("patchedSourcePath", patchedSourcePath),
                    StringProperty.of("patchesPath", patchesPath)
            );
        }

        sleep(1000);

        // clear patchedSourcePath
        try {
            Path patchedSourceDir = Path.of(patchedSourcePath);
            if (patchedSourceDir.toFile().exists()) {
                Files.walk(patchedSourceDir)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .filter(file -> !file.toPath().toString().contains(".git")) // don't delete .git directory
                        .forEach(File::delete);
            }
        } catch (IOException e) {
            strata.getLogger().error(
                    "Failed to clear patched source directory",
                    ThrowableProperty.of(e),
                    StringProperty.of("patchedSourcePath", patchedSourcePath)
            );
            return;
        }

        sleep(1000);

        // copy patched files to patchedSourcePath
        try {
            Files.walk(cache)
                    .filter(Files::isRegularFile)
                    .forEach(patchedFile -> {
                        Path relativePath = cache.relativize(patchedFile);
                        Path targetPath = Path.of(patchedSourcePath).resolve(relativePath);
                        try {
                            Files.createDirectories(targetPath.getParent());
                            Files.copy(patchedFile, targetPath);
                        } catch (IOException e) {
                            strata.getLogger().error(
                                    "Failed to copy patched file",
                                    ThrowableProperty.of(e),
                                    StringProperty.of("patchedFile", patchedFile.toString()),
                                    StringProperty.of("targetPath", targetPath.toString())
                            );
                        }
                    });
        } catch (IOException e) {
            strata.getLogger().error(
                    "Failed to copy patched files",
                    ThrowableProperty.of(e),
                    StringProperty.of("cachePath", cache.toString()),
                    StringProperty.of("patchedSourcePath", patchedSourcePath)
            );
        }
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }

}
