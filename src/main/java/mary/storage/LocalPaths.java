package mary.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import mary.exception.MaryException;

/**
 * Confines application-owned files to the process working directory.
 */
public final class LocalPaths {
    private LocalPaths() {
    }

    /**
     * Validates containment before writing, including existing links and Windows junctions.
     * Missing descendants are allowed so callers can create local subdirectories.
     *
     * @param path proposed file or directory.
     * @return normalized absolute path within the working directory.
     * @throws MaryException if containment cannot be established.
     */
    public static Path validate(Path path) throws MaryException {
        Path root = Path.of("").toAbsolutePath().normalize();
        Path target = path.toAbsolutePath().normalize();
        if (!target.startsWith(root)) {
            throw new MaryException("files must stay inside the current working folder; use a local relative path.");
        }
        try {
            Path realRoot = root.toRealPath();
            Path current = root;
            for (Path part : root.relativize(target)) {
                current = current.resolve(part);
                if (Files.isSymbolicLink(current)) {
                    throw new IOException("Symbolic links are not allowed");
                }
                if (Files.exists(current, LinkOption.NOFOLLOW_LINKS)) {
                    if (!current.toRealPath().startsWith(realRoot)) {
                        throw new IOException("Path redirects outside the working folder");
                    }
                } else if (!Files.notExists(current, LinkOption.NOFOLLOW_LINKS)) {
                    throw new IOException("Path cannot be inspected");
                }
            }
            return target;
        } catch (IOException | SecurityException exception) {
            throw new MaryException("cannot safely access " + path
                    + "; use an accessible local path without links outside the working folder.");
        }
    }
}
