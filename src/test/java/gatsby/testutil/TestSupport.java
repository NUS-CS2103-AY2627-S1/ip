package gatsby.testutil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;

/** Provides filesystem isolation helpers for tests that exercise Gatsby storage. */
public final class TestSupport {
    private static final Path DATA_DIRECTORY = Path.of("data");

    private TestSupport() {
    }

    /**
     * Copies any existing data directory aside and removes it for an isolated test.
     *
     * @return the temporary backup location, or {@code null} when no data existed
     * @throws IOException if the directory cannot be copied or removed
     */
    public static Path isolateDataDirectory() throws IOException {
        if (!Files.exists(DATA_DIRECTORY)) {
            return null;
        }

        Path backupRoot = Files.createTempDirectory(Path.of("."), ".gatsby-data-backup-");
        copyRecursively(DATA_DIRECTORY, backupRoot.resolve(DATA_DIRECTORY));
        deleteRecursively(DATA_DIRECTORY);
        return backupRoot;
    }

    /**
     * Removes test data and restores the data directory that was present before a test.
     *
     * @param backupRoot the backup returned by {@link #isolateDataDirectory()}
     * @throws IOException if cleanup or restoration fails
     */
    public static void restoreDataDirectory(Path backupRoot) throws IOException {
        deleteRecursively(DATA_DIRECTORY);
        if (backupRoot == null) {
            return;
        }

        Path backedUpData = backupRoot.resolve(DATA_DIRECTORY);
        if (Files.exists(backedUpData)) {
            copyRecursively(backedUpData, DATA_DIRECTORY);
        }
        deleteRecursively(backupRoot);
    }

    /** Writes a complete save file for a storage test. */
    public static void writeSaveFile(String contents) throws IOException {
        Files.createDirectories(DATA_DIRECTORY);
        Files.writeString(DATA_DIRECTORY.resolve("gatsby.txt"), contents);
    }

    private static void copyRecursively(Path source, Path target) throws IOException {
        try (var paths = Files.walk(source)) {
            for (Path path : paths.toList()) {
                Path destination = target.resolve(source.relativize(path));
                if (Files.isDirectory(path)) {
                    Files.createDirectories(destination);
                } else {
                    Files.copy(path, destination, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    private static void deleteRecursively(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }
        try (var paths = Files.walk(path)) {
            for (Path current : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(current);
            }
        }
    }
}
