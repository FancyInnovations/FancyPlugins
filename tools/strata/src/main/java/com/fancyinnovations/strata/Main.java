package com.fancyinnovations.strata;

import com.fancyinnovations.strata.mojang.PistonVersionDetails;
import com.fancyinnovations.strata.workspace.WorkspaceService;

public class Main {

    /**
     * For minecraft-source
     */
    public static void main2(String[] args) {
        String cacheDir = "tools/strata/strata-cache";
        Strata strata = new Strata(cacheDir);
        strata.init();

        // Get the latest snapshot version and download it
        PistonVersionDetails latest = strata.getMojangService().getLatestSnapshot();
        strata.getMojangService().downloadServerBundle(latest);
        strata.getExtractorService().extractServerBundle(latest.id());

        // Decompile
        strata.getDecompilerService().decompile(
                strata.getExtractorService().getServerJarPath(latest.id()),
                latest.id()
        );

        // Setup git repo
        String gitDir = "tools/strata/minecraft-source/src/main/java";
        strata.getWorkspaceService().initGitDirectory(gitDir);
        sleep(1000);

        // Add decompiled sources and resources
        strata.getWorkspaceService().copyDecompiledSources(latest.id(), gitDir);
        strata.getWorkspaceService().copyDataAndAssets(latest.id(), "tools/strata/minecraft-source/src/main/resources");
        strata.getWorkspaceService().gitCommit(gitDir, "Add decompiled sources");
        strata.getWorkspaceService().gitTag(gitDir, WorkspaceService.DECOMPILED_SOURCES_TAG);
        sleep(1000);

        // Apply patches
        String patchesDir = "tools/strata/minecraft-source/patches";
        strata.getPatcherService().applyFilePatches(cacheDir + "/decompiled/" + latest.id(), gitDir, patchesDir + "/files", patchesDir + "/rejected-files");
        sleep(1000);
        strata.getWorkspaceService().gitCommit(gitDir, "Apply file patches");
        strata.getWorkspaceService().gitTag(gitDir, WorkspaceService.FILE_PATCHES_TAG);
        sleep(1000);

        strata.getLogger().info("Done with setting up workspace for version " + latest.id());

        // Rebuild patches
        // TODO refactor to different task
        // strata.getPatcherService().rebuildFilePatches(cacheDir+"/decompiled/"+latest.id(), gitDir, patchesDir+"/files");
    }

    /**
     * For minecraft-diff
     */
    public static void main(String[] args) {
        Strata strata = new Strata("tools/strata/strata-cache");
        strata.init();

        String version = "26.1-rc-2";

        PistonVersionDetails ver = strata.getMojangService().getVersion(version);
        strata.getMojangService().downloadServerBundle(ver);
        strata.getExtractorService().extractServerBundle(ver.id());
        strata.getDecompilerService().decompile(
                strata.getExtractorService().getServerJarPath(ver.id()),
                ver.id()
        );

        String gitDir = "tools/strata/minecraft-diff/src";
        strata.getWorkspaceService().copyDecompiledSources(ver.id(), gitDir);

        sleep(1000);

        strata.getWorkspaceService().gitCommit(gitDir, "Update to " + version);
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }

}
