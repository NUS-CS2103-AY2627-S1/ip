package duke;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

import duke.command.Command;
import duke.exception.GaryException;
import duke.parser.Parser;
import duke.storage.Storage;
import duke.task.Task;
import duke.task.TaskList;
import duke.ui.GuiUi;
import duke.ui.Ui;

/**
 * Main entry point for the Duke task-tracking application.
 *
 * <p>Reads user commands from standard input, executes them, and saves tasks to disk.</p>
 */
public class Duke {
    private final TaskList tasks;
    private final Ui ui;
    private final Parser parser;
    private final String startupError;

    /**
     * Creates a Duke application using the given file path for storage.
     *
     * @param filePath File path to load/save tasks.
     */
    public Duke(String filePath) {
        this.ui = new Ui();
        this.parser = new Parser();

        Storage storage = null;
        ArrayList<Task> loadedTasks = new ArrayList<>();
        String loadingError = null;
        try {
            storage = new Storage(filePath);
            loadedTasks = storage.load();
        } catch (GaryException e) {
            loadingError = e.getMessage()
                    + " Gary started with an empty list; changes will not be saved in this session.";
        }

        this.startupError = loadingError;
        this.tasks = new TaskList(loadedTasks);
        if (storage != null && startupError == null) {
            this.tasks.setStorage(storage);
        }
    }

    /**
     * Starts the main input loop for the application.
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);
        ui.showWelcome();
        if (startupError != null) {
            ui.showError(startupError);
        }

        boolean isExit = false;
        while (!isExit) {
            try {
                if (!scanner.hasNextLine()) {
                    isExit = true;
                    tasks.save();
                    continue;
                }

                String fullCommand = scanner.nextLine().trim();
                if (fullCommand.isEmpty()) {
                    ui.showEmptyInputError();
                    continue;
                }

                Command command = parser.parse(fullCommand);

                assert command != null : "Parser must return a command for non-blank input";
                command.execute(tasks, ui);

                isExit = command.isExit();
            } catch (GaryException e) {
                ui.showError(e.getMessage());
            }
        }
        scanner.close();
    }

    /**
     * Processes one line of user input and returns Duke's response as a string.
     *
     * <p>This method exists to support the JavaFX GUI. The CLI path should continue to use {@link #run()}.</p>
     *
     * @param input User input string.
     * @return Response to show to the user.
     */
    public String getResponse(String input) {
        return getCommandResponse(input).text();
    }

    /**
     * Returns the greeting displayed when the GUI opens.
     *
     * @return Welcome message without the terminal-only banner.
     */
    public String getWelcomeMessage() {
        return ui.getWelcomeMessage();
    }

    /**
     * Returns a storage warning detected while starting the application, if any.
     *
     * @return Optional user-facing startup error.
     */
    public Optional<String> getStartupError() {
        return Optional.ofNullable(startupError);
    }

    /**
     * Processes one line of user input and returns its text and display type.
     *
     * @param input User input string.
     * @return Response text together with its error and exit states.
     */
    public CommandResponse getCommandResponse(String input) {
        String trimmedInput = input == null ? "" : input.trim();
        if (trimmedInput.isEmpty()) {
            return new CommandResponse("Please enter a command.", true, false);
        }

        GuiUi guiUi = new GuiUi();
        boolean shouldExit = false;
        try {
            Command command = parser.parse(trimmedInput);

            assert command != null : "Parser must return a command for non-blank input";
            command.execute(tasks, guiUi);
            shouldExit = command.isExit();
        } catch (GaryException e) {
            guiUi.showError(e.getMessage());
        }

        boolean isError = guiUi.hasError();
        return new CommandResponse(guiUi.consumeOutput(), isError, shouldExit);
    }

    public static void main(String[] args) {
        new Duke("data/duke.txt").run();
    }
}
