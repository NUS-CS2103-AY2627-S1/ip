package nori.testutil;

import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import nori.Nori;
import nori.ui.GuiUi;

/**
 * A base for tests that run whole Nori sessions.
 *
 * Nori saves to a file as soon as a task changes, so a test that runs a command
 * writes to disk. Each test is therefore given a temporary directory of its own,
 * named through the same system property the application reads, and the previous
 * value is put back afterwards. Without that, a test run would read and overwrite
 * the task file of whoever is running it, and tests would see each other's tasks.
 */
public abstract class IsolatedSessionTest {
    /** System property that redirects storage into an isolated directory. */
    protected static final String STORAGE_DIRECTORY_PROPERTY = "nori.storage.dir";

    /** This test's private storage directory, created and removed by JUnit. */
    @TempDir
    protected Path storageDirectory;

    /** Whatever the property held before this test, so it can be put back. */
    private String previousStorageDirectory;

    /** Creates the fixture for a subclass. */
    protected IsolatedSessionTest() {
    }

    /** Points Nori's storage at this test's own directory. */
    @BeforeEach
    public void redirectStorage() {
        previousStorageDirectory = System.getProperty(STORAGE_DIRECTORY_PROPERTY);
        System.setProperty(STORAGE_DIRECTORY_PROPERTY, storageDirectory.toString());
    }

    /** Puts the storage directory back to whatever it was before this test. */
    @AfterEach
    public void restoreStorage() {
        if (previousStorageDirectory == null) {
            System.clearProperty(STORAGE_DIRECTORY_PROPERTY);
        } else {
            System.setProperty(STORAGE_DIRECTORY_PROPERTY, previousStorageDirectory);
        }
    }

    /**
     * Runs commands in one fresh session and returns the response to the last of them.
     *
     * Each command is trimmed first, because both of Nori's user interfaces trim
     * a line before handing it over and the parser is written to rely on that. A
     * test that skipped the trim would be exercising input no user can produce.
     *
     * @param inputs the commands to run, in order.
     * @return the response the final command produced.
     */
    protected String runCommands(String... inputs) {
        GuiUi guiUi = new GuiUi();
        Nori nori = new Nori(guiUi);

        String response = "";
        for (String input : inputs) {
            nori.executeCommand(input.trim());
            response = guiUi.consumeResponse();
        }
        return response;
    }

    /**
     * Runs commands in one fresh session and returns whether the last was refused.
     *
     * @param inputs the commands to run, in order.
     * @return {@code true} when the final command reported something Nori could not do.
     */
    protected boolean isLastCommandRefused(String... inputs) {
        GuiUi guiUi = new GuiUi();
        Nori nori = new Nori(guiUi);

        for (String input : inputs) {
            nori.executeCommand(input.trim());
            guiUi.consumeResponse();
        }
        return guiUi.isErrorResponse();
    }
}
