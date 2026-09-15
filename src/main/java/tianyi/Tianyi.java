package tianyi;

import tianyi.command.Command;
import tianyi.command.CommandParser;
import tianyi.storage.Storage;
import tianyi.task.TaskList;
import tianyi.ui.Ui;

/**
 * Runs the Tianyi chatbot application.
 */
public class Tianyi {
    /**
     * Default location used to persist tasks in console mode.
     */
    private static final String DATA_FILE_PATH = "Data/tianyi.txt";

    /**
     * Banner and greeting displayed when Tianyi starts.
     */
    private static final String WELCOME_MESSAGE =
            " _____ _                   _\n"
                    + "|_   _(_) __ _ _ __  _   _(_)\n"
                    + "  | | | |/ _` | '_ \\| | | | |\n"
                    + "  | | | | (_| | | | | |_| | |\n"
                    + "  |_| |_|\\__,_|_| |_|\\__, |_|\n"
                    + "                     |___/\n"
                    + "Hi, I'm Tianyi.\n"
                    + "It's good to see you!\n"
                    + "What can I do for you?";

    private static final String DATA_ERROR_MESSAGE =
            "Oops! The saved data file is damaged and cannot be loaded.\n"
                    + "Please enter [bye] to exit Tianyi. The damaged data will be cleared.";

    private static final String DATA_CLEAR_ERROR_MESSAGE =
            "Oops! The damaged data file could not be cleared.\n"
                    + "Please close Tianyi and remove the data file manually.";

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final CommandParser parser;

    /**
     * Indicates that commands must remain blocked until the damaged data file is cleared on exit.
     */
    private final boolean hasDataError;

    /**
     * Creates a Tianyi application that uses the default data file.
     */
    public Tianyi() {
        this(DATA_FILE_PATH);
    }

    /**
     * Creates a Tianyi application that persists tasks at the specified path.
     *
     * @param filePath Path of the file used to load and save tasks.
     */
    public Tianyi(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        parser = new CommandParser();

        TaskList loadedTasks;
        boolean didEncounterDataError = false;

        try {
            loadedTasks = new TaskList(storage.load());
        } catch (TianyiException e) {
            loadedTasks = new TaskList();
            didEncounterDataError = true;
        }

        tasks = loadedTasks;
        hasDataError = didEncounterDataError;
    }

    /**
     * Returns Tianyi's welcome message.
     *
     * @return Welcome banner and greeting.
     */
    public String getWelcomeMessage() {
        return WELCOME_MESSAGE;
    }

    /**
     * Processes one user command and returns a response suitable for display.
     *
     * @param input Complete command entered by the user.
     * @return Structured response containing display content and command status.
     */
    public Response getResponse(String input) {
        if (input.isBlank()) {
            return new Response("", "");
        }

        if (hasDataError) {
            if (!isExitCommand(input)) {
                return Response.error(DATA_ERROR_MESSAGE);
            }

            try {
                storage.save(tasks.getTasks());
            } catch (TianyiException e) {
                return Response.error(DATA_CLEAR_ERROR_MESSAGE);
            }
        }

        try {
            Command command = parser.parse(input, tasks);

            return command.execute(tasks, storage);
        } catch (TianyiException e) {
            return Response.error("Oops! " + e.getMessage());
        }
    }

    /**
     * Reports whether the input is the exact exit command, ignoring case and surrounding whitespace.
     *
     * @param input Complete command entered by the user.
     * @return Whether the input requests application shutdown.
     */
    private boolean isExitCommand(String input) {
        return input.trim().equalsIgnoreCase("bye");
    }

    /**
     * Starts the command loop and processes input until the user exits or input ends.
     */
    public void run() {
        ui.showWelcome(WELCOME_MESSAGE);
        boolean isExit = false;

        while (!isExit && ui.hasNextCommand()) {
            String fullCommand = ui.readCommand();

            if (fullCommand.isBlank()) {
                continue;
            }

            Response response = getResponse(fullCommand);
            ui.showResponse(response.getFullMessage());
            isExit = response.isExit();
        }
    }

    /**
     * Launches Tianyi using the default data file.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Tianyi().run();
    }
}
