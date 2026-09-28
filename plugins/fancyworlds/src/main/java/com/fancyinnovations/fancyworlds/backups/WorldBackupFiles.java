package com.fancyinnovations.fancyworlds.backups;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

final class WorldBackupFiles {

    private WorldBackupFiles() {
    }

    static Path replace(Path prepared, Path destination) throws IOException {
        Path previous = destination.resolveSibling(".fw-old-" + UUID.randomUUID());

        move(destination, previous);

        try {
            move(prepared, destination);
        } catch (IOException ex) {
            try {
                move(previous, destination);
            } catch (IOException rollbackFailure) {
                ex.addSuppressed(rollbackFailure);
            }
            throw ex;
        }
        
        return previous;
    }

    static void move(Path from, Path to) throws IOException {
        try {
            Files.move(from, to, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ex) {
            Files.move(from, to);
        }
    }

    static void prepareClone(Path directory) throws IOException {
        Files.deleteIfExists(directory.resolve("uid.dat"));
        // Paper 26.2+ keeps the world UUID in saved data instead of uid.dat.
        Files.deleteIfExists(directory.resolve("data/paper/metadata.dat"));
        Files.deleteIfExists(directory.resolve("session.lock"));
    }
}
