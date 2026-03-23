package com.fancyinnovations.strata.mojang;

public record PistonVersionManifestV2(
        Latest latest,
        Version[] versions
) {

    public Version getVersion(String id) {
        for (Version version : versions) {
            if (version.id().equals(id)) {
                return version;
            }
        }
        return null;
    }

    public record Latest(
            String release,
            String snapshot
    ) {

    }

    public record Version(
            String id,
            String type,
            String url,
            String time,
            String releaseTime,
            String sha1,
            int complianceLevel
    ) {

    }

}
