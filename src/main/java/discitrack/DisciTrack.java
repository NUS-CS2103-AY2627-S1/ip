package discitrack;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import discitrack.command.Command;
import discitrack.exception.DisciTrackException;
import discitrack.exception.SaveException;
import discitrack.parser.Parser;
import discitrack.storage.Storage;
import discitrack.task.Task;
import discitrack.task.TaskList;
import discitrack.ui.Ui;
/**
 * Runs the DisciTrack chatbot application.
 */
public class DisciTrack {
    private static final String FILE_PATH = "data/discitrack.txt";
    private static final String LOAD_BLOCKED_MESSAGE = "UHOH! Task commands are disabled because your saved data "
            + "could not be loaded. Fix the file and restart.";

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private boolean shouldExit;
    private String loadError;
    private boolean hasResponseError;
    private boolean hasStorageError;

    /**
     * Creates a DisciTrack application for use by the graphical user interface.
     */
    public DisciTrack() {
        this(FILE_PATH, false);
    }

    /**
     * Creates a DisciTrack application that stores tasks at the given file path.
     *
     * @param filePath path to the file used for saving and loading tasks.
     */
    public DisciTrack(String filePath) {
        this(filePath, true);
    }

    private DisciTrack(String filePath, boolean shouldPrintResponses) {
        ui = new Ui(shouldPrintResponses);
        storage = new Storage(filePath);
        tasks = new TaskList(loadTasks());
    }

    public boolean hasStorageError() {
        return hasStorageError || loadError != null;
    }

    public String getLastResponse() {
        return ui.getLastResponse();
    }

    /**
     * Indicates whether the GUI should present the latest response as an error.
     *
     * @return whether a command or initial load failed.
     */
    public boolean hasResponseError() {
        return hasResponseError;
    }

    public boolean hasLoadError() {
        return loadError != null;
    }

    /**
     * Returns a read-only list for rendering the task board; commands own task mutations.
     *
     * @return tasks in their original command-number order.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks.asList());
    }

    /**
     * Finds board results using the same matching rules as the find command.
     *
     * @param keyword description text to find.
     * @return matching tasks in their original order.
     */
    public List<Task> getMatchingTasks(String keyword) {
        return List.copyOf(tasks.findTasksByKeyword(keyword));
    }

    /**
     * Returns the date command's matches for the GUI board.
     *
     * @param date date to match.
     * @return matching deadlines and events.
     */
    public List<Task> getTasksOnDate(LocalDate date) {
        return List.copyOf(tasks.findTasksByDate(date));
    }

    /**
     * Starts the DisciTrack application.
     *
     * @param args command line arguments supplied to the program.
     */
    public static void main(String[] args) {
        new DisciTrack(FILE_PATH).run();
    }

    /**
     * Reads user commands until the user exits the application.
     */
    public void run() {
        if (loadError == null) {
            ui.showGreeting();
        } else {
            ui.showError(getGreeting());
        }

        while (true) {
            String command = ui.readCommand();

            if (executeCommand(command)) {
                break;
            }
        }
    }

    /**
     * Returns the greeting shown when the graphical user interface opens.
     *
     * @return the DisciTrack greeting.
     */
    public String getGreeting() {
        if (loadError != null) {
            return "UHOH! Could not load your tasks: " + loadError + "." + System.lineSeparator()
                    + "Task commands are disabled to protect your saved data. Fix the file and restart.";
        }
        return ui.getGreeting();
    }

    /**
     * Processes one user command and returns the response for the graphical interface.
     *
     * @param input the command entered by the user.
     * @return the response produced after processing the command.
     */
    public String getResponse(String input) {
        shouldExit = executeCommand(input);

        return ui.getLastResponse();
    }

    private boolean executeCommand(String input) {
        hasResponseError = false;
        hasStorageError = false;
        if (loadError != null && !input.trim().equals("help") && !input.trim().equals("bye")) {
            hasResponseError = true;
            ui.showError(LOAD_BLOCKED_MESSAGE);
            return false;
        }
        try {
            Command parsedCommand = Parser.parse(input);
            parsedCommand.execute(tasks, ui, storage);
            return parsedCommand.isExit();
        } catch (DisciTrackException e) {
            hasResponseError = true;
            hasStorageError = e instanceof SaveException;
            ui.showError(e.getMessage());
        } catch (IOException e) {
            hasResponseError = true;
            hasStorageError = true;
            ui.showSaveError();
        }

        return false;
    }

    /**
     * Returns whether the latest graphical-interface command should close the application.
     *
     * @return true if the latest command was an exit command.
     */
    public boolean shouldExit() {
        return shouldExit;
    }

    /**
     * Loads the saved tasks, or starts with an empty list if the data file does not exist.
     *
     * @return the tasks loaded from storage.
     */
    private ArrayList<Task> loadTasks() {
        try {
            return new ArrayList<>(storage.load());
        } catch (IOException e) {
            loadError = e.getMessage();
            return new ArrayList<>();
        }
    }
}
