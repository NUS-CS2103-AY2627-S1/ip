package alzara;

import alzara.command.Command;
import alzara.parser.CommandParser;
import alzara.storage.Storage;
import alzara.task.TaskList;
import alzara.ui.Ui;

/**
 * The Alzara chatbot: sets up its collaborators once, then either runs the
 * console read-command-execute loop until the user says {@code bye}, or (for
 * the JavaFX GUI) answers one command at a time via {@link #getResponse}.
 */
public class Alzara {
    private final Ui ui;
    private final Storage storage;
    private TaskList memory;
    private boolean isExit = false;

    /**
     * Creates an {@link Alzara} for the console.
     */
    public Alzara() {
        this(false);
    }

    /**
     * Creates an {@link Alzara} for either the console or the JavaFX GUI,
     * using the default {@code data} save directory. The saved task list is
     * not loaded yet - see {@link #loadMemory()}.
     *
     * @param isGuiMode if true, {@link #getResponse} returns each reply as a
     *         string instead of the {@link Ui} printing it to the console
     */
    public Alzara(boolean isGuiMode) {
        this(isGuiMode, new Storage());
    }

    /**
     * Creates an {@link Alzara} for either the console or the JavaFX GUI,
     * using the given {@link Storage}, e.g. one pointed at a JUnit
     * {@code @TempDir} in a test. The saved task list is not loaded yet -
     * see {@link #loadMemory()}.
     *
     * @param isGuiMode if true, {@link #getResponse} returns each reply as a
     *         string instead of the {@link Ui} printing it to the console
     * @param storage where the task list is loaded from and saved to
     */
    public Alzara(boolean isGuiMode, Storage storage) {
        this.ui = new Ui(isGuiMode);
        this.storage = storage;
    }

    /**
     * Loads the saved task list, reporting any corrupted save-file lines
     * along the way. Called once the welcome message has already been
     * shown, by both {@link #run()} and {@link #getWelcomeMessage()}, so
     * those load-time reports never print ahead of the welcome banner.
     */
    private void loadMemory() {
        this.memory = new TaskList(storage.load(ui));
    }

    /**
     * Runs the read-command-execute loop until the user says {@code bye}.
     */
    public void run() {
        ui.showWelcome();
        loadMemory();

        while (true) {
            String command = ui.readCommand();
            ui.showLine();

            try {
                Command userCommand = CommandParser.parse(command);
                userCommand.execute(memory, ui, storage);
                if (userCommand.isExit()) {
                    return;
                }
            } catch (AlzaraException exception) {
                ui.showError(exception.getMessage());
            }
        }
    }

    /**
     * Parses and executes a single command typed into the GUI, returning
     * Alzara's reply as plain text.
     *
     * @param input the raw command text typed by the user
     * @return Alzara's reply to that command
     */
    public String getResponse(String input) {
        try {
            Command userCommand = CommandParser.parse(input);
            userCommand.execute(memory, ui, storage);
            isExit = userCommand.isExit();
        } catch (AlzaraException exception) {
            ui.showError(exception.getMessage());
        }
        return ui.getAndClearResponse();
    }

    /**
     * Returns true if the most recent {@link #getResponse} call executed a
     * command that should end the program, e.g. {@code bye}.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Returns true if the most recent {@link #getResponse} call failed with
     * an {@link AlzaraException}, so the GUI can highlight the reply as an
     * error instead of a normal reply.
     */
    public boolean isLastResponseError() {
        return ui.isLastResponseError();
    }

    /**
     * Returns Alzara's welcome message as plain text, for the GUI to show
     * once when the window opens. Also loads the saved task list - see
     * {@link #loadMemory()}.
     */
    public String getWelcomeMessage() {
        ui.showWelcome();
        loadMemory();
        return ui.getAndClearResponse();
    }

    /**
     * Starts the Alzara chatbot application.
     *
     * @param args command-line arguments supplied when the application starts
     */
    public static void main(String[] args) {
        new Alzara().run();
    }
}
