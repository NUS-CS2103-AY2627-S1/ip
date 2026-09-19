package yawned;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;

/** Resolves the standard storage paths for a packaged or development build. */
final class ApplicationPaths {
    private static final Path DEVELOPMENT_TASK_FILE = Path.of("data", "Yawned.txt");

    private ApplicationPaths() {
    }

    /**
     * Returns the task file beside the running JAR, or the development file
     * relative to the current directory when classes are run directly.
     *
     * @return Path used to persist tasks.
     */
    static Path taskFile() {
        CodeSource codeSource = ApplicationPaths.class.getProtectionDomain().getCodeSource();
        if (codeSource == null) {
            return DEVELOPMENT_TASK_FILE;
        }

        try {
            return taskFileForCodeSource(Paths.get(codeSource.getLocation().toURI()));
        } catch (URISyntaxException exception) {
            return DEVELOPMENT_TASK_FILE;
        }
    }

    /**
     * Returns the storage path appropriate for a code-source location.
     *
     * @param codeSourceLocation Directory of compiled classes or packaged JAR.
     * @return Path used to persist tasks.
     */
    static Path taskFileForCodeSource(Path codeSourceLocation) {
        if (!Files.isRegularFile(codeSourceLocation)) {
            return DEVELOPMENT_TASK_FILE;
        }

        Path jarDirectory = codeSourceLocation.getParent();
        return jarDirectory == null ? DEVELOPMENT_TASK_FILE : jarDirectory.resolve(DEVELOPMENT_TASK_FILE);
    }
}
