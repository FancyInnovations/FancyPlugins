package de.oliver.quicke2e.steps.copyFile;

import de.oliver.quicke2e.config.Context;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.regex.Pattern;

public class CopyFileService {

    public void copyFile(Context context, String sourceFolder, String source, String destination) {
        String realSource = source;

        if (source.contains("*")) {
            Pattern pattern = Pattern.compile(source);
            try {
                Optional<Path> first = Files.walk(Path.of(sourceFolder))
                        .filter(Files::isRegularFile)
                        .filter(path -> pattern.matcher(path.getFileName().toString()).matches())
                        .findFirst();
                if (first.isPresent()) {
                    realSource = first.get().toString();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Path target = context.serverEnvPath().resolve(destination);

        try {
            Files.copy(Path.of(realSource), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createDirectory(Context context, String directory) {
        Path path = context.serverEnvPath().resolve(directory);
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
