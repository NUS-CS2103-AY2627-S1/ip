package mary;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import javafx.application.Application;
import mary.exception.MaryException;
import mary.storage.LocalPaths;
import mary.ui.MaryApplication;

/**
 * Launches the graphical app without extending Application, allowing a runnable fat JAR.
 */
public class Launcher {
    /**
     * Opens JavaFX by default, or the console when --cli is supplied.
     *
     * @param args optional --cli switch.
     */
    public static void main(String[] args) {
        if (Arrays.asList(args).contains("--cli")) {
            Mary.main(args);
        } else {
            try {
                configureLocalCaches();
            } catch (MaryException exception) {
                System.err.println("Error: " + exception.getMessage());
                return;
            }
            Application.launch(MaryApplication.class, args);
        }
    }

    /**
     * Prepares local JavaFX and fallback temporary directories before toolkit startup.
     * Overrides external cache settings instead of permitting writes outside this folder.
     *
     * @throws MaryException if the local cache cannot be safely prepared.
     */
    public static void configureLocalCaches() throws MaryException {
        Path cache = prepareDirectory(Path.of(".mary", "javafx-cache"));
        Path temporary = prepareDirectory(Path.of(".mary", "tmp"));
        System.setProperty("javafx.cachedir", cache.toString());
        System.setProperty("java.io.tmpdir", temporary.toString());
    }

    /**
     * Checks existing cache entries and verifies local write access without a home-folder fallback.
     */
    private static Path prepareDirectory(Path path) throws MaryException {
        Path directory = LocalPaths.validate(path);
        try {
            Files.createDirectories(directory);
            LocalPaths.validate(directory);
            try (var entries = Files.walk(directory)) {
                for (Path entry : entries.toList()) {
                    LocalPaths.validate(entry);
                }
            }
            Path probe = Files.createTempFile(directory, ".write-check-", ".tmp");
            Files.delete(probe);
            return directory;
        } catch (IOException | UncheckedIOException | SecurityException exception) {
            throw new MaryException("cannot create a local cache in " + directory
                    + "; run MARY from a writable folder. No home-folder fallback will be used.");
        }
    }
}
