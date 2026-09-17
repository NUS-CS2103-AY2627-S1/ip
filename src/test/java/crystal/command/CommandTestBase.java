package crystal.command;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import crystal.storage.Storage;
import crystal.ui.Ui;

/**
 * Provides isolated console output and task storage for command tests.
 */
abstract class CommandTestBase {
    @TempDir
    protected Path tempDirectory;

    private PrintStream originalOutput;
    private ByteArrayOutputStream capturedOutput;

    /** Redirects standard output so command responses can be asserted in isolation. */
    @BeforeEach
    public void redirectStandardOutput() {
        originalOutput = System.out;
        capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));
    }

    /** Restores the process output stream after each command test. */
    @AfterEach
    public void restoreStandardOutput() {
        System.setOut(originalOutput);
    }

    /**
     * Creates a UI writing into this test's captured output.
     *
     * @return isolated console UI.
     */
    protected Ui createUi() {
        return new Ui();
    }

    /**
     * Creates storage backed by a temporary data file.
     *
     * @return isolated task storage.
     */
    protected Storage createStorage() {
        return new Storage(tempDirectory.resolve("data").resolve("crystal.txt"));
    }

    /**
     * Returns all console text emitted since the test began.
     *
     * @return normalized captured output.
     */
    protected String getOutput() {
        return capturedOutput.toString(StandardCharsets.UTF_8)
                .replace("\r\n", "\n");
    }
}
