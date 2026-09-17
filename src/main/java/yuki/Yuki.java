package yuki;

import java.time.Clock;

import yuki.command.Command;
import yuki.command.RemindersCommand;
import yuki.exception.YukiException;
import yuki.parser.Parser;
import yuki.storage.Storage;
import yuki.task.TaskList;
import yuki.ui.Ui;

/**
 * Runs Yuki's command-line task manager.
 */
public class Yuki {
    /** Handles loading and saving tasks. */
    private final Storage storage;
    /** Contains the tasks managed during this Yuki session. */
    private final TaskList tasks;
    /** Handles all interaction with the user. */
    private final Ui ui;
    /** Supplies the current time for reminder queries. */
    private final Clock clock;
    /** Indicates whether the most recent GUI response reports an invalid command. */
    private boolean isLastResponseError;
    /** Indicates whether the most recent GUI command requests that Yuki exit. */
    private boolean isExitRequested;
    /** Loading problem to display after the user interface is ready. */
    private final String loadingErrorMessage;

    /**
     * Creates Yuki and loads saved tasks before the command loop begins.
     */
    public Yuki() {
        this(new Ui(), new Storage(), Clock.systemDefaultZone());
    }

    /** Creates Yuki using the supplied UI and storage components. */
    Yuki(Ui ui, Storage storage) {
        this(ui, storage, Clock.systemDefaultZone());
    }

    /** Creates Yuki using the supplied components and clock. */
    Yuki(Ui ui, Storage storage, Clock clock) {
        this.ui = ui;
        this.storage = storage;
        this.clock = clock;

        TaskList loadedTasks;
        String loadError = null;
        try {
            loadedTasks = new TaskList(storage.loadTasks());
        } catch (YukiException e) {
            // Keep the chatbot usable even if the saved file is damaged.
            loadedTasks = new TaskList();
            loadError = e.getMessage();
        }
        tasks = loadedTasks;
        loadingErrorMessage = loadError;
    }

    /**
     * Creates a Yuki instance that returns responses without printing them.
     *
     * @return A Yuki instance suitable for a graphical interface.
     */
    public static Yuki createGuiInstance() {
        return new Yuki(Ui.createSilentUi(), new Storage(), Clock.systemDefaultZone());
    }

    /**
     * Greets the user and processes commands until the user exits or input ends.
     */
    public void run() {
        // Greet the user before starting the command loop.
        ui.showWelcome();
        getStartupReminderResponse();

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            try {
                String fullCommand = ui.readCommand();
                Command command = Parser.parse(fullCommand, clock);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (YukiException e) {
                // Show the error and keep the program ready for the next command.
                ui.showError(e.getMessage());
            }
        }
    }

    /**
     * Processes one command and returns Yuki's response.
     *
     * @param input Message entered by the user.
     * @return Yuki's response to the message.
     */
    public String getResponse(String input) {
        isLastResponseError = false;
        isExitRequested = false;
        try {
            Command command = Parser.parse(input, clock);
            command.execute(tasks, ui, storage);
            isExitRequested = command.isExit();
        } catch (YukiException e) {
            isLastResponseError = true;
            ui.showError(e.getMessage());
        }
        return ui.getLastResponse();
    }

    /** Returns Yuki's welcome message. */
    public String getWelcomeResponse() {
        isLastResponseError = false;
        ui.showWelcomeMessage();
        return ui.getLastResponse();
    }

    /** Returns whether the most recent response reports an invalid command. */
    public boolean isLastResponseError() {
        return isLastResponseError;
    }

    /** Returns whether the most recent GUI command requests that Yuki exit. */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Returns a startup reminder response without showing an empty result message.
     *
     * @return The reminder response, or an empty string when no task is due soon.
     */
    public String getStartupReminderResponse() {
        if (loadingErrorMessage != null) {
            isLastResponseError = true;
            ui.showLoadingError(loadingErrorMessage);
            return ui.getLastResponse();
        }
        isLastResponseError = false;
        RemindersCommand command = new RemindersCommand(clock);
        if (!command.executeIfAny(tasks, ui)) {
            return "";
        }
        return ui.getLastResponse();
    }

    /**
     * Starts Yuki.
     *
     * @param args Command-line arguments, which are not used by Yuki.
     */
    public static void main(String[] args) {
        new Yuki().run();
    }

}
