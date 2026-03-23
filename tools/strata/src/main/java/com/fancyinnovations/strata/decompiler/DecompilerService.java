package com.fancyinnovations.strata.decompiler;

import com.fancyinnovations.strata.Strata;
import de.oliver.fancyanalytics.logger.LogLevel;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import org.jetbrains.java.decompiler.api.Decompiler;
import org.jetbrains.java.decompiler.main.decompiler.DirectoryResultSaver;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;

import java.io.File;
import java.nio.file.Path;

public class DecompilerService {

    private final Strata strata;

    public DecompilerService(Strata strata) {
        this.strata = strata;
    }

    public void decompile(String inputJarPath, String versionID) {
        File inputFile = new File(inputJarPath);
        if (!inputFile.exists()) {
            strata.getLogger().error("Input JAR file does not exist: " + inputJarPath);
            return;
        }

        Path cacheDir = strata.getCacheDir().toPath();
        String outputDirPath = cacheDir.resolve("decompiled").resolve(versionID).toString();

        File outputDir = new File(outputDirPath);
        if (outputDir.exists()) {
            strata.getLogger().warn("Output directory already exists. Skipping decompilation.");
            return;
        }

        DecompilerLogger decompilerLogger = new DecompilerLogger();
        decompilerLogger.setSeverity(IFernflowerLogger.Severity.WARN);

        Decompiler decompiler = Decompiler.builder()
                .inputs(inputFile)
                .output(new DirectoryResultSaver(outputDir))
                .option("--synthetic-not-set", "true")
                .option("--ternary-constant-simplification", "true")
                .option("--include-runtime", "current")
                .option("--decompile-complex-constant-dynamic", "true")
                .option("--indent-string", "    ")
                .option("--decompile-inner", "true")
                .option("--remove-bridge", "true")
                .option("--decompile-generics", "true")
                .option("--ascii-strings", "false")
                .option("--remove-synthetic", "true")
                .option("--include-classpath", "true")
                .option("--inline-simple-lambdas", "true")
                .option("--ignore-invalid-bytecode", "false")
                .option("--bytecode-source-mapping", "true")
                .option("--dump-code-lines", "true")
                .option("--override-annotation", "false")
                .option("--skip-extra-files", "true")
                .logger(decompilerLogger)
                .build();

        decompiler.decompile();

        strata.getLogger().info("Decompilation completed. Output directory: " + outputDirPath);
    }

    class DecompilerLogger extends IFernflowerLogger {

        public DecompilerLogger() {
            super();

            setSeverity(Severity.WARN);
        }

        @Override
        public void writeMessage(String s, Severity severity) {
            LogLevel logLevel = switch (severity) {
                case TRACE -> LogLevel.DEBUG;
                case INFO -> LogLevel.INFO;
                case WARN -> LogLevel.WARN;
                case ERROR -> LogLevel.ERROR;
            };

            if (severity.ordinal() <= 1) {
                // Don't log stack traces for INFO and TRACE messages
                return;
            }

            strata.getLogger().log(logLevel, s);
        }

        @Override
        public void writeMessage(String s, Severity severity, Throwable throwable) {
            LogLevel logLevel = switch (severity) {
                case TRACE -> LogLevel.DEBUG;
                case INFO -> LogLevel.INFO;
                case WARN -> LogLevel.WARN;
                case ERROR -> LogLevel.ERROR;
            };

            if (severity.ordinal() <= 1) {
                // Don't log stack traces for INFO and TRACE messages
                return;
            }

            strata.getLogger().log(logLevel, s, ThrowableProperty.of(throwable));
        }
    }

}
