package com.fancyinnovations.strata.mojang;

public record PistonVersionDetails(
        String id,
        String releaseTime,
        String type,
        String mainClass,
        Downloads downloads,
        JavaVersion javaVersion,
        Library[] libraries
) {

    public record Downloads(
            Download client, // used for clients, contains the actual jar file of the client
            Download server, // used for servers, contains the actual jar file of the server
            Download artifact // used for libraries, contains the actual jar file of the library
    ) {

    }

    public record Download(
            String sha1,
            long size,
            String url
    ) {

    }

    public record JavaVersion(
            String component,
            String version
    ) {

    }

    public record Library(
            String name,
            Downloads downloads
    ) {

    }

}
