package benjamin;

import benjamin.command.Command;
import benjamin.parser.Parser;
import benjamin.storage.Storage;
import benjamin.task.TaskList;
import benjamin.ui.Ui;

/**
 * A chatbot that keeps a list of todos, deadlines and events.
 *
 * <p>This class only wires the parts together. Reading and printing belongs to
 * {@link Ui}, understanding input to {@link Parser}, the tasks themselves to
 * {@link TaskList}, and the save file to {@link Storage}.
 *
 * <p>The same object drives both interfaces. {@link #run()} reads from the
 * console, while {@link #getResponse(String)} answers one line at a time for
 * the graphical interface.
 */
public class Benjamin {
    private static final String DATA_FOLDER = "data";
    private static final String DATA_FILE = "benjamin.txt";

    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;
    private boolean isExit;
    private boolean isLastReplyProblem;

    /**
     * Creates a chatbot that saves to the given path, and loads whatever has
     * been saved there before.
     *
     * @param first the first part of the save path, such as {@code data}.
     * @param more the remaining parts, such as {@code benjamin.txt}.
     */
    public Benjamin(String first, String... more) {
        ui = new Ui();
        storage = new Storage(first, more);

        try {
            tasks = new TaskList(storage.load());

            for (String warning : storage.getLoadWarnings()) {
                ui.showError(warning);
            }
        } catch (BenjaminException exception) {
            ui.showLoadingError();
            tasks = new TaskList();
        }
    }

    /** Creates a chatbot that saves to the usual {@code data/benjamin.txt}. */
    public Benjamin() {
        this(DATA_FOLDER, DATA_FILE);
    }

    /** Greets the user, then handles commands until there are none left. */
    public void run() {
        ui.showWelcome();
        ui.flushToConsole();

        while (!isExit && ui.hasNextCommand()) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();

                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (BenjaminException exception) {
                ui.showError(exception.getMessage());
            } finally {
                ui.showLine();
                ui.flushToConsole();
            }
        }

        if (!isExit) {
            // The input ended without a bye command, so sign off anyway.
            ui.showLine();
            ui.showFarewell();
            ui.showLine();
            ui.flushToConsole();
        }

        ui.close();
    }

    /**
     * Returns the greeting shown when the graphical interface opens.
     *
     * <p>Any complaint about the save file is already waiting in the buffer, so
     * it comes out ahead of the greeting just as it does in the text interface.
     */
    public String getWelcome() {
        ui.showGreeting();

        isLastReplyProblem = ui.hadProblem();

        String reply = ui.flush();
        // The GUI puts this straight into a dialog box, so an empty reply would
        // show the user a blank bubble with no explanation.
        assert !reply.isEmpty() : "every command must produce something to show";

        return reply;
    }

    /**
     * Returns the reply to one line of user input.
     *
     * <p>This runs exactly the same parsing and command code as the text
     * interface, so both give the same answers.
     *
     * @param input the line the user typed.
     */
    public String getResponse(String input) {
        try {
            Command command = Parser.parse(input);
            command.execute(tasks, ui, storage);
            isExit = command.isExit();
        } catch (BenjaminException exception) {
            ui.showError(exception.getMessage());
        }

        // Read before flushing, which clears the flag.
        isLastReplyProblem = ui.hadProblem();

        String reply = ui.flush();
        // The GUI puts this straight into a dialog box, so an empty reply would
        // show the user a blank bubble with no explanation.
        assert !reply.isEmpty() : "every command must produce something to show";

        return reply;
    }

    /** Returns true if the most recent reply reported a problem. */
    public boolean isLastReplyProblem() {
        return isLastReplyProblem;
    }

    /** Returns true once a bye command has been carried out. */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Starts the chatbot in text mode, saving to {@code data/benjamin.txt}
     * under whichever folder the program is run from.
     *
     * @param args not used.
     */
    public static void main(String[] args) {
        new Benjamin().run();
    }
}
