package zen;

import zen.command.Command;
import zen.command.Parser;
import zen.storage.Storage;
import zen.task.TaskList;
import zen.ui.Ui;

/** Runs the Zen task manager through either its console or graphical interface. */
public class Zen {

    private static final String NAME = "Zen";
    private static final String GREETING = """
               Hello! I'm %s, your calm task companion.
               What can I do for you?
               """.formatted(NAME).strip();

    private final Storage storage;
    private TaskList taskList;
    private final Ui ui;
    private final String initializationError;
    private final boolean isStorageAvailable;
    private boolean isExit;

    /**
     * Creates the application and loads its saved tasks.
     *
     * @param filePath the storage location for tasks.
     */
    public Zen(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);

        String loadingError = "";
        try {
            taskList = storage.load();
        } catch (ZenException e) {
            ui.printErrorMessage(e.getMessage());
            taskList = new TaskList();
            loadingError = e.getMessage();
        }
        initializationError = loadingError;
        isStorageAvailable = loadingError.isEmpty();
        isExit = !isStorageAvailable;
    }

    /** Runs the command-processing loop until the user exits. */
    public void run() {
        if (!isStorageAvailable) {
            return;
        }

        ui.printGreeting(GREETING);
        while (!isExit) {
            ui.clearResponse();
            try {
                String fullCommand = ui.readCommand();
                ui.printDivider();
                Command command = Parser.parse(fullCommand);
                command.execute(taskList, ui, storage);
                isExit = command.isExit();
            } catch (ZenException e) {
                ui.echo(e.getMessage());
            } finally {
                ui.printDivider();
            }
        }
    }

    /**
     * Starts the application using its default task storage file.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Zen("data/task_list.txt").run();
    }

    /**
     * Processes one command received from the graphical user interface.
     *
     * @param input command entered by the user
     * @return the chatbot's response to the command
     */
    public String getResponse(String input) {
        ui.clearResponse();
        if (!isExit) {
            try {
                ui.printDivider();
                Command command = Parser.parse(input);
                command.execute(taskList, ui, storage);
                isExit = command.isExit();
            } catch (ZenException e) {
                ui.echo(e.getMessage());
            } finally {
                ui.printDivider();
            }
        }
        return ui.getResponse();
    }

    /**
     * Enables or disables formatted terminal output for debugging.
     *
     * @param isTerminalOutputEnabled whether terminal output is enabled
     */
    public void setTerminalOutputEnabled(boolean isTerminalOutputEnabled) {
        ui.setTerminalOutputEnabled(isTerminalOutputEnabled);
    }

    /**
     * Returns the greeting shared by the graphical and console interfaces.
     *
     * @return application greeting
     */
    public String getGreeting() {
        return GREETING;
    }

    /**
     * Returns the message explaining why stored tasks could not be loaded.
     *
     * @return initialization error, or an empty string when loading succeeds
     */
    public String getInitializationError() {
        return initializationError;
    }

    /**
     * Returns whether the chatbot has processed an exit command.
     *
     * @return true if the chatbot no longer accepts commands
     */
    public boolean hasExited() {
        return isExit;
    }
}
