package anders;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import anders.collection.TaskList;
import anders.command.Command;
import anders.parser.Parser;
import anders.storage.Storage;
import anders.ui.Ui;

/**
 * Runs the Anders chatbot and persists its tasks between sessions.
 *
 * <p>Task changes are persisted so the next session can continue the same trail.</p>
 */
public class Anders {
    private final Storage storage;
    private final Ui ui;
    private final TaskList tasks;

    /** Creates an Anders session using the existing storage file. */
    public Anders() {
        this(new Storage("data/anders.txt"));
    }

    /** Creates a session with supplied storage so tests can use an isolated task file. */
    Anders(Storage storage) {
        this.storage = storage;
        ui = new Ui();
        tasks = new TaskList(storage.load());
    }

    /** Returns any warning that the GUI should display after its welcome message. */
    public String getStartupWarning() {
        return storage.getLoadWarning();
    }

    /** Processes one command and returns the output for display in a GUI. */
    public String getResponse(String input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        try {
            System.setOut(new PrintStream(output));
            Command command = Parser.parse(input);
            command.execute(tasks, ui, storage);
        } catch (AndersException e) {
            ui.showError(e.getMessage());
        } finally {
            System.setOut(originalOutput);
        }
        return output.toString().trim();
    }

    /** Starts Anders and processes commands until the user exits or input ends. */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage("data/anders.txt");

        TaskList tasks = new TaskList(storage.load());

        ui.showWelcome();
        if (!storage.getLoadWarning().isEmpty()) {
            ui.showError(storage.getLoadWarning());
        }
        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            try {
                String fullCommand = ui.readCommand();
                ui.showSeparator();
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (AndersException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showSeparator();
            }
        }
    }

}
