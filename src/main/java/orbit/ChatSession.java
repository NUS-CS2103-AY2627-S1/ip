package orbit;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import orbit.exception.OrbitException;
import orbit.parser.Parser;
import orbit.storage.Storage;
import orbit.task.TaskList;
import orbit.ui.Ui;

/**
 * Adapts Orbit's existing command responses for an event-driven conversation.
 * Each session owns its tasks and output buffer; it does not redirect System.out.
 */
public class ChatSession {
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final Orbit orbit;
    private final String greeting;
    private boolean isExitRequested;

    /**
     * Loads a conversation's tasks from the given data file.
     *
     * @param dataFile path to the same storage format used by the console interface
     */
    public ChatSession(Path dataFile) {
        Ui ui = new Ui(InputStream.nullInputStream(), new PrintStream(output, true, StandardCharsets.UTF_8));
        Storage storage = new Storage(dataFile);
        TaskList tasks;
        ui.showGreeting();
        try {
            tasks = new TaskList(storage.load());
        } catch (OrbitException exception) {
            ui.showError(exception.getMessage());
            tasks = new TaskList();
        }
        greeting = readOutput();
        orbit = new Orbit(ui, storage, new Parser(), tasks);
    }

    /**
     * Returns the greeting and any startup storage warning.
     *
     * @return initial conversation text
     */
    public String getGreeting() {
        return greeting;
    }

    /**
     * Executes a command and returns only its response, without console separators.
     *
     * @param input user command
     * @return Orbit's response
     * @throws IllegalStateException if the conversation has already ended
     */
    public String getResponse(String input) {
        if (isExitRequested) {
            throw new IllegalStateException("This conversation has ended.");
        }
        isExitRequested = !orbit.processCommand(input);
        return readOutput();
    }

    /**
     * Indicates whether a valid bye command ended this conversation.
     *
     * @return true after a successful exit command
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    private String readOutput() {
        String response = output.toString(StandardCharsets.UTF_8).strip();
        output.reset();
        return response;
    }
}
