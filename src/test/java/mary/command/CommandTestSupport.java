package mary.command;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import mary.storage.Storage;
import mary.task.TaskList;
import mary.ui.Ui;

/**
 * Captures command output and isolates persistence for each test.
 */
abstract class CommandTestSupport {
    protected TaskList tasks;
    protected Storage storage;
    @TempDir
    Path directory;
    private Ui ui;
    private PrintStream originalOutput;
    private PrintStream capturedOutput;
    private ByteArrayOutputStream output;

    /**
     * Creates a fresh task list, isolated storage, and UTF-8 console capture for each test.
     */
    @BeforeEach
    void prepareCommandEnvironment() {
        tasks = new TaskList();
        storage = new Storage(directory.resolve("tasks.txt").toString());
        ui = new Ui();
        originalOutput = System.out;
        output = new ByteArrayOutputStream();
        capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8);
        System.setOut(capturedOutput);
    }

    /**
     * Restores standard output before closing the test-only capture stream.
     */
    @AfterEach
    void restoreConsole() {
        System.setOut(originalOutput);
        capturedOutput.close();
    }

    /**
     * Clears previous output and executes a command against the test fixtures.
     *
     * @param command command to execute.
     * @return captured output with CRLF normalized to LF.
     */
    String execute(Command command) {
        output.reset();
        command.execute(tasks, ui, storage);
        return output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
}
