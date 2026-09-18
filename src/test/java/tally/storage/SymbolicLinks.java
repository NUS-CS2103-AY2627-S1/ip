package tally.storage;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Makes the symbolic links the storage tests need, where the file system makes any.
 *
 * <p>Shared by the tests that load and the tests that save, since both have to ask the
 * same question before they can ask their own.
 */
final class SymbolicLinks {
    /** Prevents anyone making one: every method here is static. */
    private SymbolicLinks() {
    }

    /**
     * Makes a symbolic link, skipping the test where the file system has no such thing.
     *
     * <p>Whether links can be made at all is asked first, with a link of its own that is
     * made and removed again. Only that question can be answered by skipping: once it is
     * known they work, a link that will not be made is a fault in the test, and reporting
     * it as an environment without symbolic links would hide it.
     *
     * @param folder somewhere to try the question out in.
     * @param link where the link goes.
     * @param target what it points at, which need not exist.
     * @throws IOException if the link cannot be made on a file system that makes them.
     */
    static void makeOrSkip(Path folder, Path link, Path target) throws IOException {
        Path probe = folder.resolve("probe.link");
        try {
            Files.createSymbolicLink(probe, folder.resolve("probe.target"));
            Files.delete(probe);
        } catch (UnsupportedOperationException | IOException exception) {
            // Windows refuses this to a user without the privilege for it, which is an
            // environment these tests cannot run in rather than a failure of the code.
            assumeTrue(false, "this file system will not make a symbolic link: " + exception);
        }
        Files.createSymbolicLink(link, target);
    }
}
